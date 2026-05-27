package services;

import models.Movie;
import models.Show;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MovieService {
    private final Map<String, Movie> movies = new ConcurrentHashMap<>();
    private final ShowService showService;

    public MovieService(ShowService showService) {
        this.showService = showService;
    }

    public Movie addMovie(Movie movie) {
        movies.put(movie.getId(), movie);
        return movie;
    }

    public Movie getMovie(String movieId) {
        Movie m = movies.get(movieId);
        if (m == null) throw new IllegalArgumentException("Movie not found: " + movieId);
        return m;
    }

    public List<Movie> listMovies() {
        return List.copyOf(movies.values());
    }

    /** Returns distinct movies that have at least one active show in the given city. */
    public List<Movie> searchMoviesByCity(String city) {
        return showService.getAllShows().stream()
                .filter(s -> s.getScreen().getTheater().getCity().equalsIgnoreCase(city))
                .map(Show::getMovie)
                .distinct()
                .collect(Collectors.toList());
    }
}
