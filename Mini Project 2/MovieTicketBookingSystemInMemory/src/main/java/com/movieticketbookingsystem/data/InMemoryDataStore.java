package com.movieticketbookingsystem.data;

import com.movieticketbookingsystem.model.Booking;
import com.movieticketbookingsystem.model.Movie;
import com.movieticketbookingsystem.model.Show;
import com.movieticketbookingsystem.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Central in-memory data store for the Movie Ticket Booking System.
 * This class simulates a database by holding all application data in collections.
 */
public class InMemoryDataStore {

    private static final List<User> users = new ArrayList<>();
    private static final List<Movie> movies = new ArrayList<>();
    private static final List<Show> shows = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();

    // AtomicIntegers for simulating auto-incrementing IDs
    private static final AtomicInteger userIdCounter = new AtomicInteger(0);
    private static final AtomicInteger movieIdCounter = new AtomicInteger(0);
    private static final AtomicInteger showIdCounter = new AtomicInteger(0);
    private static final AtomicInteger bookingIdCounter = new AtomicInteger(0);

    // --- Initialization with some default data ---
    static {
        // Add a default admin user
        User admin = new User(userIdCounter.incrementAndGet(), "admin", "admin123", "Admin User", "admin@example.com", "admin");
        users.add(admin);
        System.out.println("[DataStore] Added default admin: " + admin.getUsername());

        // Add some default regular users
        User user1 = new User(userIdCounter.incrementAndGet(), "user1", "pass123", "Alice Smith", "alice@example.com", "user");
        User user2 = new User(userIdCounter.incrementAndGet(), "user2", "pass123", "Bob Johnson", "bob@example.com", "user");
        users.add(user1);
        users.add(user2);
        System.out.println("[DataStore] Added default users: " + user1.getUsername() + ", " + user2.getUsername());

        // Add some default movies
        Movie movie1 = new Movie(movieIdCounter.incrementAndGet(), "The Matrix", "Sci-Fi", 136, "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.", 8.7);
        Movie movie2 = new Movie(movieIdCounter.incrementAndGet(), "Inception", "Sci-Fi", 148, "A thief who steals corporate secrets through use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.", 8.8);
        Movie movie3 = new Movie(movieIdCounter.incrementAndGet(), "Interstellar", "Sci-Fi", 169, "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.", 8.6);
        movies.add(movie1);
        movies.add(movie2);
        movies.add(movie3);
        System.out.println("[DataStore] Added default movies.");

        // Add some default shows
        Show show1 = new Show(showIdCounter.incrementAndGet(), movie1.getMovieId(), "10:00 AM", "2025-07-05", 1, 100, 100, 12.50);
        Show show2 = new Show(showIdCounter.incrementAndGet(), movie1.getMovieId(), "02:30 PM", "2025-07-05", 1, 80, 100, 12.50);
        Show show3 = new Show(showIdCounter.incrementAndGet(), movie2.getMovieId(), "07:00 PM", "2025-07-05", 2, 120, 120, 15.00);
        Show show4 = new Show(showIdCounter.incrementAndGet(), movie3.getMovieId(), "09:45 PM", "2025-07-06", 3, 50, 50, 10.00);
        shows.add(show1);
        shows.add(show2);
        shows.add(show3);
        shows.add(show4);
        System.out.println("[DataStore] Added default shows.");
    }

    // --- User Operations ---
    public static boolean addUser(User user) {
        if (users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()) || u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            return false; // User with same username or email already exists
        }
        user.setUserId(userIdCounter.incrementAndGet());
        users.add(user);
        return true;
    }

    public static Optional<User> getUserByUserId(int userId) {
        return users.stream().filter(u -> u.getUserId() == userId).findFirst();
    }

    public static Optional<User> getUserByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public static List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public static boolean updateUser(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId() == updatedUser.getUserId()) {
                // Check for duplicate username/email if they are being changed to existing ones by other users
                boolean usernameExists = users.stream()
                                            .filter(u -> u.getUserId() != updatedUser.getUserId())
                                            .anyMatch(u -> u.getUsername().equalsIgnoreCase(updatedUser.getUsername()));
                boolean emailExists = users.stream()
                                           .filter(u -> u.getUserId() != updatedUser.getUserId())
                                           .anyMatch(u -> u.getEmail().equalsIgnoreCase(updatedUser.getEmail()));
                if (usernameExists || emailExists) {
                    return false; // Cannot update to an existing username/email
                }
                users.set(i, updatedUser);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteUser(int userId) {
        boolean removed = users.removeIf(user -> user.getUserId() == userId);
        if (removed) {
            // Also remove associated bookings
            bookings.removeIf(booking -> booking.getUserId() == userId);
        }
        return removed;
    }

    // --- Movie Operations ---
    public static boolean addMovie(Movie movie) {
        if (movies.stream().anyMatch(m -> m.getTitle().equalsIgnoreCase(movie.getTitle()))) {
            return false; // Movie with same title already exists
        }
        movie.setMovieId(movieIdCounter.incrementAndGet());
        movies.add(movie);
        return true;
    }

    public static Optional<Movie> getMovieById(int movieId) {
        return movies.stream().filter(m -> m.getMovieId() == movieId).findFirst();
    }

    public static Optional<Movie> getMovieByTitle(String title) {
        return movies.stream().filter(m -> m.getTitle().equalsIgnoreCase(title)).findFirst();
    }

    public static List<Movie> getAllMovies() {
        return new ArrayList<>(movies);
    }

    public static boolean updateMovie(Movie updatedMovie) {
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getMovieId() == updatedMovie.getMovieId()) {
                // Check for duplicate title if it's being changed
                boolean titleExists = movies.stream()
                                            .filter(m -> m.getMovieId() != updatedMovie.getMovieId())
                                            .anyMatch(m -> m.getTitle().equalsIgnoreCase(updatedMovie.getTitle()));
                if (titleExists) {
                    return false;
                }
                movies.set(i, updatedMovie);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteMovie(int movieId) {
        boolean removed = movies.removeIf(movie -> movie.getMovieId() == movieId);
        if (removed) {
            // Also remove associated shows and bookings for those shows
            List<Show> showsToDelete = shows.stream().filter(show -> show.getMovieId() == movieId).collect(Collectors.toList());
            for (Show show : showsToDelete) {
                deleteShow(show.getShowId()); // This will also delete related bookings
            }
        }
        return removed;
    }

    // --- Show Operations ---
    public static boolean addShow(Show show) {
        // Check if movie exists
        if (getMovieById(show.getMovieId()).isEmpty()) {
            return false;
        }
        // Basic check for duplicate show (same movie, date, time, hall)
        boolean duplicateShow = shows.stream().anyMatch(s ->
            s.getMovieId() == show.getMovieId() &&
            s.getShowDate().equalsIgnoreCase(show.getShowDate()) &&
            s.getShowTime().equalsIgnoreCase(show.getShowTime()) &&
            s.getHallNumber() == show.getHallNumber()
        );
        if (duplicateShow) {
            return false;
        }

        show.setShowId(showIdCounter.incrementAndGet());
        shows.add(show);
        return true;
    }

    public static Optional<Show> getShowById(int showId) {
        return shows.stream().filter(s -> s.getShowId() == showId).findFirst();
    }

    public static List<Show> getAllShows() {
        return new ArrayList<>(shows);
    }

    public static List<Show> getShowsByMovieId(int movieId) {
        return shows.stream()
                    .filter(s -> s.getMovieId() == movieId)
                    .sorted((s1, s2) -> {
                        int dateCompare = s1.getShowDate().compareTo(s2.getShowDate());
                        if (dateCompare != 0) return dateCompare;
                        return s1.getShowTime().compareTo(s2.getShowTime());
                    })
                    .collect(Collectors.toList());
    }

    public static boolean updateShow(Show updatedShow) {
        for (int i = 0; i < shows.size(); i++) {
            if (shows.get(i).getShowId() == updatedShow.getShowId()) {
                // Check if movie exists if movieId is changed
                if (getMovieById(updatedShow.getMovieId()).isEmpty()) {
                    return false;
                }
                // Check for duplicate show details after update
                boolean duplicateShow = shows.stream()
                                            .filter(s -> s.getShowId() != updatedShow.getShowId())
                                            .anyMatch(s ->
                                                s.getMovieId() == updatedShow.getMovieId() &&
                                                s.getShowDate().equalsIgnoreCase(updatedShow.getShowDate()) &&
                                                s.getShowTime().equalsIgnoreCase(updatedShow.getShowTime()) &&
                                                s.getHallNumber() == updatedShow.getHallNumber()
                                            );
                if (duplicateShow) {
                    return false;
                }
                shows.set(i, updatedShow);
                return true;
            }
        }
        return false;
    }

    public static boolean updateAvailableSeats(int showId, int seatsToChange) {
        Optional<Show> showOpt = getShowById(showId);
        if (showOpt.isPresent()) {
            Show show = showOpt.get();
            int newAvailableSeats = show.getAvailableSeats() + seatsToChange;
            if (newAvailableSeats >= 0 && newAvailableSeats <= show.getTotalSeats()) {
                show.setAvailableSeats(newAvailableSeats);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteShow(int showId) {
        boolean removed = shows.removeIf(show -> show.getShowId() == showId);
        if (removed) {
            // Also remove associated bookings
            bookings.removeIf(booking -> booking.getShowId() == showId);
        }
        return removed;
    }

    // --- Booking Operations ---
    public static boolean addBooking(Booking booking) {
        // Check if user and show exist
        if (getUserByUserId(booking.getUserId()).isEmpty() || getShowById(booking.getShowId()).isEmpty()) {
            return false;
        }
        booking.setBookingId(bookingIdCounter.incrementAndGet());
        bookings.add(booking);
        return true;
    }

    public static Optional<Booking> getBookingById(int bookingId) {
        return bookings.stream().filter(b -> b.getBookingId() == bookingId).findFirst();
    }

    public static List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public static List<Booking> getBookingsByUserId(int userId) {
        return bookings.stream()
                       .filter(b -> b.getUserId() == userId)
                       .sorted((b1, b2) -> b2.getBookingDate().compareTo(b1.getBookingDate())) // Latest first
                       .collect(Collectors.toList());
    }

    public static boolean updateBookingStatus(int bookingId, String status) {
        Optional<Booking> bookingOpt = getBookingById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(status);
            return true;
        }
        return false;
    }

    public static boolean deleteBooking(int bookingId) {
        return bookings.removeIf(booking -> booking.getBookingId() == bookingId);
    }
}
