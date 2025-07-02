package com.bugtracker.service;

import com.bugtracker.dao.BugReportDAO;
import com.bugtracker.dao.BugTypeDAO;
import com.bugtracker.dao.EmployeeDAO;
import com.bugtracker.dao.ProjectDAO;
import com.bugtracker.dao.impl.JdbcBugReportDAO;
import com.bugtracker.dao.impl.JdbcBugTypeDAO;
import com.bugtracker.dao.impl.JdbcEmployeeDAO;
import com.bugtracker.dao.impl.JdbcProjectDAO;
import com.bugtracker.model.BugReport;
import com.bugtracker.model.BugType;
import com.bugtracker.model.Employee;
import com.bugtracker.model.Project;

import java.util.List;
import java.util.Scanner;

/**
 * Service class for Employee functionalities (Developer/Tester).
 * Handles updating profile, adding bug reports (tester), updating bug status (developer), and viewing bugs.
 */
public class EmployeeService {
    private EmployeeDAO employeeDAO;
    private ProjectDAO projectDAO;
    private BugReportDAO bugReportDAO;
    private BugTypeDAO bugTypeDAO;
    private Scanner scanner;
    private Employee loggedInEmployee; // The currently logged-in employee

    public EmployeeService(Employee loggedInEmployee) {
        this.employeeDAO = new JdbcEmployeeDAO();
        this.projectDAO = new JdbcProjectDAO();
        this.bugReportDAO = new JdbcBugReportDAO();
        this.bugTypeDAO = new JdbcBugTypeDAO();
        this.scanner = new Scanner(System.in);
        this.loggedInEmployee = loggedInEmployee;
    }

    // --- Profile Management ---
    public void updateProfile() {
        System.out.println("\n--- Update Your Profile ---");
        System.out.println("Current Details for " + loggedInEmployee.getEmpName() + ":");
        System.out.println(loggedInEmployee);
        System.out.println("Enter new details (leave blank to keep current value):");

        // Collect updated details, excluding empCode and role
        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmpCode(loggedInEmployee.getEmpCode());
        updatedEmployee.setRole(loggedInEmployee.getRole()); // Role cannot be changed

        System.out.print("Enter new Name (" + loggedInEmployee.getEmpName() + "): ");
        String name = scanner.nextLine();
        updatedEmployee.setEmpName(name.isEmpty() ? loggedInEmployee.getEmpName() : name);

        System.out.print("Enter new Email (" + loggedInEmployee.getEmpEmail() + "): ");
        String email = scanner.nextLine();
        updatedEmployee.setEmpEmail(email.isEmpty() ? loggedInEmployee.getEmpEmail() : email);

        System.out.print("Enter new Password (leave blank to keep current): ");
        String password = scanner.nextLine();
        updatedEmployee.setEmpPassword(password.isEmpty() ? loggedInEmployee.getEmpPassword() : password);

        System.out.print("Enter new Gender (" + loggedInEmployee.getGender() + "): ");
        String gender = scanner.nextLine();
        updatedEmployee.setGender(gender.isEmpty() ? loggedInEmployee.getGender() : gender);

        System.out.print("Enter new DOB (YYYY-MM-DD) (" + loggedInEmployee.getDob() + "): ");
        String dob = scanner.nextLine();
        updatedEmployee.setDob(dob.isEmpty() ? loggedInEmployee.getDob() : dob);

        System.out.print("Enter new Mobile Number (" + loggedInEmployee.getMobileNo() + "): ");
        String mobileStr = scanner.nextLine();
        updatedEmployee.setMobileNo(mobileStr.isEmpty() ? loggedInEmployee.getMobileNo() : Long.parseLong(mobileStr));

        if (employeeDAO.updateEmployee(updatedEmployee)) {
            this.loggedInEmployee = employeeDAO.getEmployeeByEmpCode(loggedInEmployee.getEmpCode()); // Refresh loggedInEmployee object
            System.out.println("Profile updated successfully!");
        } else {
            System.out.println("Failed to update profile. Email might already be in use.");
        }
    }

    // --- Bug Report Hints (Only for Tester) ---
    public void addBugReportHint() {
        if (!loggedInEmployee.getRole().equalsIgnoreCase("tester")) {
            System.out.println("Access Denied: Only testers can add bug reports.");
            return;
        }

        System.out.println("\n--- Add New Bug Report ---");
        BugReport bug = new BugReport();

        System.out.println("--- Your Assigned Projects ---");
        List<Integer> assignedProjectIDs = employeeDAO.getAssignedProjectIDsForEmployee(loggedInEmployee.getEmpCode());
        if (assignedProjectIDs.isEmpty()) {
            System.out.println("You are not assigned to any projects. Cannot add a bug report.");
            return;
        }
        List<Project> assignedProjects = new java.util.ArrayList<>();
        System.out.println("------------------------------------------");
        System.out.printf("%-5s %-25s%n", "ID", "Project Name");
        System.out.println("------------------------------------------");
        for(int id : assignedProjectIDs) {
            Project p = projectDAO.getProjectByID(id);
            if (p != null) {
                assignedProjects.add(p);
                System.out.printf("%-5d %-25s%n", p.getProjectID(), p.getProjectName());
            }
        }
        System.out.println("------------------------------------------");


        System.out.print("Enter Project ID for the bug (from your assigned projects): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Project ID must be a number.");
            scanner.nextLine(); // consume invalid input
            return;
        }
        int projectID = scanner.nextInt();
        scanner.nextLine();
        if (!assignedProjectIDs.contains(projectID)) {
            System.out.println("Project ID " + projectID + " is not one of your assigned projects.");
            return;
        }
        bug.setProjectID(projectID);

        System.out.println("\n--- Available Bug Types ---");
        List<BugType> bugTypes = bugTypeDAO.getAllBugTypes();
        if (bugTypes.isEmpty()) {
            System.out.println("No bug types found. Please contact admin to add them.");
            return;
        }
        System.out.println("-----------------------------------------------------");
        System.out.printf("%-10s %-25s %-15s%n", "Bug Code", "Category", "Severity");
        System.out.println("-----------------------------------------------------");
        for (BugType type : bugTypes) {
            System.out.printf("%-10d %-25s %-15s%n", type.getBugCode(), type.getBugCategory(), type.getBugSeverity());
        }
        System.out.println("-----------------------------------------------------");

        System.out.print("Enter Bug Type Code: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Bug Type Code must be a number.");
            scanner.nextLine();
            return;
        }
        int bugCode = scanner.nextInt();
        scanner.nextLine();
        if (bugTypeDAO.getBugTypeByCode(bugCode) == null) {
            System.out.println("Bug Type with code " + bugCode + " not found.");
            return;
        }
        bug.setBugCode(bugCode);

        bug.settCode(loggedInEmployee.getEmpCode()); // Tester is the logged-in employee

        // Fetch Developers assigned to this project
        List<Integer> assignedDevCodes = employeeDAO.getAssignedEmployeeCodesForProject(projectID);
        List<Employee> developersInProject = new java.util.ArrayList<>();
        for(int devCode : assignedDevCodes) {
            Employee dev = employeeDAO.getEmployeeByEmpCode(devCode);
            if (dev != null && dev.getRole().equalsIgnoreCase("developer")) {
                developersInProject.add(dev);
            }
        }
        if (developersInProject.isEmpty()) {
            System.out.println("No developers assigned to project " + projectID + ". Cannot assign bug.");
            return;
        }
        System.out.println("\n--- Developers Assigned to Project " + projectID + " ---");
        displayEmployees(developersInProject);

        System.out.print("Enter Developer Emp Code (to assign the bug to): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Developer Emp Code must be a number.");
            scanner.nextLine();
            return;
        }
        int eCode = scanner.nextInt();
        scanner.nextLine();
        if (!assignedDevCodes.contains(eCode) || employeeDAO.getEmployeeByEmpCode(eCode) == null || !employeeDAO.getEmployeeByEmpCode(eCode).getRole().equalsIgnoreCase("developer")) {
            System.out.println("Developer with Emp Code " + eCode + " not found or not assigned to this project, or is not a developer.");
            return;
        }
        bug.seteCode(eCode);

        bug.setStatus("pending"); // Default status for new bugs

        System.out.print("Enter Bug Description: ");
        bug.setBugDes(scanner.nextLine());

        if (bugReportDAO.addBugReport(bug)) {
            System.out.println("Bug report added successfully with Bug No: " + bug.getBugNo());
        } else {
            System.out.println("Failed to add bug report.");
        }
    }

    // --- Update Bug Status (Only for Developer) ---
    public void updateBugStatus() {
        if (!loggedInEmployee.getRole().equalsIgnoreCase("developer")) {
            System.out.println("Access Denied: Only developers can update bug status.");
            return;
        }

        System.out.println("\n--- Update Bug Status ---");
        System.out.println("--- Bugs Assigned to You (" + loggedInEmployee.getEmpName() + ") ---");
        List<BugReport> assignedBugs = bugReportDAO.getBugReportsByAssignedDeveloper(loggedInEmployee.getEmpCode());
        if (assignedBugs.isEmpty()) {
            System.out.println("No bugs assigned to you.");
            return;
        }
        displayBugReports(assignedBugs);

        System.out.print("Enter Bug No to update status: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Bug No must be a number.");
            scanner.nextLine();
            return;
        }
        int bugNo = scanner.nextInt();
        scanner.nextLine();

        BugReport bug = bugReportDAO.getBugReportByBugNo(bugNo);
        if (bug == null || bug.geteCode() != loggedInEmployee.getEmpCode()) {
            System.out.println("Bug with Bug No " + bugNo + " not found or not assigned to you.");
            return;
        }

        System.out.println("Current status for Bug No " + bugNo + ": " + bug.getStatus());
        System.out.print("Enter new status (pending/resolved): ");
        String newStatus = scanner.nextLine();

        if (newStatus.equalsIgnoreCase("pending") || newStatus.equalsIgnoreCase("resolved")) {
            if (bugReportDAO.updateBugStatus(bugNo, newStatus)) {
                System.out.println("Bug status updated successfully!");
            } else {
                System.out.println("Failed to update bug status.");
            }
        } else {
            System.out.println("Invalid status. Must be 'pending' or 'resolved'.");
        }
    }

    // --- View Bugs ---
    public void viewBugs() {
        System.out.println("\n--- View Your Assigned/Reported Bugs ---");
        if (loggedInEmployee.getRole().equalsIgnoreCase("developer")) {
            System.out.println("Bugs assigned to you:");
            List<BugReport> assignedBugs = bugReportDAO.getBugReportsByAssignedDeveloper(loggedInEmployee.getEmpCode());
            if (assignedBugs.isEmpty()) {
                System.out.println("No bugs assigned to you.");
            } else {
                displayBugReports(assignedBugs);
            }
        } else if (loggedInEmployee.getRole().equalsIgnoreCase("tester")) {
            System.out.println("Bugs reported by you:");
            List<BugReport> reportedBugs = bugReportDAO.getBugReportsByReporter(loggedInEmployee.getEmpCode());
            if (reportedBugs.isEmpty()) {
                System.out.println("No bugs reported by you.");
            } else {
                displayBugReports(reportedBugs);
            }
        } else {
            System.out.println("Your role (" + loggedInEmployee.getRole() + ") does not have specific bug viewing functionality here.");
        }
    }

    // --- Bug Details ---
    public void viewBugDetails() {
        System.out.println("\n--- View Bug Details ---");
        System.out.print("Enter Bug No to view details: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Bug No must be a number.");
            scanner.nextLine();
            return;
        }
        int bugNo = scanner.nextInt();
        scanner.nextLine();

        BugReport bug = bugReportDAO.getBugReportByBugNo(bugNo);
        if (bug == null) {
            System.out.println("Bug with Bug No " + bugNo + " not found.");
            return;
        }

        System.out.println("\n--- Bug Details for Bug No: " + bug.getBugNo() + " ---");
        System.out.println("Description: " + bug.getBugDes());
        System.out.println("Status: " + bug.getStatus());

        // Fetch related details
        BugType bugType = bugTypeDAO.getBugTypeByCode(bug.getBugCode());
        if (bugType != null) {
            System.out.println("Category: " + bugType.getBugCategory());
            System.out.println("Severity: " + bugType.getBugSeverity());
        }

        Project project = projectDAO.getProjectByID(bug.getProjectID());
        if (project != null) {
            System.out.println("Project: " + project.getProjectName() + " (ID: " + project.getProjectID() + ")");
        }

        Employee reporter = employeeDAO.getEmployeeByEmpCode(bug.gettCode());
        if (reporter != null) {
            System.out.println("Reported By: " + reporter.getEmpName() + " (Emp Code: " + reporter.getEmpCode() + ")");
        }

        Employee assignee = employeeDAO.getEmployeeByEmpCode(bug.geteCode());
        if (assignee != null) {
            System.out.println("Assigned To: " + assignee.getEmpName() + " (Emp Code: " + assignee.getEmpCode() + ")");
        }
        System.out.println("--------------------------------------------------");
    }


    // Helper to display employees (copied for convenience)
    private void displayEmployees(List<Employee> employees) {
        if (employees.isEmpty()) {
            System.out.println("No employees to display.");
            return;
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s %-25s %-35s %-10s %-15s %-15s %-15s%n", "Emp Code", "Name", "Email", "Gender", "DOB", "Mobile No", "Role");
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (Employee emp : employees) {
            System.out.printf("%-10d %-25s %-35s %-10s %-15s %-15d %-15s%n",
                    emp.getEmpCode(),
                    emp.getEmpName(),
                    emp.getEmpEmail(),
                    emp.getGender(),
                    emp.getDob(),
                    emp.getMobileNo(),
                    emp.getRole());
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    // Helper to display bug reports (copied for convenience)
    private void displayBugReports(List<BugReport> bugReports) {
        if (bugReports.isEmpty()) {
            System.out.println("No bug reports to display.");
            return;
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-8s %-10s %-10s %-10s %-10s %-15s %-40s%n",
                "Bug No", "Bug Code", "Project ID", "Tester ID", "Dev ID", "Status", "Description");
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        for (BugReport bug : bugReports) {
            System.out.printf("%-8d %-10d %-10d %-10d %-10d %-15s %-40s%n",
                    bug.getBugNo(),
                    bug.getBugCode(),
                    bug.getProjectID(),
                    bug.gettCode(),
                    bug.geteCode(),
                    bug.getStatus(),
                    (bug.getBugDes().length() > 37 ? bug.getBugDes().substring(0, 34) + "..." : bug.getBugDes()));
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    // Helper to close scanner when EmployeeService is no longer needed
    public void closeScanner() {
        scanner.close();
    }
}
