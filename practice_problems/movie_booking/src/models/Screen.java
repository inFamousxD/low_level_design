package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Screen {
    private final String id;
    private final String name;
    private final Theater theater;
    private final List<Seat> seats = new ArrayList<>();

    public Screen(String id, String name, Theater theater) {
        this.id = id;
        this.name = name;
        this.theater = theater;
    }

    public void addSeat(Seat seat) { seats.add(seat); }

    public String getId()           { return id; }
    public String getName()         { return name; }
    public Theater getTheater()     { return theater; }
    public List<Seat> getSeats()    { return Collections.unmodifiableList(seats); }

    @Override
    public String toString() {
        return "Screen[" + name + " @ " + theater.getName() + "]";
    }
}
