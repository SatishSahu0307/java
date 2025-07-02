package com.bugtracker.dao;

import com.bugtracker.model.BugReport;
import java.util.List;

public interface BugReportDAO {
    boolean addBugReport(BugReport bugReport);
    BugReport getBugReportByBugNo(int bugNo);
    List<BugReport> getAllBugReports();
    List<BugReport> getBugReportsByProjectID(int projectID);
    List<BugReport> getBugReportsByAssignedDeveloper(int eCode); // Assigned to a specific developer
    List<BugReport> getBugReportsByReporter(int tCode); // Reported by a specific tester
    boolean updateBugReport(BugReport bugReport);
    boolean updateBugStatus(int bugNo, String status); // Specific update for status
    boolean deleteBugReport(int bugNo);
}
