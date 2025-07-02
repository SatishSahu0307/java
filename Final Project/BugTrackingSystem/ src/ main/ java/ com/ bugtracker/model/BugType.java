package com.bugtracker.model;

import java.util.Objects;

/**
 * Represents a Bug Type in the Bug Tracking System.
 * Corresponds to the 'BugType' table in the database.
 */
public class BugType {
    private int bugCode;
    private String bugCategory;  // e.g., Functional Errors, Compilation Errors
    private String bugSeverity;  // e.g., Critical, Major, Medium, Low

    // Constructors
    public BugType() {
        // Default constructor
    }

    public BugType(int bugCode, String bugCategory, String bugSeverity) {
        this.bugCode = bugCode;
        this.bugCategory = bugCategory;
        this.bugSeverity = bugSeverity;
    }

    // Getters
    public int getBugCode() {
        return bugCode;
    }

    public String getBugCategory() {
        return bugCategory;
    }

    public String getBugSeverity() {
        return bugSeverity;
    }

    // Setters
    public void setBugCode(int bugCode) {
        this.bugCode = bugCode;
    }

    public void setBugCategory(String bugCategory) {
        this.bugCategory = bugCategory;
    }

    public void setBugSeverity(String bugSeverity) {
        this.bugSeverity = bugSeverity;
    }

    // toString, equals, hashCode
    @Override
    public String toString() {
        return "BugType{" +
                "bugCode=" + bugCode +
                ", bugCategory='" + bugCategory + '\'' +
                ", bugSeverity='" + bugSeverity + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BugType bugType = (BugType) o;
        return bugCode == bugType.bugCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bugCode);
    }
}
