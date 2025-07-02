package com.movieticketbookingsystem.view;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Utility class to display console menus and read user input.
 */
public class ConsoleMenu {
    private static Scanner scanner = new Scanner(System.in);

    public static int getIntInput(String prompt) {
        System.out.print(prompt);
        int input = -1;
        try {
            input = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
        } finally {
            scanner.nextLine(); // Consume the rest of the line
        }
        return input;
    }

    public static double getDoubleInput(String prompt) {
        System.out.print(prompt);
        double input = -1.0;
        try {
            input = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
        } finally {
            scanner.nextLine(); // Consume the rest of the line
        }
        return input;
    }

    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static void displayMainMenu() {
        System.out.println("\n--- Movie Ticket Booking System ---");
        System.out.println("1. Login");
        System.out.println("2. Register New User");
        System.out.println("3. Exit");
        System.out.print("Enter your choice: ");
    }

    public static void displayUserMenu() {
        System.out.println("\n--- User Module ---");
        System.out.println("1. View list of currently running movies");
        System.out.println("2. Check movie details and show time");
        System.out.println("3. Book movie Seat");
        System.out.println("4. Cancel previously Booked Ticket");
        System.out.println("5. Exit to Main Menu");
        System.out.print("Enter your choice: ");
    }

    public static void displayAdminMenu() {
        System.out.println("\n--- Admin Module ---");
        System.out.println("1. Add new movie & show timing");
        System.out.println("2. Update movie detail");
        System.out.println("3. Delete movie from the list");
        System.out.println("4. View all bookings");
        System.out.println("5. Exit to Main Menu");
        System.out.print("Enter your choice: ");
    }

    public static void pressAnyKeyToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public static void closeScanner() {
        scanner.close();
    }
}
