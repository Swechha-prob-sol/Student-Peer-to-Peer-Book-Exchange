package com.bookloop.data;

import com.bookloop.model.Book;
import com.bookloop.model.ExchangeRequest;
import com.bookloop.model.Rating;
import com.bookloop.model.Student;
import com.bookloop.persistence.FileStorageManager;
import java.io.IOException;
import java.util.*;

/**
 * In-memory Data Storage layer with file persistence integration.
 * Demonstrates:
 * - HashMap: O(1) primary key lookup for students, books, requests
 * - LinkedHashMap: preserves book insertion order
 * - TreeMap: naturally sorted indexing of books by price
 * - ArrayList: dynamic arrays for lists of books, reviews, and search results
 * - LinkedList: double-linked list for sequential recent activity logs
 * - HashSet: ensures strict uniqueness of registered email addresses
 * - LinkedHashSet: maintains unique subject categories in insertion order
 * - TreeSet: maintains unique author names sorted alphabetically
 * - PriorityQueue: prioritizes pending requests by date / urgency
 * - Deque (ArrayDeque): double-ended queue for recent search keyword history
 * - Iterator & ListIterator: safe collection traversal and modification
 * - Collections utility methods: sort, unmodifiableList, reverse, etc.
 * - File-based persistence: load/save to CSV files
 */
public class DataStore {

    // Singleton instance
    private static DataStore instance;

    // 1. HashMap: Fast primary key lookups
    private final Map<String, Student> studentsMap = new HashMap<>();
    private final Map<String, Book> booksMap = new HashMap<>();
    private final Map<String, ExchangeRequest> requestsMap = new HashMap<>();

    // 2. LinkedHashMap: Preserves insertion order of listings
    private final Map<String, Book> recentListingsCache = new LinkedHashMap<>();

    // 3. TreeMap: Tree-based sorted index of books by price
    private final TreeMap<Double, List<Book>> booksByPriceIndex = new TreeMap<>();

    // 4. HashSet: Constant-time uniqueness check for student emails
    private final Set<String> registeredEmails = new HashSet<>();

    // 5. LinkedHashSet: Preserves unique subject categories in discovery order
    private final Set<String> uniqueSubjects = new LinkedHashSet<>();

    // 6. TreeSet: Maintains unique authors in alphabetical order
    private final SortedSet<String> uniqueAuthors = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

    // 7. ArrayList: List of all ratings / reviews
    private final List<Rating> ratingsList = new ArrayList<>();

    // 8. LinkedList: Activity history / audit log
    private final List<String> activityLog = new LinkedList<>();

    // 9. PriorityQueue: Pending requests prioritized by date
    private final PriorityQueue<ExchangeRequest> pendingRequestsQueue = new PriorityQueue<>();

    // 10. Deque: Recent search history (LIFO stack & FIFO queue features)
    private final Deque<String> recentSearches = new ArrayDeque<>();

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
     * Demonstrates exception propagation with throws clause.
     */
    public boolean loadFromFiles() throws IOException {
        if (!FileStorageManager.savedDataExists()) {
            return false;
        }

        List<Student> students = FileStorageManager.loadStudents();
        List<Book> books = FileStorageManager.loadBooks();
        List<ExchangeRequest> requests = FileStorageManager.loadRequests();
        List<Rating> ratings = FileStorageManager.loadRatings();

        if (students.isEmpty() && books.isEmpty()) {
            return false;
        }

        // Clear existing data before loading
        clearAll();

        // Load students
        for (Student s : students) {
            studentsMap.put(s.getId(), s);
            registeredEmails.add(s.getEmail().toLowerCase());
        }

        // Load books (and rebuild indexes)
        for (Book b : books) {
            booksMap.put(b.getId(), b);
            recentListingsCache.put(b.getId(), b);
            booksByPriceIndex.computeIfAbsent(b.getPrice(), k -> new ArrayList<>()).add(b);
            uniqueSubjects.add(b.getSubject());
            uniqueAuthors.add(b.getAuthor());
        }

        // Load requests
        for (ExchangeRequest r : requests) {
            requestsMap.put(r.getId(), r);
            pendingRequestsQueue.offer(r);
        }

        // Load ratings (without re-triggering seller rating recalculation)
        ratingsList.addAll(ratings);

        logActivity("Data loaded from saved files (" + students.size() + " students, " +
                books.size() + " books, " + requests.size() + " requests, " +
                ratings.size() + " ratings)");

        return true;
    }

    /**
     * Saves all data to CSV files.
     * Demonstrates exception handling with try-catch-finally pattern.
     */
    public void saveToFiles() {
        boolean success = false;
        try {
            FileStorageManager.saveStudents(getAllStudents());
            FileStorageManager.saveBooks(getAllBooks());
            FileStorageManager.saveRequests(getAllRequests());
            FileStorageManager.saveRatings(new ArrayList<>(ratingsList));
            success = true;
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save data: " + e.getMessage());
        } finally {
            // finally block always executes
            if (success) {
                logActivity("All data saved to files successfully.");
            } else {
                logActivity("Data save attempt failed.");
            }
        }
    }

    // ==========================================
    // Student Operations
    // ==========================================

    public boolean addStudent(Student student) {
        if (student == null || student.getId() == null) return false;
        String emailLower = student.getEmail().toLowerCase();
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

        // Add to primary HashMap
        booksMap.put(book.getId(), book);

        // Add to LinkedHashMap (insertion order)
        recentListingsCache.put(book.getId(), book);

        // Add to TreeMap price index
        booksByPriceIndex.computeIfAbsent(book.getPrice(), k -> new ArrayList<>()).add(book);

        // Add to Sets
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
        // Using standard Iterator to showcase Iterator usage
        Iterator<Book> it = booksMap.values().iterator();
        while (it.hasNext()) {
            Book b = it.next();
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
        recentListingsCache.put(updatedBook.getId(), updatedBook);
        logActivity("Book updated: [" + updatedBook.getId() + "] " + updatedBook.getTitle());
        return true;
    }

    public boolean deleteBook(String id) {
        if (id == null || !booksMap.containsKey(id)) {
            return false;
        }
        Book removed = booksMap.remove(id);
        recentListingsCache.remove(id);

        // Clean up from TreeMap price index
        if (removed != null && booksByPriceIndex.containsKey(removed.getPrice())) {
            List<Book> list = booksByPriceIndex.get(removed.getPrice());
            // Using Iterator to safely remove
            Iterator<Book> it = list.iterator();
            while (it.hasNext()) {
                if (it.next().getId().equals(id)) {
                    it.remove();
                    break;
                }
            }
            if (list.isEmpty()) {
                booksByPriceIndex.remove(removed.getPrice());
            }
        }

        logActivity("Book removed from catalog: [" + id + "]");
        return true;
    }

    public Set<String> getUniqueSubjects() {
        return Collections.unmodifiableSet(uniqueSubjects);
    }

    public SortedSet<String> getUniqueAuthors() {
        return Collections.unmodifiableSortedSet(uniqueAuthors);
    }

    public TreeMap<Double, List<Book>> getBooksByPriceIndex() {
        return booksByPriceIndex;
    }

    // ==========================================
    // Request Operations
    // ==========================================

    public boolean addRequest(ExchangeRequest request) {
        if (request == null || request.getId() == null) return false;
        requestsMap.put(request.getId(), request);
        pendingRequestsQueue.offer(request);
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
        Collections.sort(res); // Sorted by Comparable (date desc)
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

    public PriorityQueue<ExchangeRequest> getPendingRequestsQueue() {
        return pendingRequestsQueue;
    }

    // ==========================================
    // Rating Operations
    // ==========================================

    public void addRating(Rating rating) {
        if (rating == null) return;
        ratingsList.add(rating);
        // Also update seller's running rating if student exists
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
    // Search & History Operations
    // ==========================================

    public void recordSearchKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Keep at most 10 recent searches in Deque
            if (recentSearches.size() >= 10) {
                recentSearches.removeLast();
            }
            recentSearches.push(keyword.trim());
        }
    }

    public Deque<String> getRecentSearches() {
        return recentSearches;
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
        recentListingsCache.clear();
        booksByPriceIndex.clear();
        registeredEmails.clear();
        uniqueSubjects.clear();
        uniqueAuthors.clear();
        ratingsList.clear();
        activityLog.clear();
        pendingRequestsQueue.clear();
        recentSearches.clear();
    }
}
