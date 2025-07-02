package com.bugtracker;

import com.bugtracker.model.Employee;
import com.bugtracker.service.AdminService;
import com.bugtracker.service.AuthService;
import com.bugtracker.service.EmployeeService;
import com.bugtracker.service.ManagerService;
import com.bugtracker.view.ConsoleMenu;

/**
 * Main application class for the Console-Based Bug Tracking System.
 * Handles user authentication and dispatches to appropriate role-based menus.
 */
public class BugTrackingSystemApp {

    private static AuthService authService = new AuthService();
    private static AdminService adminService = new AdminService();
    // ManagerService and EmployeeService are instantiated dynamically upon login
    private static ManagerService managerService;
    private static EmployeeService employeeService;

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
                    System.out.println("Exiting Bug Tracking System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue(); // Pause before repeating main menu
        } while (choice != 2);

        // Close scanners for services when the application exits
        adminService.closeScanner();
        ConsoleMenu.closeScanner(); // Close the main scanner as well
        if (managerService != null) {
            managerService.closeScanner();
        }
        if (employeeService != null) {
            employeeService.closeScanner();
        }
    }

    private static void handleLogin() {
        System.out.print("Enter Employee Code (username): ");
        int empCode = ConsoleMenu.getIntInput("");
        System.out.print("Enter Password: ");
        String password = ConsoleMenu.getStringInput("");

        Employee loggedInUser = authService.login(empCode, password);

        if (loggedInUser != null) {
            switch (loggedInUser.getRole().toLowerCase()) {
                case "admin":
                    handleAdminPanel();
                    break;
                case "manager":
                    managerService = new ManagerService(loggedInUser); // Pass the logged-in manager
                    handleManagerPanel();
                    break;
                case "developer":
                case "tester":
                    employeeService = new EmployeeService(loggedInUser); // Pass the logged-in employee
                    handleEmployeePanel();
                    break;
                default:
                    System.out.println("Unknown role for user: " + loggedInUser.getRole());
                    // Maybe log out or deny access
            }
        }
    }

    private static void handleAdminPanel() {
        int choice;
        do {
            ConsoleMenu.displayAdminMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1: // Manage Managers
                    handleAdminManagerMenu();
                    break;
                case 2: // Manage Employees (Developers/Testers)
                    handleAdminEmployeeMenu();
                    break;
                case 3: // View All Projects
                    adminService.viewAllProjects();
                    break;
                case 4: // View Bug Reports
                    adminService.viewBugReports();
                    break;
                case 5:
                    System.out.println("Exiting Admin Panel.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue(); // Pause before repeating admin menu
        } while (choice != 5);
    }

    private static void handleAdminManagerMenu() {
        int choice;
        do {
            ConsoleMenu.displayAdminManagerMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1: adminService.addManagerAccount(); break;
                case 2: adminService.viewManagerAccounts(); break;
                case 3: adminService.deleteManager(); break;
                case 4: adminService.updateManagerDetails(); break;
                case 5: System.out.println("Returning to Admin Panel."); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue();
        } while (choice != 5);
    }

    private static void handleAdminEmployeeMenu() {
        int choice;
        do {
            ConsoleMenu.displayAdminEmployeeMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1: adminService.addEmployeeAccount(); break;
                case 2: adminService.viewEmployeeAccounts(); break;
                case 3: adminService.deleteEmployeeAccount(); break;
                case 4: adminService.updateEmployeeDetails(); break;
                case 5: System.out.println("Returning to Admin Panel."); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue();
        } while (choice != 5);
    }


    private static void handleManagerPanel() {
        int choice;
        do {
            ConsoleMenu.displayManagerPanelMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1: // Update Profile
                    managerService.updateProfile();
                    break;
                case 2: // Manage Projects
                    handleManagerProjectMenu();
                    break;
                case 3: // Manage Bugs
                    handleManagerBugMenu();
                    break;
                case 4:
                    System.out.println("Exiting Manager Panel.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue();
        } while (choice != 4);
    }

    private static void handleManagerProjectMenu() {
        int choice;
        do {
            ConsoleMenu.displayManagerProjectMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1: managerService.addProject(); break;
                case 2: managerService.viewAllProjects(); break;
                case 3: managerService.deleteProject(); break;
                case 4: managerService.updateProject(); break;
                case 5: System.out.println("Returning to Manager Panel."); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue();
        } while (choice != 5);
    }

    private static void handleManagerBugMenu() {
        int choice;
        do {
            ConsoleMenu.displayManagerBugMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1: managerService.addNewBug(); break;
                case 2: managerService.viewAllBugs(); break;
                case 3: managerService.updateBug(); break;
                case 4: managerService.deleteBug(); break;
                case 5: System.out.println("Returning to Manager Panel."); break;
                default: System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue();
        } while (choice != 5);
    }


    private static void handleEmployeePanel() {
        int choice;
        do {
            ConsoleMenu.displayEmployeePanelMenu();
            choice = ConsoleMenu.getIntInput("Choice: ");

            switch (choice) {
                case 1: // Update Profile
                    employeeService.updateProfile();
                    break;
                case 2: // Add Bug Report Hint : [only for tester]
                    employeeService.addBugReportHint();
                    break;
                case 3: // Update Bug status : [only for developer]
                    employeeService.updateBugStatus();
                    break;
                case 4: // View Bug's
                    employeeService.viewBugs();
                    break;
                case 5: // Bug Detail’s
                    employeeService.viewBugDetails();
                    break;
                case 6:
                    System.out.println("Exiting Employee Panel.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            ConsoleMenu.pressAnyKeyToContinue();
        } while (choice != 6);
    }
}
