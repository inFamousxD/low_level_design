package factories;

import models.Booking;
import models.SeatHold;
import models.ShowSeat;

import java.util.UUID;

/**
 * Encapsulates Booking construction from a confirmed SeatHold,
 * including total amount computation and booking ID generation.
 */
public class BookingFactory {

    private BookingFactory() {}

    public static Booking fromHold(SeatHold hold) {
        String bookingId = "B-" + UUID.randomUUID();
        double total = hold.getSeats().stream()
                           .mapToDouble(ShowSeat::getPrice)
                           .sum();
        return new Booking(bookingId, hold.getUser(), hold.getShow(), hold.getSeats(), total);
    }
}
