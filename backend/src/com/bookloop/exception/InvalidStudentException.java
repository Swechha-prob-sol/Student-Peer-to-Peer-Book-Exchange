package com.bookloop.exception;

/**
 * Custom unchecked exception thrown when a student is invalid or not found.
 * Demonstrates:
 * - Custom unchecked exception (extends RuntimeException)
 * - Difference between checked and unchecked exceptions
 * - Constructor overloading
 */
public class InvalidStudentException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String studentId;

    public InvalidStudentException(String studentId) {
        super("Invalid or unregistered student: " + studentId);
        this.studentId = studentId;
    }

    public InvalidStudentException(String studentId, String message) {
        super(message);
        this.studentId = studentId;
    }

    public InvalidStudentException(String studentId, String message, Throwable cause) {
        super(message, cause);
        this.studentId = studentId;
    }

    public String getStudentId() {
        return studentId;
    }
}
