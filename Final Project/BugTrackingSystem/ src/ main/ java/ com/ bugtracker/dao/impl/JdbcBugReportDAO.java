package com.bugtracker.dao.impl;

import com.bugtracker.dao.BugReportDAO;
import com.bugtracker.model.BugReport;
import com.bugtracker.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the BugReportDAO interface.
 * Handles database operations for the 'BugReport' table.
 */
public class JdbcBugReportDAO implements BugReportDAO {

    @Override
    public boolean addBugReport(BugReport bugReport) {
        String sql = "INSERT INTO BugReport (bugCode, projectID, TCode, ECode, status, bugDes) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, bugReport.getBugCode());
            pstmt.setInt(2, bugReport.getProjectID());
            pstmt.setInt(3, bugReport.gettCode());
            pstmt.setInt(4, bugReport.geteCode());
            pstmt.setString(5, bugReport.getStatus());
            pstmt.setString(6, bugReport.getBugDes());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    bugReport.setBugNo(generatedKeys.getInt(1)); // Set the generated bugNo
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error adding bug report: " + e.getMessage());
            // Check for foreign key constraint violation
            if (e.getErrorCode() == 1452) { // MySQL error code for foreign key constraint fail
                System.err.println("Invalid Bug Code, Project ID, Tester Code, or Employee Code. Please ensure all related entities exist.");
            }
            return false;
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
    }

    @Override
    public BugReport getBugReportByBugNo(int bugNo) {
        String sql = "SELECT * FROM BugReport WHERE bugNo = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        BugReport bugReport = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bugNo);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                bugReport = new BugReport(
                    rs.getInt("bugNo"),
                    rs.getInt("bugCode"),
                    rs.getInt("projectID"),
                    rs.getInt("TCode"),
                    rs.getInt("ECode"),
                    rs.getString("status"),
                    rs.getString("bugDes")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting bug report by bugNo: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return bugReport;
    }

    @Override
    public List<BugReport> getAllBugReports() {
        String sql = "SELECT * FROM BugReport";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BugReport> bugReports = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                bugReports.add(new BugReport(
                    rs.getInt("bugNo"),
                    rs.getInt("bugCode"),
                    rs.getInt("projectID"),
                    rs.getInt("TCode"),
                    rs.getInt("ECode"),
                    rs.getString("status"),
                    rs.getString("bugDes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all bug reports: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return bugReports;
    }

    @Override
    public List<BugReport> getBugReportsByProjectID(int projectID) {
        String sql = "SELECT * FROM BugReport WHERE projectID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BugReport> bugReports = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, projectID);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                bugReports.add(new BugReport(
                    rs.getInt("bugNo"),
                    rs.getInt("bugCode"),
                    rs.getInt("projectID"),
                    rs.getInt("TCode"),
                    rs.getInt("ECode"),
                    rs.getString("status"),
                    rs.getString("bugDes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bug reports by project ID: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return bugReports;
    }

    @Override
    public List<BugReport> getBugReportsByAssignedDeveloper(int eCode) {
        String sql = "SELECT * FROM BugReport WHERE ECode = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BugReport> bugReports = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, eCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                bugReports.add(new BugReport(
                    rs.getInt("bugNo"),
                    rs.getInt("bugCode"),
                    rs.getInt("projectID"),
                    rs.getInt("TCode"),
                    rs.getInt("ECode"),
                    rs.getString("status"),
                    rs.getString("bugDes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bug reports by assigned developer: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return bugReports;
    }

    @Override
    public List<BugReport> getBugReportsByReporter(int tCode) {
        String sql = "SELECT * FROM BugReport WHERE TCode = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<BugReport> bugReports = new ArrayList<>();
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, tCode);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                bugReports.add(new BugReport(
                    rs.getInt("bugNo"),
                    rs.getInt("bugCode"),
                    rs.getInt("projectID"),
                    rs.getInt("TCode"),
                    rs.getInt("ECode"),
                    rs.getString("status"),
                    rs.getString("bugDes")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error getting bug reports by reporter: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DBConnection.closeConnection(conn);
        }
        return bugReports;
    }

    @Override
    public boolean updateBugReport(BugReport bugReport) {
        String sql = "UPDATE BugReport SET bugCode=?, projectID=?, TCode=?, ECode=?, status=?, bugDes=? WHERE bugNo=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bugReport.getBugCode());
            pstmt.setInt(2, bugReport.getProjectID());
            pstmt.setInt(3, bugReport.gettCode());
            pstmt.setInt(4, bugReport.geteCode());
            pstmt.setString(5, bugReport.getStatus());
            pstmt.setString(6, bugReport.getBugDes());
            pstmt.setInt(7, bugReport.getBugNo());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bug report: " + e.getMessage());
            if (e.getErrorCode() == 1452) { // Foreign key constraint violation
                System.err.println("Invalid Bug Code, Project ID, Tester Code, or Employee Code provided.");
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
    public boolean updateBugStatus(int bugNo, String status) {
        String sql = "UPDATE BugReport SET status=? WHERE bugNo=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, bugNo);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating bug status: " + e.getMessage());
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
    public boolean deleteBugReport(int bugNo) {
        String sql = "DELETE FROM BugReport WHERE bugNo=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bugNo);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting bug report: " + e.getMessage());
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
}
