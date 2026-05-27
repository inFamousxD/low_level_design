package factories;

import enums.SeatType;
import models.Seat;
import models.Show;
import models.ShowSeat;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Centralises ShowSeat construction and the seat-type → price-multiplier mapping.
 * Previously this logic was inlined in ShowService.addShow.
 */
public class ShowSeatFactory {

    private static final AtomicInteger idCounter = new AtomicInteger(1);

    private ShowSeatFactory() {}

    public static ShowSeat create(Show show, Seat seat, double basePrice) {
        String id = "SS" + idCounter.getAndIncrement();
        double price = basePrice * multiplierFor(seat.getType());
        return new ShowSeat(id, show, seat, price);
    }

    private static double multiplierFor(SeatType type) {
        return switch (type) {
            case REGULAR -> 1.0;
            case PREMIUM -> 1.5;
            case VIP     -> 2.5;
        };
    }
}
