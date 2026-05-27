package services;

import enums.SeatType;
import models.Screen;
import models.Seat;
import models.Theater;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TheaterService {
    private final Map<String, Theater> theaters = new ConcurrentHashMap<>();
    private int seatIdCounter = 1;

    public Theater addTheater(Theater theater) {
        theaters.put(theater.getId(), theater);
        return theater;
    }

    public Screen addScreen(String theaterId, Screen screen) {
        Theater t = getTheater(theaterId);
        t.addScreen(screen);
        return screen;
    }

    /**
     * Adds a grid of seats to a screen: rows × seatsPerRow.
     * Rows are labelled A, B, C… and seats numbered from 1.
     * The first premiumRows rows get PREMIUM type; the rest are REGULAR.
     */
    public void addSeats(Screen screen, int rows, int seatsPerRow, int premiumRows) {
        for (int r = 0; r < rows; r++) {
            String row = String.valueOf((char) ('A' + r));
            SeatType type = r < premiumRows ? SeatType.PREMIUM : SeatType.REGULAR;
            for (int n = 1; n <= seatsPerRow; n++) {
                String seatId = "S" + (seatIdCounter++);
                screen.addSeat(new Seat(seatId, row, n, type, screen));
            }
        }
    }

    public Theater getTheater(String theaterId) {
        Theater t = theaters.get(theaterId);
        if (t == null) throw new IllegalArgumentException("Theater not found: " + theaterId);
        return t;
    }

    public List<Theater> getTheatersByCity(String city) {
        return theaters.values().stream()
                .filter(t -> t.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }
}
