package models;

import java.time.LocalDateTime;

public class Show {
    private final String id;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public Show(String id, Movie movie, Screen screen, LocalDateTime startTime) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(movie.getDurationMinutes());
    }

    public String getId()                   { return id; }
    public Movie getMovie()                 { return movie; }
    public Screen getScreen()               { return screen; }
    public LocalDateTime getStartTime()     { return startTime; }
    public LocalDateTime getEndTime()       { return endTime; }

    @Override
    public String toString() {
        return "Show[" + movie.getTitle() + " @ " + screen.getTheater().getName()
                + ", " + startTime + "]";
    }
}
