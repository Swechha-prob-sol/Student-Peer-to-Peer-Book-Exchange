package com.bookloop.exception;

/**
 * Custom checked exception thrown when a book is not found in the system.
 * Demonstrates:
 * - Custom checked exception (extends Exception)
 * - Exception propagation with 'throws'
 * - Constructor chaining with super()
 * - Multiple constructors for flexibility
 */
public class BookNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String bookId;

    /**
     * Constructor with book ID.
     */
    public BookNotFoundException(String bookId) {
        super("Book not found with ID: " + bookId);
        this.bookId = bookId;
    }

    /**
     * Constructor with book ID and custom message.
     */
    public BookNotFoundException(String bookId, String message) {
        super(message);
        this.bookId = bookId;
    }

    /**
     * Constructor with book ID, message, and cause (exception chaining).
     */
    public BookNotFoundException(String bookId, String message, Throwable cause) {
        super(message, cause);
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }
}
