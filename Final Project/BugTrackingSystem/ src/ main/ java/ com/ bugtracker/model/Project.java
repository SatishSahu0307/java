package com.bugtracker.model;

import java.util.Objects;

/**
 * Represents a Project in the Bug Tracking System.
 * Corresponds to the 'Project' table in the database.
 */
public class Project {
    private int projectID;
    private String projectName;
    private String sDate; // Start Date
    private String eDate; // End Date
    private String projectDec; // Project Description

    // Constructors
    public Project() {
        // Default constructor
    }

    public Project(int projectID, String projectName, String sDate, String eDate, String projectDec) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.sDate = sDate;
        this.eDate = eDate;
        this.projectDec = projectDec;
    }

    // Getters
    public int getProjectID() {
        return projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getsDate() {
        return sDate;
    }

    public String geteDate() {
        return eDate;
    }

    public String getProjectDec() {
        return projectDec;
    }

    // Setters
    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }

    public void setProjectDec(String projectDec) {
        this.projectDec = projectDec;
    }

    // toString, equals, hashCode
    @Override
    public String toString() {
        return "Project{" +
                "projectID=" + projectID +
                ", projectName='" + projectName + '\'' +
                ", sDate='" + sDate + '\'' +
                ", eDate='" + eDate + '\'' +
                ", projectDec='" + projectDec + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return projectID == project.projectID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectID);
    }
}
