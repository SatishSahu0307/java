package com.bugtracker.view;

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

    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static void displayMainMenu() {
        System.out.println("\n--- Bug Tracking System ---");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Enter your choice: ");
    }

    public static void displayAdminMenu() {
        System.out.println("\n--- Admin Panel ---");
        System.out.println("1. Manage Managers");
        System.out.println("2. Manage Employees (Developers/Testers)");
        System.out.println("3. View All Projects");
        System.out.println("4. View All Bug Reports");
        System.out.println("5. Exit to Main Menu");
        System.out.print("Enter your choice: ");
    }

    public static void displayAdminManagerMenu() {
        System.out.println("\n--- Admin: Manager Management ---");
        System.out.println("1. Add Manager Account");
        System.out.println("2. View Manager Accounts");
        System.out.println("3. Delete Manager Account");
        System.out.println("4. Update Manager Details");
        System.out.println("5. Back to Admin Panel");
        System.out.print("Enter your choice: ");
    }

    public static void displayAdminEmployeeMenu() {
        System.out.println("\n--- Admin: Employee Management ---");
        System.out.println("1. Add Employee Account (Developer/Tester)");
        System.out.println("2. View Employee Accounts");
        System.out.println("3. Delete Employee Account");
        System.out.println("4. Update Employee Details");
        System.out.println("5. Back to Admin Panel");
        System.out.print("Enter your choice: ");
    }

    public static void displayManagerPanelMenu() {
        System.out.println("\n--- Manager Panel ---");
        System.out.println("1. Update Profile");
        System.out.println("2. Manage Projects");
        System.out.println("3. Manage Bugs");
        System.out.println("4. Exit to Main Menu");
        System.out.print("Enter your choice: ");
    }

    public static void displayManagerProjectMenu() {
        System.out.println("\n--- Manager: Project Management ---");
        System.out.println("1. Add Project");
        System.out.println("2. View All Projects");
        System.out.println("3. Delete Project");
        System.out.println("4. Update Project");
        System.out.println("5. Back to Manager Panel");
        System.out.print("Enter your choice: ");
    }

    public static void displayManagerBugMenu() {
        System.out.println("\n--- Manager: Bug Management ---");
        System.out.println("1. Add New Bug");
        System.out.println("2. View All Bugs");
        System.out.println("3. Update Bug");
        System.out.println("4. Delete Bug");
        System.out.println("5. Back to Manager Panel");
        System.out.print("Enter your choice: ");
    }

    public static void displayEmployeePanelMenu() {
        System.out.println("\n--- Employee Panel ---");
        System.out.println("1. Update Profile");
        System.out.println("2. Add Bug Report (Only for Tester)");
        System.out.println("3. Update Bug Status (Only for Developer)");
        System.out.println("4. View Bugs (Assigned/Reported by you)");
        System.out.println("5. View Bug Details (by Bug No)");
        System.out.println("6. Exit to Main Menu");
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
