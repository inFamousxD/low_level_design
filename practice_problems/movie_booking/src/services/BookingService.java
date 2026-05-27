package services;

import enums.BookingStatus;
import enums.HoldStatus;
import models.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BookingService {

    private static final Duration HOLD_TTL = Duration.ofMinutes(10);

    private final ShowService showService;
    private final Map<String, SeatHold> holds   = new ConcurrentHashMap<>();
    private final Map<String, Booking>  bookings = new ConcurrentHashMap<>();

    // Background sweeper releases ShowSeats whose hold has expired
    private final ScheduledExecutorService sweeper =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "hold-expiry-sweeper");
                t.setDaemon(true);
                return t;
            });

    private BookingService() {
        this.showService = ShowService.getInstance();
        sweeper.scheduleAtFixedRate(this::sweepExpiredHolds, 1, 1, TimeUnit.MINUTES);
    }

    private static class Holder {
        private static final BookingService INSTANCE = new BookingService();
    }

    public static BookingService getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Temporarily holds the given ShowSeat IDs for the user.
     * The hold expires after HOLD_TTL; seats are freed automatically by the sweeper.
     *
     * @param showSeatIds IDs from {@link ShowService#getShowSeats(String)}
     * @throws IllegalStateException if any seat is already held or booked
     */
    public SeatHold holdSeats(String showId, List<String> showSeatIds, User user) {
        String holdId = "H-" + UUID.randomUUID();
        Instant expiresAt = Instant.now().plus(HOLD_TTL);

        // ShowService acquires the per-show lock for atomic seat validation + hold
        List<ShowSeat> held = showService.holdSeats(showId, showSeatIds, holdId, expiresAt);

        SeatHold seatHold = new SeatHold(holdId, user, showService.getShow(showId), held, expiresAt);
        holds.put(holdId, seatHold);
        return seatHold;
    }

    /**
     * Confirms a hold into a real booking and marks all seats as BOOKED.
     * Synchronized on the hold object so that concurrent confirm / cancel / sweep
     * calls for the same hold are serialized.
     *
     * @throws IllegalStateException if the hold has expired or is no longer active
     */
    public Booking confirmBooking(String holdId) {
        SeatHold hold = getHold(holdId);
        synchronized (hold) {
            if (hold.isExpired() || hold.getStatus() != HoldStatus.ACTIVE) {
                if (hold.getStatus() == HoldStatus.ACTIVE) {
                    hold.setStatus(HoldStatus.EXPIRED);
                    showService.releaseSeats(hold.getShow().getId(), hold.getSeats());
                }
                throw new IllegalStateException(
                        "Hold " + holdId + " is no longer active: " + hold.getStatus());
            }

            showService.bookSeats(hold.getShow().getId(), hold.getSeats());
            hold.setStatus(HoldStatus.CONFIRMED);

            double total = hold.getSeats().stream().mapToDouble(ShowSeat::getPrice).sum();
            String bookingId = "B-" + UUID.randomUUID();
            Booking booking = new Booking(bookingId, hold.getUser(), hold.getShow(),
                                           hold.getSeats(), total);
            bookings.put(bookingId, booking);
            return booking;
        }
    }

    /**
     * Cancels an active hold and immediately frees the seats.
     */
    public void cancelHold(String holdId) {
        SeatHold hold = getHold(holdId);
        synchronized (hold) {
            if (hold.getStatus() != HoldStatus.ACTIVE) {
                throw new IllegalStateException(
                        "Hold " + holdId + " is not active: " + hold.getStatus());
            }
            hold.setStatus(HoldStatus.CANCELLED);
            showService.releaseSeats(hold.getShow().getId(), hold.getSeats());
        }
    }

    /**
     * Cancels a confirmed booking and releases the seats back to AVAILABLE.
     */
    public void cancelBooking(String bookingId) {
        Booking booking = getBooking(bookingId);
        synchronized (booking) {
            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                throw new IllegalStateException(
                        "Booking " + bookingId + " is not confirmed: " + booking.getStatus());
            }
            booking.setStatus(BookingStatus.CANCELLED);
            showService.releaseSeats(booking.getShow().getId(), booking.getSeats());
        }
    }

    public SeatHold getHold(String holdId) {
        SeatHold h = holds.get(holdId);
        if (h == null) throw new IllegalArgumentException("Hold not found: " + holdId);
        return h;
    }

    public Booking getBooking(String bookingId) {
        Booking b = bookings.get(bookingId);
        if (b == null) throw new IllegalArgumentException("Booking not found: " + bookingId);
        return b;
    }

    /** Periodic cleanup: release ShowSeats whose TTL has passed. */
    private void sweepExpiredHolds() {
        holds.values().forEach(hold -> {
            synchronized (hold) {
                if (hold.getStatus() == HoldStatus.ACTIVE && hold.isExpired()) {
                    hold.setStatus(HoldStatus.EXPIRED);
                    showService.releaseSeats(hold.getShow().getId(), hold.getSeats());
                }
            }
        });
    }

    public void shutdown() {
        sweeper.shutdown();
    }
}
