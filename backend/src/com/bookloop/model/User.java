package com.bookloop.model;

import com.bookloop.interfaces.Displayable;
import java.util.Objects;

/**
 * Abstract base class representing a registered user in BookLoop.
 * Demonstrates:
 * - Abstraction (abstract class & abstract method)
 * - Encapsulation (private fields, validated getters/setters)
 * - this keyword & constructor chaining
 * - static counter & static validation
 * - Interface implementation (Displayable)
 */
public abstract class User implements Displayable {
    // Static counter for generating fallback IDs if needed
    private static int userCounter = 100;

    // Encapsulated attributes
    private String id;
    private String name;
    private String email;
    private String phone;
    private String joinedDate;

    /**
     * Default constructor.
     * Demonstrates 'this' constructor chaining.
     */
    public User() {
        this("USR" + (++userCounter), "Anonymous Student", "student@college.edu", "+91 90000 00000", "2026-01-01");
    }

    /**
     * Parameterized constructor.
     *
     * @param id unique user ID
     * @param name full name
     * @param email college email address
     * @param phone mobile phone number
     * @param joinedDate registration date
     */
    public User(String id, String name, String email, String phone, String joinedDate) {
        this.id = id != null ? id.trim() : "USR" + (++userCounter);
        this.setName(name);
        this.setEmail(email);
        this.phone = phone != null ? phone.trim() : "+91 90000 00000";
        this.joinedDate = joinedDate != null ? joinedDate : "2026-01-01";
    }

    // Abstract method to be overridden by subclasses (Polymorphism)
    public abstract void displayProfile();

    // Getters and Setters with Encapsulation & Validation

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id != null && !id.trim().isEmpty()) {
            this.id = id.trim();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.name = "Unknown Student";
        } else {
            this.name = name.trim();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.contains("@") && email.contains(".")) {
            this.email = email.trim().toLowerCase();
        } else {
            this.email = "student@college.edu";
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(String joinedDate) {
        this.joinedDate = joinedDate;
    }

    // Static helper method
    public static boolean isValidCollegeEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User [ID=" + id + ", Name=" + name + ", Email=" + email + "]";
    }
}
