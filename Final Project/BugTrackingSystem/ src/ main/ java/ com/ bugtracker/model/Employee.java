package com.bugtracker.model;

import java.util.Objects;

/**
 * Represents an Employee (user) in the Bug Tracking System.
 * Corresponds to the 'Employee' table in the database.
 */
public class Employee {
    private int empCode;
    private String empName;
    private String empEmail;
    private String empPassword;
    private String gender;
    private String dob; // Date of Birth
    private long mobileNo;
    private String role; // manager, developer, tester, admin

    // Constructors

    public Employee() {
        // Default constructor
    }

    public Employee(int empCode, String empName, String empEmail, String empPassword,
                    String gender, String dob, long mobileNo, String role) {
        this.empCode = empCode;
        this.empName = empName;
        this.empEmail = empEmail;
        this.empPassword = empPassword;
        this.gender = gender;
        this.dob = dob;
        this.mobileNo = mobileNo;
        this.role = role;
    }

    // Getters

    public int getEmpCode() {
        return empCode;
    }

    public String getEmpName() {
        return empName;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public String getEmpPassword() {
        return empPassword;
    }

    public String getGender() {
        return gender;
    }

    public String getDob() {
        return dob;
    }

    public long getMobileNo() {
        return mobileNo;
    }

    public String getRole() {
        return role;
    }

    // Setters

    public void setEmpCode(int empCode) {
        this.empCode = empCode;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public void setEmpEmail(String empEmail) {
        this.empEmail = empEmail;
    }

    public void setEmpPassword(String empPassword) {
        this.empPassword = empPassword;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setMobileNo(long mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // toString, equals, hashCode (for better object handling)

    @Override
    public String toString() {
        return "Employee{" +
                "empCode=" + empCode +
                ", empName='" + empName + '\'' +
                ", empEmail='" + empEmail + '\'' +
                ", gender='" + gender + '\'' +
                ", DOB='" + dob + '\'' +
                ", mobileNo=" + mobileNo +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return empCode == employee.empCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(empCode);
    }
}
