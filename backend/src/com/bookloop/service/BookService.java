package com.bookloop.service;

import com.bookloop.comparator.BookAuthorComparator;
import com.bookloop.comparator.BookPriceAscendingComparator;
import com.bookloop.comparator.BookPriceDescendingComparator;
import com.bookloop.comparator.BookTitleComparator;
import com.bookloop.data.DataStore;
import com.bookloop.model.Book;
import com.bookloop.model.Condition;
import com.bookloop.model.ListingType;
import com.bookloop.model.Student;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Service managing book catalog operations: creation, retrieval, search, sorting, and updates.
 * Demonstrates:
 * - Method Overloading (multiple search and query variations)
 * - Comparable and Comparator usage for sorting
 * - Iterator usage for filtering
 * - Business validation
 */
public class BookService {
    private final DataStore store;
    private static int nextBookIdNum = 225;

    public BookService(DataStore store) {
        this.store = store;
    }

    /**
     * Creates and adds a new book listing to the exchange platform.
     */
    public Book addBook(String title, String author, String isbn, String subject,
                        String category, int semester, String edition, Condition condition,
                        double price, ListingType listingType, String preferredExchange,
                        String description, Student seller) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be blank.");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Author name cannot be blank.");
        }
        if (seller == null) {
            throw new IllegalArgumentException("Seller must be a valid registered student.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        String bookId = "BK" + (nextBookIdNum++);
        String dateListed = LocalDate.now().toString();

        Book book = new Book(
                bookId, title, author, isbn, subject, category, semester, edition,
                condition, price, listingType, preferredExchange, description,
                seller.getId(), seller.getName(), seller.getCollege(), seller.getRating(),
                "Active", dateListed
        );

        store.addBook(book);
        return book;
    }

    public Book getBookById(String id) {
        return store.getBook(id);
    }

    public List<Book> getAllBooks() {
        return store.getAllBooks();
    }

    public List<Book> getAvailableBooks() {
        return store.getActiveBooks();
    }

    // ==========================================
    // Method Overloading: Search Methods
    // ==========================================

    /**
     * Search Method 1: Free-text search across title, author, subject, and description.
     */
    public List<Book> searchBooks(String keyword) {
        store.recordSearchKeyword(keyword);
        List<Book> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAvailableBooks();
        }
        for (Book b : store.getAllBooks()) {
            if (b.matchesKeyword(keyword)) {
                results.add(b);
            }
        }
        return results;
    }

    /**
     * Search Method 2: Search by academic subject and semester.
     */
    public List<Book> searchBooks(String subject, int semester) {
        List<Book> results = new ArrayList<>();
        for (Book b : store.getAllBooks()) {
            if (b.matchesSubject(subject) && b.matchesSemester(semester)) {
                results.add(b);
            }
        }
        return results;
    }

    /**
     * Search Method 3: Filter by academic semester alone.
     */
    public List<Book> searchBooksBySemester(int semester) {
        List<Book> results = new ArrayList<>();
        for (Book b : store.getAllBooks()) {
            if (b.matchesSemester(semester)) {
                results.add(b);
            }
        }
        return results;
    }

    /**
     * Search Method 4: Filter specifically by author name.
     */
    public List<Book> searchBooksByAuthor(String author) {
        List<Book> results = new ArrayList<>();
        if (author == null) return results;
        String query = author.toLowerCase().trim();
        for (Book b : store.getAllBooks()) {
            if (b.getAuthor().toLowerCase().contains(query)) {
                results.add(b);
            }
        }
        return results;
    }

    /**
     * Search Method 5: Filter specifically by subject.
     */
    public List<Book> searchBooksBySubject(String subject) {
        List<Book> results = new ArrayList<>();
        if (subject == null) return results;
        for (Book b : store.getAllBooks()) {
            if (b.matchesSubject(subject)) {
                results.add(b);
            }
        }
        return results;
    }

    // ==========================================
    // Sorting with Comparable and Comparators
    // ==========================================

    /**
     * Sorts books naturally using Comparable<Book> (Title A to Z).
     */
    public List<Book> sortBooksNaturally(List<Book> list) {
        List<Book> sorted = new ArrayList<>(list);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Sorts books by title alphabetically using BookTitleComparator.
     */
    public List<Book> sortByTitle(List<Book> list) {
        List<Book> sorted = new ArrayList<>(list);
        sorted.sort(new BookTitleComparator());
        return sorted;
    }

    /**
     * Sorts books by author alphabetically using BookAuthorComparator.
     */
    public List<Book> sortByAuthor(List<Book> list) {
        List<Book> sorted = new ArrayList<>(list);
        sorted.sort(new BookAuthorComparator());
        return sorted;
    }

    /**
     * Sorts books by price from Low to High using BookPriceAscendingComparator.
     */
    public List<Book> sortByPriceLowToHigh(List<Book> list) {
        List<Book> sorted = new ArrayList<>(list);
        sorted.sort(new BookPriceAscendingComparator());
        return sorted;
    }

    /**
     * Sorts books by price from High to Low using BookPriceDescendingComparator.
     */
    public List<Book> sortByPriceHighToLow(List<Book> list) {
        List<Book> sorted = new ArrayList<>(list);
        sorted.sort(new BookPriceDescendingComparator());
        return sorted;
    }

    // ==========================================
    // Update and Delete Operations
    // ==========================================

    public boolean updateBookDetails(String bookId, String newTitle, String newAuthor,
                                     double newPrice, Condition newCondition,
                                     ListingType newType, String newStatus) {
        Book book = store.getBook(bookId);
        if (book == null) return false;

        if (newTitle != null && !newTitle.trim().isEmpty()) book.setTitle(newTitle);
        if (newAuthor != null && !newAuthor.trim().isEmpty()) book.setAuthor(newAuthor);
        if (newPrice >= 0) book.setPrice(newPrice);
        if (newCondition != null) book.setCondition(newCondition);
        if (newType != null) book.setListingType(newType);
        if (newStatus != null && !newStatus.trim().isEmpty()) book.setStatus(newStatus);

        return store.updateBook(book);
    }

    public boolean deleteBook(String bookId, String studentId) {
        Book book = store.getBook(bookId);
        if (book == null) return false;
        // Verify ownership (security check)
        if (!book.getSellerId().equalsIgnoreCase(studentId)) {
            throw new SecurityException("Unauthorized: You can only delete books that you listed.");
        }
        return store.deleteBook(bookId);
    }

    public List<Book> getBooksBySeller(String sellerId) {
        List<Book> sellerBooks = new ArrayList<>();
        for (Book b : store.getAllBooks()) {
            if (b.getSellerId().equalsIgnoreCase(sellerId)) {
                sellerBooks.add(b);
            }
        }
        return sellerBooks;
    }
}
