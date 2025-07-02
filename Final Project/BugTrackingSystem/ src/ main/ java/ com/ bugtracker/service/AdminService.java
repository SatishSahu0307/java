package com.bugtracker.service;

import com.bugtracker.dao.EmployeeDAO;
import com.bugtracker.dao.BugReportDAO;
import com.bugtracker.dao.ProjectDAO;
import com.bugtracker.dao.impl.JdbcEmployeeDAO;
import com.bugtracker.dao.impl.JdbcBugReportDAO;
import com.bugtracker.dao.impl.JdbcProjectDAO;
import com.bugtracker.model.Employee;
import com.bugtracker.model.BugReport;
import com.bugtracker.model.Project;

import java.util.List;
import java.util.Scanner;

/**
 * Service class for Admin functionalities.
 * Handles operations related to managing managers, employees, viewing projects and bug reports.
 */
public class AdminService {
    private EmployeeDAO employeeDAO;
    private ProjectDAO projectDAO;
    private BugReportDAO bugReportDAO;
    private Scanner scanner;

    public AdminService() {
        this.employeeDAO = new JdbcEmployeeDAO();
        this.projectDAO = new JdbcProjectDAO();
        this.bugReportDAO = new JdbcBugReportDAO();
        this.scanner = new Scanner(System.in);
    }

    // --- Manager Management ---

    public void addManagerAccount() {
        System.out.println("\n--- Add New Manager Account ---");
        Employee manager = getEmployeeDetailsFromUser("manager");
        if (manager != null) {
            if (employeeDAO.addEmployee(manager)) {
                System.out.println("Manager account added successfully!");
            } else {
                System.out.println("Failed to add manager account. Employee code or email might already exist.");
            }
        } else {
            System.out.println("Manager creation cancelled or invalid input.");
        }
    }

    public void viewManagerAccounts() {
        System.out.println("\n--- View Manager Accounts ---");
        List<Employee> managers = employeeDAO.getEmployeesByRole("manager");
        if (managers.isEmpty()) {
            System.out.println("No manager accounts found.");
            return;
        }
        displayEmployees(managers);
    }

    public void deleteManager() {
        System.out.println("\n--- Delete Manager Account ---");
        System.out.print("Enter Manager Emp Code to delete: ");
        int empCode = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Employee manager = employeeDAO.getEmployeeByEmpCode(empCode);
        if (manager == null || !manager.getRole().equalsIgnoreCase("manager")) {
            System.out.println("Manager with Emp Code " + empCode + " not found or is not a manager.");
            return;
        }

        System.out.print("Are you sure you want to delete Manager " + manager.getEmpName() + " (Emp Code: " + empCode + ")? (yes/no): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            if (employeeDAO.deleteEmployee(empCode)) {
                System.out.println("Manager account deleted successfully!");
            } else {
                System.out.println("Failed to delete manager account.");
            }
        } else {
            System.out.println("Manager deletion cancelled.");
        }
    }

    public void updateManagerDetails() {
        System.out.println("\n--- Update Manager Details ---");
        System.out.print("Enter Manager Emp Code to update: ");
        int empCode = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Employee manager = employeeDAO.getEmployeeByEmpCode(empCode);
        if (manager == null || !manager.getRole().equalsIgnoreCase("manager")) {
            System.out.println("Manager with Emp Code " + empCode + " not found or is not a manager.");
            return;
        }

        System.out.println("Current Details for " + manager.getEmpName() + ":");
        System.out.println(manager);
        System.out.println("Enter new details (leave blank to keep current value):");

        // Collect updated details
        Employee updatedManager = getEmployeeDetailsFromUserForUpdate(manager);

        if (employeeDAO.updateEmployee(updatedManager)) {
            System.out.println("Manager details updated successfully!");
        } else {
            System.out.println("Failed to update manager details.");
        }
    }

    // --- Employee Management (Developers/Testers) ---

    public void addEmployeeAccount() {
        System.out.println("\n--- Add New Employee Account (Developer/Tester) ---");
        Employee employee = getEmployeeDetailsFromUserForDevTester();
        if (employee != null) {
            if (employeeDAO.addEmployee(employee)) {
                System.out.println("Employee account added successfully!");
            } else {
                System.out.println("Failed to add employee account. Employee code or email might already exist.");
            }
        } else {
            System.out.println("Employee creation cancelled or invalid input.");
        }
    }

    public void viewEmployeeAccounts() {
        System.out.println("\n--- View All Employee Accounts (Developers/Testers) ---");
        List<Employee> allEmployees = employeeDAO.getAllEmployees();
        List<Employee> employees = new java.util.ArrayList<>();
        for (Employee emp : allEmployees) {
            if (emp.getRole().equalsIgnoreCase("developer") || emp.getRole().equalsIgnoreCase("tester")) {
                employees.add(emp);
            }
        }
        if (employees.isEmpty()) {
            System.out.println("No employee accounts (developers/testers) found.");
            return;
        }
        displayEmployees(employees);
    }

    public void deleteEmployeeAccount() {
        System.out.println("\n--- Delete Employee Account ---");
        System.out.print("Enter Employee Emp Code to delete: ");
        int empCode = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Employee employee = employeeDAO.getEmployeeByEmpCode(empCode);
        if (employee == null || (!employee.getRole().equalsIgnoreCase("developer") && !employee.getRole().equalsIgnoreCase("tester"))) {
            System.out.println("Employee with Emp Code " + empCode + " not found or is not a developer/tester.");
            return;
        }

        System.out.print("Are you sure you want to delete Employee " + employee.getEmpName() + " (Emp Code: " + empCode + ")? (yes/no): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("yes")) {
            if (employeeDAO.deleteEmployee(empCode)) {
                System.out.println("Employee account deleted successfully!");
            } else {
                System.out.println("Failed to delete employee account. Ensure no bug reports are directly assigned to them as TCode/ECode, if so, reassign or delete bug reports first.");
            }
        } else {
            System.out.println("Employee deletion cancelled.");
        }
    }

    public void updateEmployeeDetails() {
        System.out.println("\n--- Update Employee Details ---");
        System.out.print("Enter Employee Emp Code to update: ");
        int empCode = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Employee employee = employeeDAO.getEmployeeByEmpCode(empCode);
        if (employee == null || (!employee.getRole().equalsIgnoreCase("developer") && !employee.getRole().equalsIgnoreCase("tester"))) {
            System.out.println("Employee with Emp Code " + empCode + " not found or is not a developer/tester.");
            return;
        }

        System.out.println("Current Details for " + employee.getEmpName() + ":");
        System.out.println(employee);
        System.out.println("Enter new details (leave blank to keep current value):");

        Employee updatedEmployee = getEmployeeDetailsFromUserForUpdate(employee);

        if (employeeDAO.updateEmployee(updatedEmployee)) {
            System.out.println("Employee details updated successfully!");
        } else {
            System.out.println("Failed to update employee details.");
        }
    }

    // --- Project and Bug Reports ---

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

    public void viewBugReports() {
        System.out.println("\n--- View All Bug Reports ---");
        List<BugReport> bugReports = bugReportDAO.getAllBugReports();
        if (bugReports.isEmpty()) {
            System.out.println("No bug reports found.");
            return;
        }
        displayBugReports(bugReports);
    }

    // --- Helper Methods ---

    private Employee getEmployeeDetailsFromUser(String role) {
        Employee employee = new Employee();
        employee.setRole(role);

        System.out.print("Enter " + role + " Emp Code (numeric, used for login): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input for Emp Code. Must be a number.");
            scanner.nextLine(); // consume invalid input
            return null;
        }
        employee.setEmpCode(scanner.nextInt());
        scanner.nextLine(); // consume newline

        System.out.print("Enter " + role + " Name: ");
        employee.setEmpName(scanner.nextLine());

        System.out.print("Enter " + role + " Email: ");
        employee.setEmpEmail(scanner.nextLine());

        System.out.print("Enter " + role + " Password: ");
        employee.setEmpPassword(scanner.nextLine());

        System.out.print("Enter Gender (Male/Female/Other): ");
        employee.setGender(scanner.nextLine());

        System.out.print("Enter DOB (YYYY-MM-DD): ");
        employee.setDob(scanner.nextLine());

        System.out.print("Enter Mobile Number: ");
        if (!scanner.hasNextLong()) {
            System.out.println("Invalid input for Mobile Number. Must be a number.");
            scanner.nextLine(); // consume invalid input
            return null;
        }
        employee.setMobileNo(scanner.nextLong());
        scanner.nextLine(); // consume newline

        return employee;
    }

    private Employee getEmployeeDetailsFromUserForDevTester() {
        Employee employee = new Employee();

        System.out.print("Enter Employee Role (Developer/Tester): ");
        String role = scanner.nextLine();
        if (!role.equalsIgnoreCase("developer") && !role.equalsIgnoreCase("tester")) {
            System.out.println("Invalid role. Must be 'Developer' or 'Tester'.");
            return null;
        }
        employee.setRole(role);

        System.out.print("Enter Employee Emp Code (numeric, used for login): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Invalid input for Emp Code. Must be a number.");
            scanner.nextLine(); // consume invalid input
            return null;
        }
        employee.setEmpCode(scanner.nextInt());
        scanner.nextLine(); // consume newline

        System.out.print("Enter Employee Name: ");
        employee.setEmpName(scanner.nextLine());

        System.out.print("Enter Employee Email: ");
        employee.setEmpEmail(scanner.nextLine());

        System.out.print("Enter Employee Password: ");
        employee.setEmpPassword(scanner.nextLine());

        System.out.print("Enter Gender (Male/Female/Other): ");
        employee.setGender(scanner.nextLine());

        System.out.print("Enter DOB (YYYY-MM-DD): ");
        employee.setDob(scanner.nextLine());

        System.out.print("Enter Mobile Number: ");
        if (!scanner.hasNextLong()) {
            System.out.println("Invalid input for Mobile Number. Must be a number.");
            scanner.nextLine(); // consume invalid input
            return null;
        }
        employee.setMobileNo(scanner.nextLong());
        scanner.nextLine(); // consume newline

        return employee;
    }


    private Employee getEmployeeDetailsFromUserForUpdate(Employee existingEmployee) {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setEmpCode(existingEmployee.getEmpCode()); // Emp Code cannot be updated

        System.out.print("Enter new Name (" + existingEmployee.getEmpName() + "): ");
        String name = scanner.nextLine();
        updatedEmployee.setEmpName(name.isEmpty() ? existingEmployee.getEmpName() : name);

        System.out.print("Enter new Email (" + existingEmployee.getEmpEmail() + "): ");
        String email = scanner.nextLine();
        updatedEmployee.setEmpEmail(email.isEmpty() ? existingEmployee.getEmpEmail() : email);

        System.out.print("Enter new Password (leave blank to keep current): ");
        String password = scanner.nextLine();
        updatedEmployee.setEmpPassword(password.isEmpty() ? existingEmployee.getEmpPassword() : password);

        System.out.print("Enter new Gender (" + existingEmployee.getGender() + "): ");
        String gender = scanner.nextLine();
        updatedEmployee.setGender(gender.isEmpty() ? existingEmployee.getGender() : gender);

        System.out.print("Enter new DOB (YYYY-MM-DD) (" + existingEmployee.getDob() + "): ");
        String dob = scanner.nextLine();
        updatedEmployee.setDob(dob.isEmpty() ? existingEmployee.getDob() : dob);

        System.out.print("Enter new Mobile Number (" + existingEmployee.getMobileNo() + "): ");
        String mobileStr = scanner.nextLine();
        updatedEmployee.setMobileNo(mobileStr.isEmpty() ? existingEmployee.getMobileNo() : Long.parseLong(mobileStr));

        // Role cannot be changed via update for simplicity; it's set during add.
        updatedEmployee.setRole(existingEmployee.getRole());

        return updatedEmployee;
    }

    private void displayEmployees(List<Employee> employees) {
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

    private void displayBugReports(List<BugReport> bugReports) {
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

    // Helper to close scanner when AdminService is no longer needed (e.g., app shutdown)
    public void closeScanner() {
        scanner.close();
    }
}
