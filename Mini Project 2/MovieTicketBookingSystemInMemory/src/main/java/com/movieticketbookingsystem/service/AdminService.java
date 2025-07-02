package com.movieticketbookingsystem.service;

import com.movieticketbookingsystem.data.InMemoryDataStore;
import com.movieticketbookingsystem.model.Booking;
import com.movieticketbookingsystem.model.Movie;
import com.movieticketbookingsystem.model.Show;
import com.movieticketbookingsystem.model.User; // Added to display user details for bookings
import com.movieticketbookingsystem.view.ConsoleMenu;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Service class for Admin functionalities.
 * Handles adding/updating/deleting movies and shows, and viewing all bookings.
 * Interacts with InMemoryDataStore for all data operations.
 */
public class AdminService {
    private Scanner scanner;

    public AdminService() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * 1. Add new movie & show timing
     */
    public void addNewMovieAndShowTiming() {
        System.out.println("\n--- Add New Movie ---");
        Movie movie = new Movie();
        System.out.print("Enter Movie Title: ");
        movie.setTitle(ConsoleMenu.getStringInput(""));
        System.out.print("Enter Genre: ");
        movie.setGenre(ConsoleMenu.getStringInput(""));
        System.out.print("Enter Duration (minutes): ");
        movie.setDuration(ConsoleMenu.getIntInput(""));
        System.out.print("Enter Description: ");
        movie.setDescription(ConsoleMenu.getStringInput(""));
        System.out.print("Enter Rating (e.g., 8.5): ");
        movie.setRating(ConsoleMenu.getDoubleInput(""));

        if (InMemoryDataStore.addMovie(movie)) {
            System.out.println("Movie '" + movie.getTitle() + "' added successfully with ID: " + movie.getMovieId());
            System.out.print("Do you want to add show timings for this movie now? (yes/no): ");
            String choice = ConsoleMenu.getStringInput("");
            if (choice.equalsIgnoreCase("yes")) {
                addShowTimingForMovie(movie.getMovieId());
            }
        } else {
            System.out.println("Failed to add movie. Title might already exist.");
        }
    }

    public void addShowTimingForMovie(int movieId) {
        Optional<Movie> movieOpt = InMemoryDataStore.getMovieById(movieId);
        if (movieOpt.isEmpty()) {
            System.out.println("Movie with ID " + movieId + " not found. Cannot add show timing.");
            return;
        }
        Movie movie = movieOpt.get();

        System.out.println("\n--- Add Show Timing for Movie: " + movie.getTitle() + " ---");
        Show show = new Show();
        show.setMovieId(movieId);

        System.out.print("Enter Show Date (YYYY-MM-DD): ");
        show.setShowDate(ConsoleMenu.getStringInput(""));
        System.out.print("Enter Show Time (e.g., 10:00 AM, 02:30 PM): ");
        show.setShowTime(ConsoleMenu.getStringInput(""));
        System.out.print("Enter Hall Number: ");
        show.setHallNumber(ConsoleMenu.getIntInput(""));
        System.out.print("Enter Total Seats in Hall: ");
        show.setTotalSeats(ConsoleMenu.getIntInput(""));
        show.setAvailableSeats(show.getTotalSeats()); // Initially all seats are available
        System.out.print("Enter Ticket Price: ");
        show.setTicketPrice(ConsoleMenu.getDoubleInput(""));

        if (InMemoryDataStore.addShow(show)) {
            System.out.println("Show timing added successfully for " + movie.getTitle() + " (Show ID: " + show.getShowId() + ").");
        } else {
            System.out.println("Failed to add show timing. A show with these details (movie, date, time, hall) might already exist or invalid movie ID.");
        }
    }

    /**
     * 2. Update movie detail
     */
    public void updateMovieDetail() {
        System.out.println("\n--- Update Movie Details ---");
        List<Movie> movies = InMemoryDataStore.getAllMovies();
        if (movies.isEmpty()) {
            System.out.println("No movies to update.");
            return;
        }
        displayMovies(movies);

        System.out.print("Enter Movie ID to update: ");
        int movieId = ConsoleMenu.getIntInput("");
        if (movieId == -1) return;

        Optional<Movie> movieOpt = InMemoryDataStore.getMovieById(movieId);
        if (movieOpt.isEmpty()) {
            System.out.println("Movie with ID " + movieId + " not found.");
            return;
        }
        Movie movie = movieOpt.get();

        System.out.println("Current Details for Movie ID " + movieId + ":");
        System.out.println(movie);
        System.out.println("Enter new details (leave blank to keep current value):");

        Movie updatedMovie = new Movie();
        updatedMovie.setMovieId(movieId); // ID cannot be updated

        System.out.print("Enter new Title (" + movie.getTitle() + "): ");
        String title = ConsoleMenu.getStringInput("");
        updatedMovie.setTitle(title.isEmpty() ? movie.getTitle() : title);

        System.out.print("Enter new Genre (" + movie.getGenre() + "): ");
        String genre = ConsoleMenu.getStringInput("");
        updatedMovie.setGenre(genre.isEmpty() ? movie.getGenre() : genre);

        System.out.print("Enter new Duration (minutes) (" + movie.getDuration() + "): ");
        String durationStr = ConsoleMenu.getStringInput("");
        updatedMovie.setDuration(durationStr.isEmpty() ? movie.getDuration() : Integer.parseInt(durationStr));

        System.out.print("Enter new Description (" + movie.getDescription() + "): ");
        String description = ConsoleMenu.getStringInput("");
        updatedMovie.setDescription(description.isEmpty() ? movie.getDescription() : description);

        System.out.print("Enter new Rating (e.g., 8.5) (" + movie.getRating() + "): ");
        String ratingStr = ConsoleMenu.getStringInput("");
        updatedMovie.setRating(ratingStr.isEmpty() ? movie.getRating() : Double.parseDouble(ratingStr));

        if (InMemoryDataStore.updateMovie(updatedMovie)) {
            System.out.println("Movie details updated successfully!");
        } else {
            System.out.println("Failed to update movie details. Title might already exist for another movie.");
        }
    }

    /**
     * 3. Delete movie from the list
     */
    public void deleteMovieFromList() {
        System.out.println("\n--- Delete Movie ---");
        List<Movie> movies = InMemoryDataStore.getAllMovies();
        if (movies.isEmpty()) {
            System.out.println("No movies to delete.");
            return;
        }
        displayMovies(movies);

        System.out.print("Enter Movie ID to delete: ");
        int movieId = ConsoleMenu.getIntInput("");
        if (movieId == -1) return;

        Optional<Movie> movieOpt = InMemoryDataStore.getMovieById(movieId);
        if (movieOpt.isEmpty()) {
            System.out.println("Movie with ID " + movieId + " not found.");
            return;
        }
        Movie movie = movieOpt.get();

        System.out.print("Are you sure you want to delete Movie '" + movie.getTitle() + "' (ID: " + movieId + ")? (yes/no): ");
        String confirmation = ConsoleMenu.getStringInput("");
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Movie deletion cancelled.");
            return;
        }

        if (InMemoryDataStore.deleteMovie(movieId)) {
            System.out.println("Movie and all its associated shows and bookings deleted successfully!");
        } else {
            System.out.println("Failed to delete movie.");
        }
    }

    /**
     * 4. View all bookings
     */
    public void viewAllBookings() {
        System.out.println("\n--- View All Bookings ---");
        List<Booking> bookings = InMemoryDataStore.getAllBookings();
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        displayBookings(bookings);
    }

    // --- Helper Methods ---

    private void displayMovies(List<Movie> movies) {
        if (movies.isEmpty()) {
            System.out.println("No movies to display.");
            return;
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s %-30s %-15s %-10s %-50s %-8s%n", "ID", "Title", "Genre", "Duration", "Description", "Rating");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (Movie movie : movies) {
            String descriptionPreview = movie.getDescription().length() > 47 ? movie.getDescription().substring(0, 44) + "..." : movie.getDescription();
            System.out.printf("%-5d %-30s %-15s %-10d %-50s %-8.1f%n",
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getGenre(),
                    movie.getDuration(),
                    descriptionPreview,
                    movie.getRating());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private void displayBookings(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            System.out.println("No bookings to display.");
            return;
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-15s %-15s %-15s %-15s %-15s %-15s%n", "Booking ID", "User Name", "Movie Title", "Show Time", "Tickets", "Total Amount", "Status");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (Booking booking : bookings) {
            // Fetch associated User and Movie/Show details for a more informative display
            String userName = InMemoryDataStore.getUserByUserId(booking.getUserId())
                                                .map(User::getUsername)
                                                .orElse("N/A");
            String movieTitle = "N/A";
            String showTime = "N/A";
            Optional<Show> showOpt = InMemoryDataStore.getShowById(booking.getShowId());
            if (showOpt.isPresent()) {
                Show show = showOpt.get();
                showTime = show.getShowDate() + " " + show.getShowTime();
                movieTitle = InMemoryDataStore.getMovieById(show.getMovieId())
                                               .map(Movie::getTitle)
                                               .orElse("N/A");
            }

            System.out.printf("%-10d %-15s %-15s %-15s %-15d %-15.2f %-15s%n",
                    booking.getBookingId(),
                    userName,
                    movieTitle,
                    showTime,
                    booking.getNumberOfTickets(),
                    booking.getTotalAmount(),
                    booking.getStatus());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    public void closeScanner() {
        scanner.close();
    }
}
