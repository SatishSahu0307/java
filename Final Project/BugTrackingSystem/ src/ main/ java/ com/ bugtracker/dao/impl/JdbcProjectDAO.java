package com.bugtracker.dao.impl;

import com.bugtracker.dao.ProjectDAO;
import com.bugtracker.model.Project;
import com.bugtracker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the ProjectDAO interface.
 * Handles database operations for the 'Project' table.
 */
public class JdbcProjectDAO implements ProjectDAO {

    /**
     * Common helper method to close JDBC resources (ResultSet, PreparedStatement, Connection).
     * This method can be called multiple times across different DAO methods to avoid code duplication.
     * @param rs The ResultSet to close (can be null).
     * @param pstmt The PreparedStatement to close (can be null).
     * @param conn The Connection to close (can be null).
     */
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing JDBC resources: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Always close the connection via DBConnection utility
            DBConnection.closeConnection(conn);
        }
    }


    @Override
    public boolean addProject(Project project) {
        // projectID is AUTO_INCREMENT, so we don't set it in INSERT
        String sql = "INSERT INTO Project (projectName, SDate, EDate, projectDec) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DBConnection.getConnection();
            // Use RETURN_GENERATED_KEYS to get the auto-generated projectID
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, project.getProjectName());
            pstmt.setString(2, project.getsDate());
            pstmt.setString(3, project.geteDate());
            pstmt.setString(4, project.getProjectDec());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    project.setProjectID(generatedKeys.getInt(1)); // Set the generated ID back to the object
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding project: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                System.err.println("Project with name '" + project.getProjectName() + "' already exists.");
            }
            return false;
        } finally {
            // Calling the common helper method to close resources
            closeResources(generatedKeys, pstmt, conn);
        }
    }

    @Override
    public Project getProjectByID(int projectID) {
        String sql = "SELECT * FROM Project WHERE projectID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Project project = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectID);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                project = new Project(
                    rs.getInt("projectID"),
                    rs.getString("projectName"),
                    rs.getString("SDate"),
                    rs.getString("EDate"),
                    rs.getString("projectDec")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting project by ID: " + e.getMessage());
        } finally {
            // Calling the common helper method to close resources
            closeResources(rs, pstmt, conn);
        }
        return project;
    }

    @Override
    public List<Project> getAllProjects() {
        String sql = "SELECT * FROM Project";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Project> projects = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(new Project(
                    rs.getInt("projectID"),
                    rs.getString("projectName"),
                    rs.getString("SDate"),
                    rs.getString("EDate"),
                    rs.getString("projectDec")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all projects: " + e.getMessage());
        } finally {
            // Calling the common helper method to close resources
            closeResources(rs, pstmt, conn);
        }
        return projects;
    }

    @Override
    public boolean updateProject(Project project) {
        String sql = "UPDATE Project SET projectName=?, SDate=?, EDate=?, projectDec=? WHERE projectID=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, project.getProjectName());
            pstmt.setString(2, project.getsDate());
            pstmt.setString(3, project.geteDate());
            pstmt.setString(4, project.getProjectDec());
            pstmt.setInt(5, project.getProjectID());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating project: " + e.getMessage());
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                System.err.println("Project with name '" + project.getProjectName() + "' already exists.");
            }
            return false;
        } finally {
            // Calling the common helper method to close resources
            closeResources(null, pstmt, conn); // No ResultSet in update operations
        }
    }

    @Override
    public boolean deleteProject(int projectID) {
        String sql = "DELETE FROM Project WHERE projectID=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            // Important: Handle foreign key constraints before deleting a project.
            // Delete associated entries in AssignProject and BugReport first.
            deleteProjectAssignments(projectID, conn);
            deleteBugReportsForProject(projectID, conn);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectID);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting project: " + e.getMessage());
            return false;
        } finally {
            // Calling the common helper method to close resources
            closeResources(null, pstmt, conn); // No ResultSet in delete operations
        }
    }

    /**
     * Helper method to delete entries in AssignProject table for a given project.
     * @param projectID The project ID.
     * @param conn The active database connection.
     * @throws SQLException If a database access error occurs.
     */
    private void deleteProjectAssignments(int projectID, Connection conn) throws SQLException {
        String sql = "DELETE FROM AssignProject WHERE projectID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectID);
            pstmt.executeUpdate();
            System.out.println("Removed employee assignments for project ID: " + projectID);
        }
    }

    /**
     * Helper method to delete entries in BugReport table for a given project.
     * @param projectID The project ID.
     * @param conn The active database connection.
     * @throws SQLException If a database access error occurs.
     */
    private void deleteBugReportsForProject(int projectID, Connection conn) throws SQLException {
        String sql = "DELETE FROM BugReport WHERE projectID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectID);
            pstmt.executeUpdate();
            System.out.println("Removed bug reports for project ID: " + projectID);
        }
    }
}
