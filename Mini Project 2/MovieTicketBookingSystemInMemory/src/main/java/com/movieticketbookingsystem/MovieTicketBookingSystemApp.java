package com.movieticketbookingsystem;

import com.movieticketbookingsystem.model.User;
import com.movieticketbookingsystem.service.AdminService;
import com.movieticketbookingsystem.service.AuthService;
import com.movieticketbookingsystem.service.UserService;
import com.movieticketbookingsystem.view.ConsoleMenu;

/**
 * Main application class for the Console-Based Movie Ticket Booking System.
 * Handles user authentication (login/registration) and dispatches to appropriate role-based menus.
 */
public class MovieTicketBookingSystemApp {

    private static AuthService authService = new AuthService();
    private static AdminService adminService = new AdminService();
    private static UserService userService; // Instantiated dynamically upon user login

    public static void main(String[] args) {
        runApp();
    }

    public static void runApp() {
        int choice;
        do {
            ConsoleMenu.displayMainMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegistration();
                    break;
                case 3:
                    System.out.println("Exiting Movie Ticket Booking System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue(); // Pause before repeating main menu
        } while (choice != 3);

        // Close scanners for services when the application exits
        adminService.closeScanner();
        ConsoleMenu.closeScanner(); // Close the main scanner as well
        if (userService != null) {
            userService.closeScanner();
        }
    }

    private static void handleLogin() {
        System.out.print("Enter Username: ");
        String username = ConsoleMenu.getStringInput("");
        System.out.print("Enter Password: ");
        String password = ConsoleMenu.getStringInput("");

        User loggedInUser = authService.login(username, password);

        if (loggedInUser != null) {
            switch (loggedInUser.getRole().toLowerCase()) {
                case "admin":
                    handleAdminModule();
                    break;
                case "user":
                    userService = new UserService(loggedInUser); // Pass the logged-in user
                    handleUserModule();
                    break;
                default:
                    System.out.println("Unknown role for user: " + loggedInUser.getRole());
                    // Maybe log out or deny access
            }
        }
    }

    private static void handleRegistration() {
        System.out.println("\n--- Register New User ---");
        System.out.print("Enter desired Username: ");
        String username = ConsoleMenu.getStringInput("");
        System.out.print("Enter Password: ");
        String password = ConsoleMenu.getStringInput("");
        System.out.print("Enter Your Full Name: ");
        String name = ConsoleMenu.getStringInput("");
        System.out.print("Enter Your Email: ");
        String email = ConsoleMenu.getStringInput("");

        authService.registerUser(username, password, name, email);
    }

    private static void handleUserModule() {
        int choice;
        do {
            ConsoleMenu.displayUserMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1: // View list of currently running movies
                    userService.viewRunningMovies();
                    break;
                case 2: // Check movie details and show time
                    userService.checkMovieDetailsAndShowTime();
                    break;
                case 3: // Book movie Seat
                    userService.bookMovieSeat();
                    break;
                case 4: // Cancel previously Booked Ticket
                    userService.cancelPreviouslyBookedTicket();
                    break;
                case 5:
                    System.out.println("Exiting User Module.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue();
        } while (choice != 5);
    }

    private static void handleAdminModule() {
        int choice;
        do {
            ConsoleMenu.displayAdminMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1: // Add new movie & show timing
                    adminService.addNewMovieAndShowTiming();
                    break;
                case 2: // Update movie detail
                    adminService.updateMovieDetail();
                    break;
                case 3: // Delete movie from the list
                    adminService.deleteMovieFromList();
                    break;
                case 4: // View all bookings
                    adminService.viewAllBookings();
                    break;
                case 5:
                    System.out.println("Exiting Admin Module.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue();
        } while (choice != 5);
    }
}
