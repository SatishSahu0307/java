package com.movieticketbookingsystem.model;

import java.util.Objects;

/**
 * Represents a Booking in the Movie Ticket Booking System.
 */
public class Booking {
    private int bookingId;
    private int userId;
    private int showId;
    private int numberOfTickets;
    private String bookingDate; // YYYY-MM-DD
    private double totalAmount;
    private String status; // 'confirmed', 'cancelled'

    // Constructors
    public Booking() {
    }

    public Booking(int bookingId, int userId, int showId, int numberOfTickets, String bookingDate, double totalAmount, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.showId = showId;
        this.numberOfTickets = numberOfTickets;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public int getShowId() {
        return showId;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // toString, equals, hashCode
    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", userId=" + userId +
                ", showId=" + showId +
                ", numberOfTickets=" + numberOfTickets +
                ", bookingDate='" + bookingDate + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
}
