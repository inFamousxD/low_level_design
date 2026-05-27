package models;

import enums.SeatStatus;
import java.time.Instant;

/**
 * Represents the state of one physical seat for a specific show.
 * Mutations (hold/book/release) must only be called while the caller holds
 * the per-show lock inside ShowService — that is what makes transitions atomic.
 */
public class ShowSeat {
    private final String id;
    private final Show show;
    private final Seat seat;
    private final double price;

    // Written under per-show lock; volatile so reads outside the lock see current value
    private volatile SeatStatus status = SeatStatus.AVAILABLE;
    private volatile String holdId;
    private volatile Instant holdExpiresAt;

    public ShowSeat(String id, Show show, Seat seat, double price) {
        this.id = id;
        this.show = show;
        this.seat = seat;
        this.price = price;
    }

    /**
     * Returns true if this seat can be held right now.
     * If a prior hold has expired, the seat is reset to AVAILABLE as a side-effect.
     * Must be called under the per-show lock.
     */
    public boolean isEffectivelyAvailable() {
        if (status == SeatStatus.AVAILABLE) return true;
        if (status == SeatStatus.HELD
                && holdExpiresAt != null
                && Instant.now().isAfter(holdExpiresAt)) {
            status = SeatStatus.AVAILABLE;
            holdId = null;
            holdExpiresAt = null;
            return true;
        }
        return false;
    }

    /** Must be called under the per-show lock. */
    public void hold(String holdId, Instant expiresAt) {
        this.status = SeatStatus.HELD;
        this.holdId = holdId;
        this.holdExpiresAt = expiresAt;
    }

    /** Must be called under the per-show lock. */
    public void book() {
        this.status = SeatStatus.BOOKED;
        this.holdId = null;
        this.holdExpiresAt = null;
    }

    /** Must be called under the per-show lock. */
    public void release() {
        this.status = SeatStatus.AVAILABLE;
        this.holdId = null;
        this.holdExpiresAt = null;
    }

    public String getId()             { return id; }
    public Show getShow()             { return show; }
    public Seat getSeat()             { return seat; }
    public double getPrice()          { return price; }
    public SeatStatus getStatus()     { return status; }
    public String getHoldId()         { return holdId; }
    public Instant getHoldExpiresAt() { return holdExpiresAt; }

    @Override
    public String toString() {
        return "ShowSeat[" + seat + ", " + status + "]";
    }
}
