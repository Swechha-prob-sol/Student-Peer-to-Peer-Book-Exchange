package com.bookloop.data;

import com.bookloop.model.Book;
import com.bookloop.model.ExchangeRequest;
import com.bookloop.model.Rating;
import com.bookloop.model.Student;
import com.bookloop.persistence.FileStorageManager;

import java.io.IOException;
import java.util.*;

/**
 * In-memory Data Storage layer for BookLoop.
 * Demonstrates the core essential Java Collections Framework:
 * - HashMap: O(1) primary key lookup for students, books, and requests by unique ID
 * - HashSet: Fast constant-time uniqueness check to prevent duplicate email registrations
 * - ArrayList: Dynamic resizable lists for books, ratings, search results, and activity logs
 * - Collections utilities: sort(), unmodifiableList(), unmodifiableSet()
 */
public class DataStore {

    // Singleton instance
    private static DataStore instance;

    // 1. HashMap: Fast primary key lookups by ID
    private final Map<String, Student> studentsMap = new HashMap<>();
    private final Map<String, Book> booksMap = new HashMap<>();
    private final Map<String, ExchangeRequest> requestsMap = new HashMap<>();

    // 2. HashSet: Constant-time uniqueness check for student emails & subjects
    private final Set<String> registeredEmails = new HashSet<>();
    private final Set<String> uniqueSubjects = new HashSet<>();
    private final Set<String> uniqueAuthors = new HashSet<>();

    // 3. ArrayList: Dynamic arrays for ratings, activity logs, and searches
    private final List<Rating> ratingsList = new ArrayList<>();
    private final List<String> activityLog = new ArrayList<>();
    private final List<String> recentSearches = new ArrayList<>();

    // Private constructor for singleton pattern
    private DataStore() {
        // Initialized via DummyData or file loading
    }

    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    // ==========================================
    // File Persistence: Load & Save
    // ==========================================

    /**
     * Loads all data from CSV files into memory.
     * Returns true if data was loaded from files, false if no saved data exists.
     */
    public boolean loadFromFiles() throws IOException {
        if (!FileStorageManager.savedDataExists()) {
            return false;
        }

        clearAll();

        // 1. Load Students
        List<Student> loadedStudents = FileStorageManager.loadStudents();
        for (Student s : loadedStudents) {
            studentsMap.put(s.getId(), s);
            registeredEmails.add(s.getEmail().toLowerCase().trim());
        }

        // 2. Load Books
        List<Book> loadedBooks = FileStorageManager.loadBooks();
        for (Book b : loadedBooks) {
            booksMap.put(b.getId(), b);
            uniqueSubjects.add(b.getSubject());
            uniqueAuthors.add(b.getAuthor());
        }

        // 3. Load Requests
        List<ExchangeRequest> loadedRequests = FileStorageManager.loadRequests();
        for (ExchangeRequest r : loadedRequests) {
            requestsMap.put(r.getId(), r);
        }

        // 4. Load Ratings
        List<Rating> loadedRatings = FileStorageManager.loadRatings();
        ratingsList.addAll(loadedRatings);

        logActivity("Loaded " + studentsMap.size() + " students, " + booksMap.size() +
                " books, " + requestsMap.size() + " requests from files.");
        return true;
    }

    /**
     * Saves all current in-memory data to CSV files.
     */
    public void saveToFiles() throws IOException {
        synchronized (this) {
            boolean success = false;
            try {
                FileStorageManager.saveStudents(getAllStudents());
                FileStorageManager.saveBooks(getAllBooks());
                FileStorageManager.saveRequests(getAllRequests());
                FileStorageManager.saveRatings(new ArrayList<>(ratingsList));
                success = true;
            } finally {
                if (success) {
                    logActivity("All data saved to files successfully.");
                } else {
                    logActivity("Data save attempt failed.");
                }
            }
        }
    }

    // ==========================================
    // Student Operations
    // ==========================================

    public boolean addStudent(Student student) {
        if (student == null || student.getId() == null) return false;
        String emailLower = student.getEmail().toLowerCase().trim();
        if (registeredEmails.contains(emailLower)) {
            return false; // Prevent duplicate email registration using HashSet
        }
        studentsMap.put(student.getId(), student);
        registeredEmails.add(emailLower);
        logActivity("New student registered: " + student.getName() + " (" + student.getCollege() + ")");
        return true;
    }

    public Student getStudent(String id) {
        if (id == null) return null;
        return studentsMap.get(id.trim());
    }

    public Student getStudentByEmail(String email) {
        if (email == null) return null;
        for (Student s : studentsMap.values()) {
            if (s.getEmail().equalsIgnoreCase(email.trim())) {
                return s;
            }
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(studentsMap.values());
    }

    public boolean isEmailRegistered(String email) {
        return email != null && registeredEmails.contains(email.toLowerCase().trim());
    }

    // ==========================================
    // Book Operations
    // ==========================================

    public boolean addBook(Book book) {
        if (book == null || book.getId() == null) return false;

        booksMap.put(book.getId(), book);
        uniqueSubjects.add(book.getSubject());
        uniqueAuthors.add(book.getAuthor());

        logActivity("Book listed: \"" + book.getTitle() + "\" by " + book.getSellerName());
        return true;
    }

    public Book getBook(String id) {
        if (id == null) return null;
        return booksMap.get(id.trim());
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(booksMap.values());
    }

    public List<Book> getActiveBooks() {
        List<Book> active = new ArrayList<>();
        for (Book b : booksMap.values()) {
            if (b.isAvailable()) {
                active.add(b);
            }
        }
        return active;
    }

    public boolean updateBook(Book updatedBook) {
        if (updatedBook == null || !booksMap.containsKey(updatedBook.getId())) {
            return false;
        }
        booksMap.put(updatedBook.getId(), updatedBook);
        logActivity("Book updated: [" + updatedBook.getId() + "] " + updatedBook.getTitle());
        return true;
    }

    public boolean deleteBook(String id) {
        if (id == null || !booksMap.containsKey(id)) {
            return false;
        }
        booksMap.remove(id);
        logActivity("Book removed from catalog: [" + id + "]");
        return true;
    }

    public Set<String> getUniqueSubjects() {
        return Collections.unmodifiableSet(uniqueSubjects);
    }

    public Set<String> getUniqueAuthors() {
        return Collections.unmodifiableSet(uniqueAuthors);
    }

    // ==========================================
    // Request Operations
    // ==========================================

    public boolean addRequest(ExchangeRequest request) {
        if (request == null || request.getId() == null) return false;
        requestsMap.put(request.getId(), request);
        logActivity(request.getRequestType() + " request sent for: \"" + request.getBookTitle() + "\"");
        return true;
    }

    public ExchangeRequest getRequest(String id) {
        if (id == null) return null;
        return requestsMap.get(id.trim());
    }

    public List<ExchangeRequest> getAllRequests() {
        return new ArrayList<>(requestsMap.values());
    }

    public List<ExchangeRequest> getRequestsForSeller(String sellerId) {
        List<ExchangeRequest> res = new ArrayList<>();
        for (ExchangeRequest req : requestsMap.values()) {
            if (req.getSellerId().equalsIgnoreCase(sellerId)) {
                res.add(req);
            }
        }
        Collections.sort(res); // Sorted by natural Comparable (date desc)
        return res;
    }

    public List<ExchangeRequest> getRequestsByRequester(String requesterId) {
        List<ExchangeRequest> res = new ArrayList<>();
        for (ExchangeRequest req : requestsMap.values()) {
            if (req.getRequesterId().equalsIgnoreCase(requesterId)) {
                res.add(req);
            }
        }
        Collections.sort(res);
        return res;
    }

    // ==========================================
    // Rating Operations
    // ==========================================

    public void addRating(Rating rating) {
        if (rating == null) return;
        ratingsList.add(rating);
        Student seller = getStudent(rating.getSellerId());
        if (seller != null) {
            seller.addRating(rating.getRating());
        }
        logActivity("Review submitted for seller " + rating.getSellerId() + ": " + rating.getRating() + "★");
    }

    public List<Rating> getRatingsForSeller(String sellerId) {
        List<Rating> res = new ArrayList<>();
        for (Rating r : ratingsList) {
            if (r.getSellerId().equalsIgnoreCase(sellerId)) {
                res.add(r);
            }
        }
        return res;
    }

    public List<Rating> getAllRatings() {
        return Collections.unmodifiableList(ratingsList);
    }

    // ==========================================
    // Search History & Activity Log
    // ==========================================

    public void recordSearchKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            recentSearches.add(keyword.trim());
            if (recentSearches.size() > 10) {
                recentSearches.remove(0);
            }
        }
    }

    public List<String> getRecentSearches() {
        return Collections.unmodifiableList(recentSearches);
    }

    public void logActivity(String log) {
        activityLog.add(0, "[" + new Date() + "] " + log);
        if (activityLog.size() > 50) {
            activityLog.remove(activityLog.size() - 1);
        }
    }

    public List<String> getActivityLog() {
        return Collections.unmodifiableList(activityLog);
    }

    // ==========================================
    // Reset DataStore to clean state
    // ==========================================

    public void clearAll() {
        studentsMap.clear();
        booksMap.clear();
        requestsMap.clear();
        registeredEmails.clear();
        uniqueSubjects.clear();
        uniqueAuthors.clear();
        ratingsList.clear();
        activityLog.clear();
        recentSearches.clear();
    }
}
