package models;

import enums.SeatType;

public class Seat {
    private final String id;
    private final String row;
    private final int number;
    private final SeatType type;
    private final Screen screen;

    public Seat(String id, String row, int number, SeatType type, Screen screen) {
        this.id = id;
        this.row = row;
        this.number = number;
        this.type = type;
        this.screen = screen;
    }

    public String getId()      { return id; }
    public String getRow()     { return row; }
    public int getNumber()     { return number; }
    public SeatType getType()  { return type; }
    public Screen getScreen()  { return screen; }

    @Override
    public String toString() {
        return row + number + "(" + type + ")";
    }
}
