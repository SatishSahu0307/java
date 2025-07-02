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
 * Service class for Manager functionalities.
 * Handles updating profile, managing projects, and managing bugs.
 */
public class ManagerService {
    private EmployeeDAO employeeDAO;
    private ProjectDAO projectDAO;
    private BugReportDAO bugReportDAO;
    private BugTypeDAO bugTypeDAO;
    private Scanner scanner;
    private Employee loggedInManager;

    public ManagerService(Employee loggedInManager) {
        this.employeeDAO = new JdbcEmployeeDAO();
        this.projectDAO = new JdbcProjectDAO();
        this.bugReportDAO = new JdbcBugReportDAO();
        this.bugTypeDAO = new JdbcBugTypeDAO();
        this.scanner = new Scanner(System.in);
        this.loggedInManager = loggedInManager;
    }

    // --- Profile Management ---
    public void updateProfile() {
        System.out.println("\n--- Update Your Profile ---");
        System.out.println("Current Details for " + loggedInManager.getEmpName() + ":");
        System.out.println(loggedInManager);
        System.out.println("Enter new details (leave blank to keep current value):");

        // Collect updated details, excluding empCode and role
        Employee updatedManager = new Employee();
        updatedManager.setEmpCode(loggedInManager.getEmpCode());
        updatedManager.setRole(loggedInManager.getRole()); // Role cannot be changed

        System.out.print("Enter new Name (" + loggedInManager.getEmpName() + "): ");
        String name = scanner.nextLine();
        updatedManager.setEmpName(name.isEmpty() ? loggedInManager.getEmpName() : name);

        System.out.print("Enter new Email (" + loggedInManager.getEmpEmail() + "): ");
        String email = scanner.nextLine();
        updatedManager.setEmpEmail(email.isEmpty() ? loggedInManager.getEmpEmail() : email);

        System.out.print("Enter new Password (leave blank to keep current): ");
        String password = scanner.nextLine();
        updatedManager.setEmpPassword(password.isEmpty() ? loggedInManager.getEmpPassword() : password);

        System.out.print("Enter new Gender (" + loggedInManager.getGender() + "): ");
        String gender = scanner.nextLine();
        updatedManager.setGender(gender.isEmpty() ? loggedInManager.getGender() : gender);

        System.out.print("Enter new DOB (YYYY-MM-DD) (" + loggedInManager.getDob() + "): ");
        String dob = scanner.nextLine();
        updatedManager.setDob(dob.isEmpty() ? loggedInManager.getDob() : dob);

        System.out.print("Enter new Mobile Number (" + loggedInManager.getMobileNo() + "): ");
        String mobileStr = scanner.nextLine();
        updatedManager.setMobileNo(mobileStr.isEmpty() ? loggedInManager.getMobileNo() : Long.parseLong(mobileStr));

        if (employeeDAO.updateEmployee(updatedManager)) {
            this.loggedInManager = employeeDAO.getEmployeeByEmpCode(loggedInManager.getEmpCode()); // Refresh loggedInManager object
            System.out.println("Profile updated successfully!");
        } else {
            System.out.println("Failed to update profile. Email might already be in use.");
        }
    }

    // --- Project Management ---
    public void addProject() {
        System.out.println("\n--- Add New Project ---");
        Project project = new Project();
        System.out.print("Enter Project Name: ");
        project.setProjectName(scanner.nextLine());
        System.out.print("Enter Start Date (YYYY-MM-DD): ");
        project.setsDate(scanner.nextLine());
        System.out.print("Enter End Date (YYYY-MM-DD): ");
        project.seteDate(scanner.nextLine());
        System.out.print("Enter Project Description: ");
        project.setProjectDec(scanner.nextLine());

        if (projectDAO.addProject(project)) {
            System.out.println("Project added successfully with ID: " + project.getProjectID());
            System.out.print("Do you want to assign employees to this project now? (yes/no): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("yes")) {
                assignEmployeesToProject(project.getProjectID());
            }
        } else {
            System.out.println("Failed to add project. Project name might already exist.");
        }
    }

    public void viewAllProjects() {
        System.out.println("\n--- View All Projects ---");
        List<Project> projects = projectDAO.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s %-25s %-15s %-15s %-30s%n", "ID", "Project Name", "Start Date", "End Date", "Description");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (Project project : projects) {
            System.out.printf("%-5d %-25s %-15s %-15s %-30s%n",
                    project.getProjectID(),
                    project.getProjectName(),
                    project.getsDate(),
                    project.geteDate(),
                    (project.getProjectDec().length() > 27 ? project.getProjectDec().substring(0, 24) + "..." : project.getProjectDec()));
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
    }

    public void deleteProject() {
        System.out.println("\n--- Delete Project ---");
        System.out.print("Enter Project ID to delete: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Project ID must be a number.");
            scanner.nextLine(); // consume invalid input
            return;
        }
        int projectID = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Project project = projectDAO.getProjectByID(projectID);
        if (project == null) {
            System.out.println("Project with ID " + projectID + " not found.");
            return;
        }

        System.out.print("Are you sure you want to delete Project '" + project.getProjectName() + "' (ID: " + projectID + ")? (yes/no): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            if (projectDAO.deleteProject(projectID)) {
                System.out.println("Project deleted successfully!");
            } else {
                System.out.println("Failed to delete project. Ensure no dependent data (bugs, assignments) exist or are handled by cascade delete.");
            }
        } else {
            System.out.println("Project deletion cancelled.");
        }
    }

    public void updateProject() {
        System.out.println("\n--- Update Project ---");
        System.out.print("Enter Project ID to update: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Project ID must be a number.");
            scanner.nextLine(); // consume invalid input
            return;
        }
        int projectID = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Project project = projectDAO.getProjectByID(projectID);
        if (project == null) {
            System.out.println("Project with ID " + projectID + " not found.");
            return;
        }

        System.out.println("Current Details for Project ID " + projectID + ":");
        System.out.println(project);
        System.out.println("Enter new details (leave blank to keep current value):");

        Project updatedProject = new Project();
        updatedProject.setProjectID(projectID); // ID cannot be updated

        System.out.print("Enter new Project Name (" + project.getProjectName() + "): ");
        String name = scanner.nextLine();
        updatedProject.setProjectName(name.isEmpty() ? project.getProjectName() : name);

        System.out.print("Enter new Start Date (YYYY-MM-DD) (" + project.getsDate() + "): ");
        String sDate = scanner.nextLine();
        updatedProject.setsDate(sDate.isEmpty() ? project.getsDate() : sDate);

        System.out.print("Enter new End Date (YYYY-MM-DD) (" + project.geteDate() + "): ");
        String eDate = scanner.nextLine();
        updatedProject.seteDate(eDate.isEmpty() ? project.geteDate() : eDate);

        System.out.print("Enter new Project Description (" + project.getProjectDec() + "): ");
        String dec = scanner.nextLine();
        updatedProject.setProjectDec(dec.isEmpty() ? project.getProjectDec() : dec);

        if (projectDAO.updateProject(updatedProject)) {
            System.out.println("Project updated successfully!");
            System.out.print("Do you want to manage employee assignments for this project? (yes/no): ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("yes")) {
                assignEmployeesToProject(projectID);
            }
        } else {
            System.out.println("Failed to update project. Project name might already exist.");
        }
    }

    // --- Bug Management ---
    public void addNewBug() {
        System.out.println("\n--- Add New Bug Report ---");
        BugReport bug = new BugReport();

        viewAllProjects();
        System.out.print("Enter Project ID for the bug: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Project ID must be a number.");
            scanner.nextLine(); // consume invalid input
            return;
        }
        int projectID = scanner.nextInt();
        scanner.nextLine();
        if (projectDAO.getProjectByID(projectID) == null) {
            System.out.println("Project with ID " + projectID + " not found.");
            return;
        }
        bug.setProjectID(projectID);

        System.out.println("\n--- Available Bug Types ---");
        List<BugType> bugTypes = bugTypeDAO.getAllBugTypes();
        if (bugTypes.isEmpty()) {
            System.out.println("No bug types found. Please add them to BugType table first.");
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

        // Fetch Testers
        List<Employee> testers = employeeDAO.getEmployeesByRole("tester");
        if (testers.isEmpty()) {
            System.out.println("No Testers found in the system. Cannot assign bug.");
            return;
        }
        System.out.println("\n--- Available Testers ---");
        displayEmployees(testers);
        System.out.print("Enter Tester Emp Code (who reported the bug): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Tester Emp Code must be a number.");
            scanner.nextLine();
            return;
        }
        int tCode = scanner.nextInt();
        scanner.nextLine();
        if (employeeDAO.getEmployeeByEmpCode(tCode) == null || !employeeDAO.getEmployeeByEmpCode(tCode).getRole().equalsIgnoreCase("tester")) {
            System.out.println("Tester with Emp Code " + tCode + " not found or is not a tester.");
            return;
        }
        bug.settCode(tCode);

        // Fetch Developers
        List<Employee> developers = employeeDAO.getEmployeesByRole("developer");
        if (developers.isEmpty()) {
            System.out.println("No Developers found in the system. Cannot assign bug.");
            return;
        }
        System.out.println("\n--- Available Developers ---");
        displayEmployees(developers);
        System.out.print("Enter Developer Emp Code (to assign the bug to): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Developer Emp Code must be a number.");
            scanner.nextLine();
            return;
        }
        int eCode = scanner.nextInt();
        scanner.nextLine();
        if (employeeDAO.getEmployeeByEmpCode(eCode) == null || !employeeDAO.getEmployeeByEmpCode(eCode).getRole().equalsIgnoreCase("developer")) {
            System.out.println("Developer with Emp Code " + eCode + " not found or is not a developer.");
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

    public void viewAllBugs() {
        System.out.println("\n--- View All Bug Reports ---");
        List<BugReport> bugReports = bugReportDAO.getAllBugReports();
        if (bugReports.isEmpty()) {
            System.out.println("No bug reports found.");
            return;
        }
        displayBugReports(bugReports);
    }

    public void updateBug() {
        System.out.println("\n--- Update Bug Report ---");
        System.out.print("Enter Bug No to update: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Bug No must be a number.");
            scanner.nextLine();
            return;
        }
        int bugNo = scanner.nextInt();
        scanner.nextLine();

        BugReport bug = bugReportDAO.getBugReportByBugNo(bugNo);
        if (bug == null) {
            System.out.println("Bug report with Bug No " + bugNo + " not found.");
            return;
        }

        System.out.println("Current Details for Bug No " + bugNo + ":");
        System.out.println(bug);
        System.out.println("Enter new details (leave blank to keep current value):");

        BugReport updatedBug = new BugReport();
        updatedBug.setBugNo(bugNo); // Bug No cannot be updated

        // Bug Code
        System.out.println("\n--- Available Bug Types ---");
        List<BugType> bugTypes = bugTypeDAO.getAllBugTypes();
        if (!bugTypes.isEmpty()) {
            System.out.println("-----------------------------------------------------");
            System.out.printf("%-10s %-25s %-15s%n", "Bug Code", "Category", "Severity");
            System.out.println("-----------------------------------------------------");
            for (BugType type : bugTypes) {
                System.out.printf("%-10d %-25s %-15s%n", type.getBugCode(), type.getBugCategory(), type.getBugSeverity());
            }
            System.out.println("-----------------------------------------------------");
        }
        System.out.print("Enter new Bug Type Code (" + bug.getBugCode() + "): ");
        String bugCodeStr = scanner.nextLine();
        if (!bugCodeStr.isEmpty()) {
            try {
                int newBugCode = Integer.parseInt(bugCodeStr);
                if (bugTypeDAO.getBugTypeByCode(newBugCode) != null) {
                    updatedBug.setBugCode(newBugCode);
                } else {
                    System.out.println("Invalid Bug Type Code. Keeping current.");
                    updatedBug.setBugCode(bug.getBugCode());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for Bug Type Code. Keeping current.");
                updatedBug.setBugCode(bug.getBugCode());
            }
        } else {
            updatedBug.setBugCode(bug.getBugCode());
        }

        // Project ID
        viewAllProjects();
        System.out.print("Enter new Project ID (" + bug.getProjectID() + "): ");
        String projectIDStr = scanner.nextLine();
        if (!projectIDStr.isEmpty()) {
            try {
                int newProjectID = Integer.parseInt(projectIDStr);
                if (projectDAO.getProjectByID(newProjectID) != null) {
                    updatedBug.setProjectID(newProjectID);
                } else {
                    System.out.println("Invalid Project ID. Keeping current.");
                    updatedBug.setProjectID(bug.getProjectID());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for Project ID. Keeping current.");
                updatedBug.setProjectID(bug.getProjectID());
            }
        } else {
            updatedBug.setProjectID(bug.getProjectID());
        }

        // Tester Code (TCode)
        List<Employee> testers = employeeDAO.getEmployeesByRole("tester");
        if (!testers.isEmpty()) {
            System.out.println("\n--- Available Testers ---");
            displayEmployees(testers);
        }
        System.out.print("Enter new Tester Emp Code (" + bug.gettCode() + "): ");
        String tCodeStr = scanner.nextLine();
        if (!tCodeStr.isEmpty()) {
            try {
                int newTCode = Integer.parseInt(tCodeStr);
                if (employeeDAO.getEmployeeByEmpCode(newTCode) != null && employeeDAO.getEmployeeByEmpCode(newTCode).getRole().equalsIgnoreCase("tester")) {
                    updatedBug.settCode(newTCode);
                } else {
                    System.out.println("Invalid Tester Emp Code. Keeping current.");
                    updatedBug.settCode(bug.gettCode());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for Tester Emp Code. Keeping current.");
                updatedBug.settCode(bug.gettCode());
            }
        } else {
            updatedBug.settCode(bug.gettCode());
        }

        // Developer Code (ECode)
        List<Employee> developers = employeeDAO.getEmployeesByRole("developer");
        if (!developers.isEmpty()) {
            System.out.println("\n--- Available Developers ---");
            displayEmployees(developers);
        }
        System.out.print("Enter new Developer Emp Code (" + bug.geteCode() + "): ");
        String eCodeStr = scanner.nextLine();
        if (!eCodeStr.isEmpty()) {
            try {
                int newECode = Integer.parseInt(eCodeStr);
                if (employeeDAO.getEmployeeByEmpCode(newECode) != null && employeeDAO.getEmployeeByEmpCode(newECode).getRole().equalsIgnoreCase("developer")) {
                    updatedBug.seteCode(newECode);
                } else {
                    System.out.println("Invalid Developer Emp Code. Keeping current.");
                    updatedBug.seteCode(bug.geteCode());
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input for Developer Emp Code. Keeping current.");
                updatedBug.seteCode(bug.geteCode());
            }
        } else {
            updatedBug.seteCode(bug.geteCode());
        }

        // Status
        System.out.print("Enter new Status (pending/resolved) (" + bug.getStatus() + "): ");
        String status = scanner.nextLine();
        if (!status.isEmpty()) {
            if (status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("resolved")) {
                updatedBug.setStatus(status);
            } else {
                System.out.println("Invalid status. Must be 'pending' or 'resolved'. Keeping current.");
                updatedBug.setStatus(bug.getStatus());
            }
        } else {
            updatedBug.setStatus(bug.getStatus());
        }

        // Bug Description
        System.out.print("Enter new Bug Description (" + bug.getBugDes() + "): ");
        String bugDes = scanner.nextLine();
        updatedBug.setBugDes(bugDes.isEmpty() ? bug.getBugDes() : bugDes);


        if (bugReportDAO.updateBugReport(updatedBug)) {
            System.out.println("Bug report updated successfully!");
        } else {
            System.out.println("Failed to update bug report.");
        }
    }

    public void deleteBug() {
        System.out.println("\n--- Delete Bug Report ---");
        System.out.print("Enter Bug No to delete: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Bug No must be a number.");
            scanner.nextLine();
            return;
        }
        int bugNo = scanner.nextInt();
        scanner.nextLine(); // consume newline

        BugReport bug = bugReportDAO.getBugReportByBugNo(bugNo);
        if (bug == null) {
            System.out.println("Bug report with Bug No " + bugNo + " not found.");
            return;
        }

        System.out.print("Are you sure you want to delete Bug No " + bugNo + "? (yes/no): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            if (bugReportDAO.deleteBugReport(bugNo)) {
                System.out.println("Bug report deleted successfully!");
            } else {
                System.out.println("Failed to delete bug report.");
            }
        } else {
            System.out.println("Bug deletion cancelled.");
        }
    }

    // --- Helper for assigning employees to a project ---
    private void assignEmployeesToProject(int projectID) {
        Project project = projectDAO.getProjectByID(projectID);
        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        while (true) {
            System.out.println("\n--- Manage Assignments for Project: " + project.getProjectName() + " (ID: " + projectID + ") ---");
            System.out.println("1. Assign Employee to Project");
            System.out.println("2. View Assigned Employees");
            System.out.println("3. Back to Project Management");
            System.out.print("Enter your choice: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // consume invalid input
                continue;
            }
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("--- Assign Employee ---");
                    List<Employee> allEmployees = employeeDAO.getAllEmployees();
                    if (allEmployees.isEmpty()) {
                        System.out.println("No employees found to assign.");
                        break;
                    }
                    System.out.println("\n--- All Employees ---");
                    displayEmployees(allEmployees);
                    System.out.print("Enter Employee Emp Code to assign to project " + project.getProjectName() + ": ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Invalid input. Emp Code must be a number.");
                        scanner.nextLine();
                        break;
                    }
                    int empCode = scanner.nextInt();
                    scanner.nextLine();

                    Employee empToAssign = employeeDAO.getEmployeeByEmpCode(empCode);
                    if (empToAssign == null) {
                        System.out.println("Employee with Emp Code " + empCode + " not found.");
                        break;
                    }

                    if (employeeDAO.assignProjectToEmployee(projectID, empCode)) {
                        System.out.println("Employee " + empToAssign.getEmpName() + " assigned to project " + project.getProjectName() + " successfully.");
                    } else {
                        System.out.println("Failed to assign employee to project. They might already be assigned.");
                    }
                    break;
                case 2:
                    System.out.println("--- Employees Assigned to Project " + project.getProjectName() + " ---");
                    List<Integer> assignedEmpCodes = employeeDAO.getAssignedEmployeeCodesForProject(projectID);
                    if (assignedEmpCodes.isEmpty()) {
                        System.out.println("No employees assigned to this project.");
                        break;
                    }
                    List<Employee> assignedEmployees = new java.util.ArrayList<>();
                    for (int code : assignedEmpCodes) {
                        Employee assignedEmp = employeeDAO.getEmployeeByEmpCode(code);
                        if (assignedEmp != null) {
                            assignedEmployees.add(assignedEmp);
                        }
                    }
                    displayEmployees(assignedEmployees);
                    break;
                case 3:
                    return; // Exit assignment loop
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }


    // Helper to display employees (copied from AdminService for consistency)
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
