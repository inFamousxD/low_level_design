package models;

import enums.HoldStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class SeatHold {
    private final String id;
    private final User user;
    private final Show show;
    private final List<ShowSeat> showSeats;
    private final Instant createdAt;
    private final Instant expiresAt;
    private volatile HoldStatus status;

    public SeatHold(String id, User user, Show show, List<ShowSeat> showSeats, Instant expiresAt) {
        this.id = id;
        this.user = user;
        this.show = show;
        this.showSeats = Collections.unmodifiableList(showSeats);
        this.createdAt = Instant.now();
        this.expiresAt = expiresAt;
        this.status = HoldStatus.ACTIVE;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public String getId()              { return id; }
    public User getUser()              { return user; }
    public Show getShow()              { return show; }
    public List<ShowSeat> getSeats()   { return showSeats; }
    public Instant getCreatedAt()      { return createdAt; }
    public Instant getExpiresAt()      { return expiresAt; }
    public HoldStatus getStatus()      { return status; }
    public void setStatus(HoldStatus s){ this.status = s; }

    @Override
    public String toString() {
        return "SeatHold[" + id + ", " + user.getName() + ", expires=" + expiresAt + ", " + status + "]";
    }
}
