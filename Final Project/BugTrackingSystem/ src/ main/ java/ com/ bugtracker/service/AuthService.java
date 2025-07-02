package com.bugtracker.service;

import com.bugtracker.dao.EmployeeDAO;
import com.bugtracker.dao.impl.JdbcEmployeeDAO;
import com.bugtracker.model.Employee;

/**
 * Service class for handling user authentication (login).
 */
public class AuthService {
    private EmployeeDAO employeeDAO;

    public AuthService() {
        this.employeeDAO = new JdbcEmployeeDAO();
    }

    /**
     * Authenticates an employee based on empCode and password.
     *
     * @param empCode The employee's code (username).
     * @param password The employee's password.
     * @return The authenticated Employee object if login is successful, null otherwise.
     */
    public Employee login(int empCode, String password) {
        Employee employee = employeeDAO.loginEmployee(empCode, password);
        if (employee != null) {
            System.out.println("Login successful! Welcome, " + employee.getEmpName() + " (" + employee.getRole() + ").");
        } else {
            System.out.println("Invalid employee code or password.");
        }
        return employee;
    }
}
