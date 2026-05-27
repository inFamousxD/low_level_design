package models;

import enums.BookingStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class Booking {
    private final String id;
    private final User user;
    private final Show show;
    private final List<ShowSeat> showSeats;
    private final double totalAmount;
    private final Instant createdAt;
    private volatile BookingStatus status;

    public Booking(String id, User user, Show show, List<ShowSeat> showSeats, double totalAmount) {
        this.id = id;
        this.user = user;
        this.show = show;
        this.showSeats = Collections.unmodifiableList(showSeats);
        this.totalAmount = totalAmount;
        this.createdAt = Instant.now();
        this.status = BookingStatus.CONFIRMED;
    }

    public String getId()               { return id; }
    public User getUser()               { return user; }
    public Show getShow()               { return show; }
    public List<ShowSeat> getSeats()    { return showSeats; }
    public double getTotalAmount()      { return totalAmount; }
    public Instant getCreatedAt()       { return createdAt; }
    public BookingStatus getStatus()    { return status; }
    public void setStatus(BookingStatus s) { this.status = s; }

    @Override
    public String toString() {
        return "Booking[" + id + ", " + user.getName() + ", " + show.getMovie().getTitle()
                + ", seats=" + showSeats + ", ₹" + totalAmount + ", " + status + "]";
    }
}
