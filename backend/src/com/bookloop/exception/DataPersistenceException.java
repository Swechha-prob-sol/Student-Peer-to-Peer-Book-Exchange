package com.bookloop.exception;

/**
 * Custom unchecked exception thrown when file persistence operations fail.
 * Demonstrates:
 * - Runtime exception for I/O-related business logic failures
 * - Wrapping lower-level IOException into domain exception
 */
public class DataPersistenceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataPersistenceException(String message) {
        super(message);
    }

    public DataPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
