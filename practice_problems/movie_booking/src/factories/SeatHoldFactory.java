package factories;

import models.SeatHold;
import models.Show;
import models.ShowSeat;
import models.User;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Owns the hold TTL policy and encapsulates SeatHold construction.
 * Previously HOLD_TTL lived in BookingService and the hold ID / expiry were computed inline.
 */
public class SeatHoldFactory {

    public static final Duration HOLD_TTL = Duration.ofMinutes(10);

    private SeatHoldFactory() {}

    public static String generateHoldId() {
        return "H-" + UUID.randomUUID();
    }

    public static Instant generateExpiresAt() {
        return Instant.now().plus(HOLD_TTL);
    }

    public static SeatHold create(String holdId, User user, Show show,
                                   List<ShowSeat> seats, Instant expiresAt) {
        return new SeatHold(holdId, user, show, seats, expiresAt);
    }
}
