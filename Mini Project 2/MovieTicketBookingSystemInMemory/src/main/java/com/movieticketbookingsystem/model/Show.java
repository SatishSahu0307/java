package com.movieticketbookingsystem.model;

import java.util.Objects;

/**
 * Represents a Show (showtime for a movie) in the Movie Ticket Booking System.
 */
public class Show {
    private int showId;
    private int movieId;
    private String showTime;
    private String showDate; // YYYY-MM-DD
    private int hallNumber;
    private int availableSeats;
    private int totalSeats;
    private double ticketPrice;

    // Constructors
    public Show() {
    }

    public Show(int showId, int movieId, String showTime, String showDate, int hallNumber, int availableSeats, int totalSeats, double ticketPrice) {
        this.showId = showId;
        this.movieId = movieId;
        this.showTime = showTime;
        this.showDate = showDate;
        this.hallNumber = hallNumber;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.ticketPrice = ticketPrice;
    }

    // Getters
    public int getShowId() {
        return showId;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getShowTime() {
        return showTime;
    }

    public String getShowDate() {
        return showDate;
    }

    public int getHallNumber() {
        return hallNumber;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    // Setters
    public void setShowId(int showId) {
        this.showId = showId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public void setHallNumber(int hallNumber) {
        this.hallNumber = hallNumber;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    // toString, equals, hashCode
    @Override
    public String toString() {
        return "Show{" +
                "showId=" + showId +
                ", movieId=" + movieId +
                ", showTime='" + showTime + '\'' +
                ", showDate='" + showDate + '\'' +
                ", hallNumber=" + hallNumber +
                ", availableSeats=" + availableSeats +
                ", totalSeats=" + totalSeats +
                ", ticketPrice=" + ticketPrice +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Show show = (Show) o;
        return showId == show.showId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showId);
    }
}
