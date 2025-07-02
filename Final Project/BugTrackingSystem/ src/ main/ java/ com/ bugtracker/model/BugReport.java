package com.bugtracker.model;

import java.util.Objects;

/**
 * Represents a Bug Report in the Bug Tracking System.
 * Corresponds to the 'BugReport' table in the database.
 */
public class BugReport {
    private int bugNo;
    private int bugCode;    // FK to BugType
    private int projectID;  // FK to Project
    private int tCode;      // FK to Employee (Tester Code)
    private int eCode;      // FK to Employee (Developer Code assigned)
    private String status;  // pending, resolved
    private String bugDes;  // Bug Description

    // Constructors
    public BugReport() {
        // Default constructor
    }

    public BugReport(int bugNo, int bugCode, int projectID, int tCode, int eCode, String status, String bugDes) {
        this.bugNo = bugNo;
        this.bugCode = bugCode;
        this.projectID = projectID;
        this.tCode = tCode;
        this.eCode = eCode;
        this.status = status;
        this.bugDes = bugDes;
    }

    // Getters
    public int getBugNo() {
        return bugNo;
    }

    public int getBugCode() {
        return bugCode;
    }

    public int getProjectID() {
        return projectID;
    }

    public int gettCode() {
        return tCode;
    }

    public int geteCode() {
        return eCode;
    }

    public String getStatus() {
        return status;
    }

    public String getBugDes() {
        return bugDes;
    }

    // Setters
    public void setBugNo(int bugNo) {
        this.bugNo = bugNo;
    }

    public void setBugCode(int bugCode) {
        this.bugCode = bugCode;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public void settCode(int tCode) {
        this.tCode = tCode;
    }

    public void seteCode(int eCode) {
        this.eCode = eCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBugDes(String bugDes) {
        this.bugDes = bugDes;
    }

    // toString, equals, hashCode
    @Override
    public String toString() {
        return "BugReport{" +
                "bugNo=" + bugNo +
                ", bugCode=" + bugCode +
                ", projectID=" + projectID +
                ", tCode=" + tCode +
                ", eCode=" + eCode +
                ", status='" + status + '\'' +
                ", bugDes='" + bugDes + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BugReport bugReport = (BugReport) o;
        return bugNo == bugReport.bugNo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bugNo);
    }
}
