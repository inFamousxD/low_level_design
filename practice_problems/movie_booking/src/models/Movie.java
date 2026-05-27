package models;

public class Movie {
    private final String id;
    private final String title;
    private final String genre;
    private final int durationMinutes;
    private final String language;
    private double rating;

    public Movie(String id, String title, String genre, int durationMinutes, String language) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.language = language;
    }

    public String getId()              { return id; }
    public String getTitle()           { return title; }
    public String getGenre()           { return genre; }
    public int getDurationMinutes()    { return durationMinutes; }
    public String getLanguage()        { return language; }
    public double getRating()          { return rating; }
    public void setRating(double r)    { this.rating = r; }

    @Override
    public String toString() {
        return "Movie[" + title + ", " + language + ", " + durationMinutes + "min]";
    }
}
