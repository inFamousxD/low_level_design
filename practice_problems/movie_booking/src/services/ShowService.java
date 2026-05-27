package services;

import enums.SeatType;
import models.*;

import java.time.LocalDate;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class ShowService {

    // showId → Show
    private final Map<String, Show> shows = new ConcurrentHashMap<>();

    // showId → (showSeatId → ShowSeat)
    private final Map<String, Map<String, ShowSeat>> showSeats = new ConcurrentHashMap<>();

    // Per-show lock — prevents concurrent seat-state mutations for the same show
    private final Map<String, ReentrantLock> showLocks = new ConcurrentHashMap<>();

    private final AtomicInteger showSeatIdCounter = new AtomicInteger(1);

    private static final Map<SeatType, Double> PRICE_MULTIPLIER = Map.of(
            SeatType.REGULAR, 1.0,
            SeatType.PREMIUM, 1.5,
            SeatType.VIP,     2.5
    );

    private ShowService() {}

    private static class Holder {
        private static final ShowService INSTANCE = new ShowService();
    }

    public static ShowService getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Registers a show and auto-creates one ShowSeat per physical seat on the screen.
     * @param basePrice base ticket price (applied to REGULAR seats; others are scaled up)
     */
    public synchronized Show addShow(Show show, double basePrice) {
        shows.put(show.getId(), show);
        showLocks.put(show.getId(), new ReentrantLock());

        Map<String, ShowSeat> seatMap = new ConcurrentHashMap<>();
        for (Seat seat : show.getScreen().getSeats()) {
            double price = basePrice * PRICE_MULTIPLIER.getOrDefault(seat.getType(), 1.0);
            String ssId = "SS" + showSeatIdCounter.getAndIncrement();
            ShowSeat ss = new ShowSeat(ssId, show, seat, price);
            seatMap.put(ssId, ss);
        }
        showSeats.put(show.getId(), seatMap);
        return show;
    }

    public Show getShow(String showId) {
        Show s = shows.get(showId);
        if (s == null) throw new IllegalArgumentException("Show not found: " + showId);
        return s;
    }

    public Collection<Show> getAllShows() {
        return Collections.unmodifiableCollection(shows.values());
    }

    public List<Show> getShowsByMovie(String movieId) {
        return shows.values().stream()
                .filter(s -> s.getMovie().getId().equals(movieId))
                .collect(Collectors.toList());
    }

    public List<Show> getShowsByMovieAndCity(String movieId, String city, LocalDate date) {
        return shows.values().stream()
                .filter(s -> s.getMovie().getId().equals(movieId))
                .filter(s -> s.getScreen().getTheater().getCity().equalsIgnoreCase(city))
                .filter(s -> s.getStartTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<Show> getShowsByTheater(String theaterId, LocalDate date) {
        return shows.values().stream()
                .filter(s -> s.getScreen().getTheater().getId().equals(theaterId))
                .filter(s -> s.getStartTime().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    /** Returns all ShowSeats for a show (snapshot). */
    public List<ShowSeat> getShowSeats(String showId) {
        return new ArrayList<>(getShowSeatMap(showId).values());
    }

    /** Returns ShowSeats that are currently available (accounting for expired holds). */
    public List<ShowSeat> getAvailableSeats(String showId) {
        ReentrantLock lock = getLock(showId);
        lock.lock();
        try {
            return getShowSeatMap(showId).values().stream()
                    .filter(ShowSeat::isEffectivelyAvailable)
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Atomically marks the requested ShowSeat IDs as HELD.
     * Throws if any seat is unavailable (already held/booked).
     */
    public List<ShowSeat> holdSeats(String showId, List<String> showSeatIds,
                                     String holdId, Instant expiresAt) {
        ReentrantLock lock = getLock(showId);
        lock.lock();
        try {
            Map<String, ShowSeat> seatMap = getShowSeatMap(showId);

            // Validate all seats exist and are available before touching any
            List<ShowSeat> targets = new ArrayList<>();
            for (String ssId : showSeatIds) {
                ShowSeat ss = seatMap.get(ssId);
                if (ss == null)
                    throw new IllegalArgumentException("ShowSeat not found: " + ssId);
                if (!ss.isEffectivelyAvailable())
                    throw new IllegalStateException(
                            "Seat " + ss.getSeat() + " is not available (status: " + ss.getStatus() + ")");
                targets.add(ss);
            }

            // All clear — mark as held
            for (ShowSeat ss : targets) {
                ss.hold(holdId, expiresAt);
            }
            return targets;

        } finally {
            lock.unlock();
        }
    }

    /**
     * Atomically marks the given ShowSeats as BOOKED.
     * Caller must ensure these seats are currently held by the matching holdId.
     */
    public void bookSeats(String showId, List<ShowSeat> seats) {
        ReentrantLock lock = getLock(showId);
        lock.lock();
        try {
            for (ShowSeat ss : seats) ss.book();
        } finally {
            lock.unlock();
        }
    }

    /** Atomically releases the given ShowSeats back to AVAILABLE. */
    public void releaseSeats(String showId, List<ShowSeat> seats) {
        ReentrantLock lock = getLock(showId);
        lock.lock();
        try {
            for (ShowSeat ss : seats) ss.release();
        } finally {
            lock.unlock();
        }
    }

    private Map<String, ShowSeat> getShowSeatMap(String showId) {
        Map<String, ShowSeat> m = showSeats.get(showId);
        if (m == null) throw new IllegalArgumentException("Show not found: " + showId);
        return m;
    }

    private ReentrantLock getLock(String showId) {
        ReentrantLock lock = showLocks.get(showId);
        if (lock == null) throw new IllegalArgumentException("Show not found: " + showId);
        return lock;
    }
}
