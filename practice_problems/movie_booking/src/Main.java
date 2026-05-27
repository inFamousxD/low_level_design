import enums.SeatStatus;
import models.*;
import services.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // ── Bootstrap services (singletons) ──────────────────────────────────
        ShowService    showService    = ShowService.getInstance();
        TheaterService theaterService = TheaterService.getInstance();
        MovieService   movieService   = MovieService.getInstance();
        BookingService bookingService = BookingService.getInstance();

        // ── Setup: theater, screen, seats ─────────────────────────────────────
        Theater pvr = new Theater("T1", "PVR Cinemas", "Bangalore", "MG Road");
        theaterService.addTheater(pvr);

        Screen screen1 = new Screen("SC1", "Screen 1", pvr);
        theaterService.addScreen(pvr.getId(), screen1);
        // 3 premium rows (A-C) + 2 regular rows (D-E), 5 seats each
        theaterService.addSeats(screen1, 5, 5, 3);
        pvr.addScreen(screen1);  // keep theater's own list in sync

        // ── Movie ─────────────────────────────────────────────────────────────
        Movie inception = new Movie("M1", "Inception", "Sci-Fi", 148, "English");
        inception.setRating(8.8);
        movieService.addMovie(inception);

        // ── Show (today 6 PM) ─────────────────────────────────────────────────
        LocalDateTime showTime = LocalDate.now().atTime(18, 0);
        Show show = new Show("SH1", inception, screen1, showTime);
        showService.addShow(show, 200.0);  // ₹200 base price for REGULAR seats

        // ── Search: movies in Bangalore ───────────────────────────────────────
        System.out.println("=== Movies in Bangalore ===");
        movieService.searchMoviesByCity("Bangalore").forEach(System.out::println);

        // ── Search: shows for Inception in Bangalore today ───────────────────
        System.out.println("\n=== Shows for Inception in Bangalore today ===");
        showService.getShowsByMovieAndCity("M1", "Bangalore", LocalDate.now())
                   .forEach(System.out::println);

        // ── Available seats ───────────────────────────────────────────────────
        List<ShowSeat> available = showService.getAvailableSeats("SH1");
        System.out.println("\n=== Available seats (" + available.size() + ") ===");
        available.forEach(ss -> System.out.println("  " + ss.getSeat() + " ₹" + ss.getPrice()));

        // ── Alice holds 2 seats ───────────────────────────────────────────────
        User alice = new User("U1", "Alice", "alice@example.com");
        ShowSeat seat1 = available.get(0);
        ShowSeat seat2 = available.get(1);

        System.out.println("\n=== Alice holds " + seat1.getSeat() + " and " + seat2.getSeat() + " ===");
        SeatHold aliceHold = bookingService.holdSeats(
                "SH1", List.of(seat1.getId(), seat2.getId()), alice);
        System.out.println("Hold: " + aliceHold);

        // ── Bob tries to hold the same seats ─────────────────────────────────
        User bob = new User("U2", "Bob", "bob@example.com");
        System.out.println("\n=== Bob tries to hold the same seats ===");
        try {
            bookingService.holdSeats("SH1", List.of(seat1.getId(), seat2.getId()), bob);
            System.out.println("ERROR: should have thrown!");
        } catch (IllegalStateException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }

        // ── Alice confirms her booking ────────────────────────────────────────
        System.out.println("\n=== Alice confirms booking ===");
        Booking booking = bookingService.confirmBooking(aliceHold.getId());
        System.out.println("Booking confirmed: " + booking);

        // ── Verify seats are now BOOKED ───────────────────────────────────────
        System.out.println("\n=== Seat status after booking ===");
        System.out.println(seat1.getSeat() + " → " + seat1.getStatus());
        System.out.println(seat2.getSeat() + " → " + seat2.getStatus());

        // ── Bob holds two different seats successfully ─────────────────────────
        List<ShowSeat> stillAvailable = showService.getAvailableSeats("SH1");
        ShowSeat seat3 = stillAvailable.get(0);
        ShowSeat seat4 = stillAvailable.get(1);
        System.out.println("\n=== Bob holds " + seat3.getSeat() + " and " + seat4.getSeat() + " ===");
        SeatHold bobHold = bookingService.holdSeats(
                "SH1", List.of(seat3.getId(), seat4.getId()), bob);
        System.out.println("Hold: " + bobHold);
        System.out.println(seat3.getSeat() + " → " + seat3.getStatus());

        // ── Bob cancels his hold ───────────────────────────────────────────────
        System.out.println("\n=== Bob cancels hold ===");
        bookingService.cancelHold(bobHold.getId());
        System.out.println(seat3.getSeat() + " → " + seat3.getStatus()
                + " (back to AVAILABLE)");

        // ── Demo: hold-expiry (use short TTL via reflection-free trick) ────────
        // We simulate expiry by confirming a hold that has already been cancelled
        System.out.println("\n=== Attempting to confirm a cancelled hold ===");
        try {
            bookingService.confirmBooking(bobHold.getId());
        } catch (IllegalStateException e) {
            System.out.println("Correctly rejected: " + e.getMessage());
        }

        // ── Available seats after all operations ──────────────────────────────
        System.out.println("\n=== Final available seat count ===");
        System.out.println(showService.getAvailableSeats("SH1").size()
                + " of " + showService.getShowSeats("SH1").size() + " seats available");

        bookingService.shutdown();
    }
}
