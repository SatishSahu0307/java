package com.bugtracker.dao;

import com.bugtracker.model.Employee;
import java.util.List;

public interface EmployeeDAO {
    boolean addEmployee(Employee employee);
    Employee getEmployeeByEmpCode(int empCode);
    List<Employee> getAllEmployees();
    boolean updateEmployee(Employee employee);
    boolean deleteEmployee(int empCode);
    Employee loginEmployee(int empCode, String password); // For authentication
    List<Employee> getEmployeesByRole(String role); // For filtering managers/developers/testers
    boolean assignProjectToEmployee(int projectID, int empCode); // For AssignProject table
    List<Integer> getAssignedProjectIDsForEmployee(int empCode); // To check employee project assignments
    List<Integer> getAssignedEmployeeCodesForProject(int projectID); // To check project assignments
}
