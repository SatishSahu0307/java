package com.movieticketbookingsystem.service;

import com.movieticketbookingsystem.data.InMemoryDataStore;
import com.movieticketbookingsystem.model.User;

import java.util.Optional;

/**
 * Service class for handling user authentication (login) and registration.
 * Interacts with InMemoryDataStore for user data.
 */
public class AuthService {

    /**
     * Authenticates a user based on username and password.
     *
     * @param username The user's username.
     * @param password The user's password.
     * @return The authenticated User object if login is successful, null otherwise.
     */
    public User login(String username, String password) {
        Optional<User> userOpt = InMemoryDataStore.getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) { // In a real app, hash passwords!
                System.out.println("Login successful! Welcome, " + user.getName() + " (" + user.getRole() + ").");
                return user;
            }
        }
        System.out.println("Invalid username or password.");
        return null;
    }

    /**
     * Registers a new user with a default 'user' role.
     *
     * @param username The desired username.
     * @param password The desired password.
     * @param name The user's full name.
     * @param email The user's email.
     * @return true if registration is successful, false otherwise.
     */
    public boolean registerUser(String username, String password, String name, String email) {
        // Basic email format check (can be more robust)
        if (!email.contains("@") || !email.contains(".")) {
            System.out.println("Registration failed: Invalid email format.");
            return false;
        }

        User newUser = new User(0, username, password, name, email, "user"); // userId will be set by DataStore
        if (InMemoryDataStore.addUser(newUser)) {
            System.out.println("Registration successful! Your User ID is: " + newUser.getUserId());
            return true;
        } else {
            System.out.println("Registration failed. Username or Email might already exist.");
            return false;
        }
    }
}
