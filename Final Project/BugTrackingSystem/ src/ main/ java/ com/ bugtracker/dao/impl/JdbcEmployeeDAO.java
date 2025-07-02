package com.bugtracker.dao.impl;

import com.bugtracker.dao.EmployeeDAO;
import com.bugtracker.model.Employee;
import com.bugtracker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the EmployeeDAO interface.
 * Handles database operations for the 'Employee' table.
 */
public class JdbcEmployeeDAO implements EmployeeDAO {

    @Override
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO Employee (empCode, empName, empEmail, empPassword, gender, DOB, mobileNo, Role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, employee.getEmpCode());
            pstmt.setString(2, employee.getEmpName());
            pstmt.setString(3, employee.getEmpEmail());
            pstmt.setString(4, employee.getEmpPassword());
            pstmt.setString(5, employee.getGender());
            pstmt.setString(6, employee.getDob());
            pstmt.setLong(7, employee.getMobileNo());
            pstmt.setString(8, employee.getRole());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            // Check for duplicate entry error if empCode is already present or empEmail is not unique
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                System.err.println("Employee with Emp Code " + employee.getEmpCode() + " or Email " + employee.getEmpEmail() + " already exists.");
            }
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Employee getEmployeeByEmpCode(int empCode) {
        String sql = "SELECT * FROM Employee WHERE empCode = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Employee employee = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                employee = new Employee(
                    rs.getInt("empCode"),
                    rs.getString("empName"),
                    rs.getString("empEmail"),
                    rs.getString("empPassword"),
                    rs.getString("gender"),
                    rs.getString("DOB"),
                    rs.getLong("mobileNo"),
                    rs.getString("Role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee by empCode: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return employee;
    }

    @Override
    public List<Employee> getAllEmployees() {
        String sql = "SELECT * FROM Employee";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Employee> employees = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                employees.add(new Employee(
                    rs.getInt("empCode"),
                    rs.getString("empName"),
                    rs.getString("empEmail"),
                    rs.getString("empPassword"),
                    rs.getString("gender"),
                    rs.getString("DOB"),
                    rs.getLong("mobileNo"),
                    rs.getString("Role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all employees: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return employees;
    }

    @Override
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE Employee SET empName=?, empEmail=?, empPassword=?, gender=?, DOB=?, mobileNo=?, Role=? WHERE empCode=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, employee.getEmpName());
            pstmt.setString(2, employee.getEmpEmail());
            pstmt.setString(3, employee.getEmpPassword());
            pstmt.setString(4, employee.getGender());
            pstmt.setString(5, employee.getDob());
            pstmt.setLong(6, employee.getMobileNo());
            pstmt.setString(7, employee.getRole());
            pstmt.setInt(8, employee.getEmpCode());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            // Check for duplicate email error
            if (e.getErrorCode() == 1062) {
                System.err.println("Another employee with the email '" + employee.getEmpEmail() + "' already exists.");
            }
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean deleteEmployee(int empCode) {
        String sql = "DELETE FROM Employee WHERE empCode=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            // Before deleting employee, also delete their project assignments
            // This prevents foreign key constraint violation
            deleteEmployeeProjectAssignments(empCode, conn);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empCode);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            // If there are other foreign key constraints (e.g., in BugReport as TCode/ECode),
            // you'd need to handle them (e.g., set to NULL, delete cascadingly, or prevent deletion).
            // For simplicity, we are handling AssignProject here.
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Helper method to delete entries in AssignProject table for a given employee.
     * @param empCode The employee code.
     * @param conn The active database connection.
     * @throws SQLException If a database access error occurs.
     */
    private void deleteEmployeeProjectAssignments(int empCode, Connection conn) throws SQLException {
        String sql = "DELETE FROM AssignProject WHERE empCode = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, empCode);
            pstmt.executeUpdate();
            System.out.println("Removed project assignments for empCode: " + empCode);
        }
    }


    @Override
    public Employee loginEmployee(int empCode, String password) {
        String sql = "SELECT * FROM Employee WHERE empCode = ? AND empPassword = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Employee employee = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empCode);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                employee = new Employee(
                    rs.getInt("empCode"),
                    rs.getString("empName"),
                    rs.getString("empEmail"),
                    rs.getString("empPassword"),
                    rs.getString("gender"),
                    rs.getString("DOB"),
                    rs.getLong("mobileNo"),
                    rs.getString("Role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error during employee login: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return employee;
    }

    @Override
    public List<Employee> getEmployeesByRole(String role) {
        String sql = "SELECT * FROM Employee WHERE Role = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Employee> employees = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, role);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                employees.add(new Employee(
                    rs.getInt("empCode"),
                    rs.getString("empName"),
                    rs.getString("empEmail"),
                    rs.getString("empPassword"),
                    rs.getString("gender"),
                    rs.getString("DOB"),
                    rs.getLong("mobileNo"),
                    rs.getString("Role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting employees by role: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return employees;
    }

    @Override
    public boolean assignProjectToEmployee(int projectID, int empCode) {
        String sql = "INSERT INTO AssignProject (projectID, empCode) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectID);
            pstmt.setInt(2, empCode);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error assigning project " + projectID + " to employee " + empCode + ": " + e.getMessage());
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("Employee " + empCode + " is already assigned to project " + projectID + ".");
            } else if (e.getErrorCode() == 1452) { // Foreign key constraint fails
                System.err.println("Invalid Project ID or Employee Code. Please ensure both exist.");
            }
            return false;
        } finally {
            DBConnection.closeConnection(conn);
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Integer> getAssignedProjectIDsForEmployee(int empCode) {
        List<Integer> projectIDs = new ArrayList<>();
        String sql = "SELECT projectID FROM AssignProject WHERE empCode = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, empCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                projectIDs.add(rs.getInt("projectID"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching assigned projects for employee " + empCode + ": " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return projectIDs;
    }

    @Override
    public List<Integer> getAssignedEmployeeCodesForProject(int projectID) {
        List<Integer> empCodes = new ArrayList<>();
        String sql = "SELECT empCode FROM AssignProject WHERE projectID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                empCodes.add(rs.getInt("empCode"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching assigned employees for project " + projectID + ": " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return empCodes;
    }
}
