package com.bugtracker.dao.impl;

import com.bugtracker.dao.BugTypeDAO;
import com.bugtracker.model.BugType;
import com.bugtracker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the BugTypeDAO interface.
 * Handles database operations for the 'BugType' table.
 */
public class JdbcBugTypeDAO implements BugTypeDAO {

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
    public BugType getBugTypeByCode(int bugCode) {
        String sql = "SELECT * FROM BugType WHERE bugCode = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BugType bugType = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bugCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                bugType = new BugType(
                    rs.getInt("bugCode"),
                    rs.getString("bugCategory"),
                    rs.getString("bugSeverity")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting bug type by code: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return bugType;
    }

    @Override
    public List<BugType> getAllBugTypes() {
        String sql = "SELECT * FROM BugType ORDER BY bugCode ASC"; // Order for consistent display
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BugType> bugTypes = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                bugTypes.add(new BugType(
                    rs.getInt("bugCode"),
                    rs.getString("bugCategory"),
                    rs.getString("bugSeverity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all bug types: " + e.getMessage());
        } finally {
            closeResources(rs, pstmt, conn);
        }
        return bugTypes;
    }
}
