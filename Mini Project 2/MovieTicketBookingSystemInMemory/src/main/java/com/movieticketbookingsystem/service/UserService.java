package com.movieticketbookingsystem.service;

import com.movieticketbookingsystem.data.InMemoryDataStore;
import com.movieticketbookingsystem.model.Booking;
import com.movieticketbookingsystem.model.Movie;
import com.movieticketbookingsystem.model.Show;
import com.movieticketbookingsystem.model.User;
import com.movieticketbookingsystem.view.ConsoleMenu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Service class for User functionalities.
 * Handles viewing movies, checking showtimes, booking, and canceling tickets.
 * Interacts with InMemoryDataStore for all data operations.
 */
public class UserService {
    private User loggedInUser;
    private Scanner scanner;

    public UserService(User loggedInUser) {
        this.loggedInUser = loggedInUser;
        this.scanner = new Scanner(System.in);
    }

    /**
     * 1. View list of currently running movies
     */
    public void viewRunningMovies() {
        System.out.println("\n--- Currently Running Movies ---");
        List<Movie> movies = InMemoryDataStore.getAllMovies();
        if (movies.isEmpty()) {
            System.out.println("No movies are currently running.");
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

    /**
     * 2. Check movie details and show time
     */
    public void checkMovieDetailsAndShowTime() {
        System.out.println("\n--- Check Movie Details and Showtimes ---");
        viewRunningMovies(); // Show all movies first

        System.out.print("Enter Movie ID to view details and showtimes: ");
        int movieId = ConsoleMenu.getIntInput("");
        if (movieId == -1) return;

        Optional<Movie> movieOpt = InMemoryDataStore.getMovieById(movieId);
        if (movieOpt.isEmpty()) {
            System.out.println("Movie with ID " + movieId + " not found.");
            return;
        }
        Movie movie = movieOpt.get();

        System.out.println("\n--- Details for Movie: " + movie.getTitle() + " ---");
        System.out.println("ID: " + movie.getMovieId());
        System.out.println("Title: " + movie.getTitle());
        System.out.println("Genre: " + movie.getGenre());
        System.out.println("Duration: " + movie.getDuration() + " minutes");
        System.out.println("Description: " + movie.getDescription());
        System.out.println("Rating: " + movie.getRating());

        System.out.println("\n--- Available Showtimes for " + movie.getTitle() + " ---");
        List<Show> shows = InMemoryDataStore.getShowsByMovieId(movieId);
        if (shows.isEmpty()) {
            System.out.println("No showtimes found for this movie.");
            return;
        }

        displayShows(shows);
    }

    /**
     * 4. Book movie Seat (moved before cancel as it's a primary user action)
     */
    public void bookMovieSeat() {
        System.out.println("\n--- Book Movie Seats ---");
        viewRunningMovies(); // Show movies first

        System.out.print("Enter Movie ID you want to book for: ");
        int movieId = ConsoleMenu.getIntInput("");
        if (movieId == -1) return;

        Optional<Movie> movieOpt = InMemoryDataStore.getMovieById(movieId);
        if (movieOpt.isEmpty()) {
            System.out.println("Movie with ID " + movieId + " not found.");
            return;
        }
        Movie movie = movieOpt.get();

        System.out.println("\n--- Available Showtimes for " + movie.getTitle() + " ---");
        List<Show> shows = InMemoryDataStore.getShowsByMovieId(movieId);
        if (shows.isEmpty()) {
            System.out.println("No showtimes available for this movie.");
            return;
        }
        displayShows(shows);

        System.out.print("Enter Show ID you want to book: ");
        int showId = ConsoleMenu.getIntInput("");
        if (showId == -1) return;

        Optional<Show> selectedShowOpt = InMemoryDataStore.getShowById(showId);
        if (selectedShowOpt.isEmpty() || selectedShowOpt.get().getMovieId() != movieId) {
            System.out.println("Invalid Show ID for the selected movie.");
            return;
        }
        Show selectedShow = selectedShowOpt.get();

        System.out.println("Selected Show: " + movie.getTitle() + " on " + selectedShow.getShowDate() + " at " + selectedShow.getShowTime() + " (Hall: " + selectedShow.getHallNumber() + ")");
        System.out.println("Available Seats: " + selectedShow.getAvailableSeats());
        System.out.println("Total Seats: " + selectedShow.getTotalSeats());
        System.out.println("Ticket Price per seat: $" + selectedShow.getTicketPrice());

        System.out.print("Enter number of tickets to book: ");
        int numTickets = ConsoleMenu.getIntInput("");
        if (numTickets <= 0) {
            System.out.println("Number of tickets must be positive.");
            return;
        }

        if (numTickets > selectedShow.getAvailableSeats()) {
            System.out.println("Not enough seats available. Only " + selectedShow.getAvailableSeats() + " seats remaining.");
            return;
        }

        double totalAmount = numTickets * selectedShow.getTicketPrice();
        System.out.println("Total amount for " + numTickets + " tickets: $" + String.format("%.2f", totalAmount));

        System.out.print("Confirm booking? (yes/no): ");
        String confirmation = ConsoleMenu.getStringInput("");
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Booking cancelled by user.");
            return;
        }

        // Proceed with booking
        Booking newBooking = new Booking();
        newBooking.setUserId(loggedInUser.getUserId());
        newBooking.setShowId(showId);
        newBooking.setNumberOfTickets(numTickets);
        newBooking.setBookingDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE)); // Current date
        newBooking.setTotalAmount(totalAmount);
        newBooking.setStatus("confirmed");

        if (InMemoryDataStore.addBooking(newBooking)) {
            // Update available seats in the Show
            if (InMemoryDataStore.updateAvailableSeats(showId, -numTickets)) {
                System.out.println("Booking successful! Your Booking ID is: " + newBooking.getBookingId());
            } else {
                System.out.println("Booking added, but failed to update available seats. Data inconsistency.");
            }
        } else {
            System.out.println("Failed to create booking. Please try again.");
        }
    }

    /**
     * 3. Cancel previously Booked Ticket
     */
    public void cancelPreviouslyBookedTicket() {
        System.out.println("\n--- Cancel Previously Booked Ticket ---");
        List<Booking> userBookings = InMemoryDataStore.getBookingsByUserId(loggedInUser.getUserId());

        if (userBookings.isEmpty()) {
            System.out.println("You have no bookings to cancel.");
            return;
        }

        System.out.println("\n--- Your Bookings ---");
        displayBookings(userBookings);

        System.out.print("Enter Booking ID to cancel: ");
        int bookingId = ConsoleMenu.getIntInput("");
        if (bookingId == -1) return;

        Optional<Booking> bookingToCancelOpt = InMemoryDataStore.getBookingById(bookingId);

        if (bookingToCancelOpt.isEmpty() || bookingToCancelOpt.get().getUserId() != loggedInUser.getUserId()) {
            System.out.println("Booking with ID " + bookingId + " not found or does not belong to you.");
            return;
        }
        Booking bookingToCancel = bookingToCancelOpt.get();

        if (bookingToCancel.getStatus().equalsIgnoreCase("cancelled")) {
            System.out.println("Booking ID " + bookingId + " is already cancelled.");
            return;
        }

        System.out.print("Are you sure you want to cancel Booking ID " + bookingId + "? (yes/no): ");
        String confirmation = ConsoleMenu.getStringInput("");
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Cancellation cancelled by user.");
            return;
        }

        if (InMemoryDataStore.updateBookingStatus(bookingId, "cancelled")) {
            // Release seats back to the show
            if (InMemoryDataStore.updateAvailableSeats(bookingToCancel.getShowId(), bookingToCancel.getNumberOfTickets())) {
                System.out.println("Booking ID " + bookingId + " successfully cancelled and seats released.");
            } else {
                System.out.println("Booking ID " + bookingId + " cancelled, but failed to release seats. Data inconsistency.");
            }
        } else {
            System.out.println("Failed to cancel booking ID " + bookingId + ". Please try again.");
        }
    }

    // --- Helper Methods ---

    private void displayShows(List<Show> shows) {
        if (shows.isEmpty()) {
            System.out.println("No shows to display.");
            return;
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-15s %-15s %-10s %-15s %-15s %-15s%n", "Show ID", "Date", "Time", "Hall", "Available Seats", "Total Seats", "Ticket Price");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (Show show : shows) {
            System.out.printf("%-8d %-15s %-15s %-10d %-15d %-15d %-15.2f%n",
                    show.getShowId(),
                    show.getShowDate(),
                    show.getShowTime(),
                    show.getHallNumber(),
                    show.getAvailableSeats(),
                    show.getTotalSeats(),
                    show.getTicketPrice());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private void displayBookings(List<Booking> bookings) {
        if (bookings.isEmpty()) {
            System.out.println("No bookings to display.");
            return;
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-10s %-10s %-15s %-15s %-15s %-15s%n", "Booking ID", "User ID", "Show ID", "Tickets", "Booking Date", "Total Amount", "Status");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (Booking booking : bookings) {
            System.out.printf("%-10d %-10d %-10d %-15d %-15s %-15.2f %-15s%n",
                    booking.getBookingId(),
                    booking.getUserId(),
                    booking.getShowId(),
                    booking.getNumberOfTickets(),
                    booking.getBookingDate(),
                    booking.getTotalAmount(),
                    booking.getStatus());
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    public void closeScanner() {
        scanner.close();
    }
}
