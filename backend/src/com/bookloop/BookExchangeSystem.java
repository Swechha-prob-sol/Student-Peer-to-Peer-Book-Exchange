package com.bookloop;

import com.bookloop.data.DataStore;
import com.bookloop.data.DummyData;
import com.bookloop.exception.*;
import com.bookloop.model.*;
import com.bookloop.persistence.CsvExportService;
import com.bookloop.persistence.FileStorageManager;
import com.bookloop.service.*;
import com.bookloop.threading.*;
import com.bookloop.util.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main Entry Point: BookLoop - Student Peer-to-Peer Book Exchange Platform.
 * Menu-driven Java console application demonstrating:
 * - Clean separation of UI (menus), Business Logic (Services), and Data (DataStore/DummyData)
 * - Dynamic student context switching (defaults to Aarav Sharma, DTU)
 * - Comprehensive implementation of all Milestone 1, 2, and 3 requirements
 * - Robust input validation and user-friendly error recovery
 *
 * Milestone 3 Additions:
 * - Book Recommendation Engine (BookRecommendationService: semester, subjects, price, non-self)
 * - Project Analytics & Executive Reports (ProjectReportService: HashMap, TreeMap, native arrays, String/StringBuilder)
 * - Data Validation Utility (ValidationUtil: student, book, request, rating, custom ValidationException)
 * - CSV Export Service (CsvExportService: separate exports/ folder, no overwrite of persistence files)
 */
public class BookExchangeSystem {

    private final DataStore dataStore;
    private final StudentService studentService;
    private final BookService bookService;
    private final ExchangeService exchangeService;
    private final RatingService ratingService;
    private final StreamService streamService;
    private final BookRecommendationService recommendationService;
    private final ProjectReportService projectReportService;
    private final CsvExportService csvExportService;
    private final Scanner scanner;
    private AutoSaveManager autoSaveManager;

    // Currently logged-in active student context
    private Student currentStudent;

    public BookExchangeSystem() {
        this.dataStore = DataStore.getInstance();
        this.studentService = new StudentService(dataStore);
        this.bookService = new BookService(dataStore);
        this.exchangeService = new ExchangeService(dataStore);
        this.ratingService = new RatingService(dataStore);
        this.streamService = new StreamService(dataStore);
        this.recommendationService = new BookRecommendationService(dataStore);
        this.projectReportService = new ProjectReportService(dataStore);
        this.csvExportService = new CsvExportService();
        this.scanner = new Scanner(System.in);

        // Initialize data: loads from files if available, otherwise dummy data
        DummyData.initialize(dataStore);

        // Default active user (Aarav Sharma - DTU) matching the frontend default
        this.currentStudent = studentService.getStudentById("STU101");

        // Start auto-save daemon thread (saves every 60 seconds)
        this.autoSaveManager = new AutoSaveManager(dataStore, 60000);
        autoSaveManager.start();
    }

    public static void main(String[] args) {
        BookExchangeSystem app = new BookExchangeSystem();
        app.run();
    }

    public void run() {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readIntChoice("Enter your choice (1-26): ", 1, 26);

            try {
                switch (choice) {
                    case 1:
                        handleRegisterStudent();
                        break;
                    case 2:
                        handleSwitchStudent();
                        break;
                    case 3:
                        handleViewAllBooks();
                        break;
                    case 4:
                        handleSearchBooks();
                        break;
                    case 5:
                        handleSortBooks();
                        break;
                    case 6:
                        handleViewBookAndSeller();
                        break;
                    case 7:
                        handleAddBookListing();
                        break;
                    case 8:
                        handleUpdateDeleteMyBook();
                        break;
                    case 9:
                        handleSendExchangeRequest();
                        break;
                    case 10:
                        handleManageReceivedRequests();
                        break;
                    case 11:
                        handleTrackSentRequests();
                        break;
                    case 12:
                        handleRateSeller();
                        break;
                    case 13:
                        handleSystemInfoAndReset();
                        break;
                    case 14:
                        handleStreamOperations();
                        break;
                    case 15:
                        handleStringDemos();
                        break;
                    case 16:
                        handleMultithreadingDemo();
                        break;
                    case 17:
                        handleGenericsDemos();
                        break;
                    case 18:
                        handleFileOperations();
                        break;
                    case 19:
                        handleExceptionDemo();
                        break;
                    case 20:
                        handleSaveData();
                        break;
                    case 21:
                        handleBookRecommendations();
                        break;
                    case 22:
                        handleProjectAnalytics();
                        break;
                    case 23:
                        handleDataValidationDemos();
                        break;
                    case 24:
                        handleCsvExports();
                        break;
                    case 25:
                        handleSaveData();
                        break;
                    case 26:
                        handleSaveData();
                        if (autoSaveManager != null) autoSaveManager.shutdown();
                        running = false;
                        System.out.println("\nThank you for using BookLoop! Happy studying and exchanging.");
                        break;
                    default:
                        System.out.println("Invalid selection. Please choose an option from the menu.");
                }
            } catch (ValidationException e) {
                // Demonstrates catching custom validation exception
                System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                if (e.getFieldName() != null) {
                    System.out.println("  Field: '" + e.getFieldName() + "' | Value: \"" + e.getInvalidValue() + "\"");
                }
            } catch (BookNotFoundException e) {
                // Demonstrates catching custom checked exception
                System.out.println("\n[ERROR] " + e.getMessage() + " (Book ID: " + e.getBookId() + ")");
            } catch (InvalidStudentException e) {
                // Demonstrates catching custom unchecked exception
                System.out.println("\n[ERROR] " + e.getMessage());
            } catch (InvalidExchangeRequestException e) {
                // Demonstrates catching custom checked exception
                System.out.println("\n[ERROR] " + e.getMessage());
            } catch (SecurityException | IllegalStateException e) {
                // Demonstrates multi-catch (catching multiple exception types)
                System.out.println("\n[ERROR] " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("\n[ERROR] Invalid input: " + e.getMessage());
            } catch (Exception e) {
                // Generic catch-all
                System.out.println("\n[ERROR] Unexpected error: " + e.getMessage());
            }

            if (running) {
                System.out.print("\nPress Enter to return to main menu...");
                scanner.nextLine();
            }
        }
    }

    // ==========================================
    // UI Menu Displays
    // ==========================================

    private void printBanner() {
        System.out.println("================================================================================");
        System.out.println("   ____   ____   ____  _  ___     ____   ____  _____ ");
        System.out.println("  | __ ) / __ \\ / __ \\| |/ / |   / __ \\ / __ \\|  __ \\");
        System.out.println("  |  _ \\| |  | | |  | | ' /| |  | |  | | |  | | |__) |");
        System.out.println("  | |_) | |__| | |__| | . \\| |__| |__| | |__| |  ___/ ");
        System.out.println("  |____/ \\____/ \\____/|_|\\_\\_____\\____/ \\____/|_|     ");
        System.out.println("       STUDENT PEER-TO-PEER ACADEMIC BOOK EXCHANGE (CORE JAVA)");
        System.out.println("              Read More. Spend Less. Exchange Smarter.");
        System.out.println("  Milestone 3: Recommendations + Analytics + Validation + CSV Export");
        System.out.println("================================================================================");
    }

    private void printMainMenu() {
        System.out.println("\n--------------------------------------------------------------------------------");
        System.out.printf("ACTIVE STUDENT: %s (%s) | %s Sem %d%n",
                currentStudent != null ? currentStudent.getName() : "None",
                currentStudent != null ? currentStudent.getId() : "N/A",
                currentStudent != null ? currentStudent.getCollege() : "",
                currentStudent != null ? currentStudent.getSemester() : 0);
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("  === MILESTONE 1 FEATURES ===");
        System.out.println(" 1.  Register a New Student");
        System.out.println(" 2.  Switch Active Student Context");
        System.out.println(" 3.  View All Available Books (Catalog)");
        System.out.println(" 4.  Search Books (by Title, Subject, Semester, or Author)");
        System.out.println(" 5.  Sort Books (by Title, Author, Price Low-High, Price High-Low)");
        System.out.println(" 6.  View Detailed Book & Seller Profile");
        System.out.println(" 7.  List / Add a Book for Sale or Exchange");
        System.out.println(" 8.  Manage Personal Book Listings (Update / Delete)");
        System.out.println(" 9.  Send Exchange or Purchase Request");
        System.out.println(" 10. Manage Received Requests (Accept / Reject / Advance Stage)");
        System.out.println(" 11. Track Sent Requests & Transaction History");
        System.out.println(" 12. Rate a Seller (Peer Reviews & Star Ratings)");
        System.out.println(" 13. System Activity Log & Reset Data to Baseline");
        System.out.println("  === MILESTONE 2 FEATURES ===");
        System.out.println(" 14. Stream API & Lambda Operations (filter, map, sort, group)");
        System.out.println(" 15. Strings, Arrays & Wrapper Class Demonstrations");
        System.out.println(" 16. Multithreading Demo (Producer-Consumer Processing)");
        System.out.println(" 17. Generics Demonstrations (Type-Safe Utilities)");
        System.out.println(" 18. File Operations (Import/Export, StreamTokenizer)");
        System.out.println(" 19. Exception Handling Demonstration");
        System.out.println(" 20. Save All Data to Files");
        System.out.println("  === MILESTONE 3 FEATURES ===");
        System.out.println(" 21. Book Recommendation Engine (Smart, Semester, Subject, Budget)");
        System.out.println(" 22. Platform Analytics & Executive Reports (KPIs, Subjects, Leaderboard)");
        System.out.println(" 23. Data Validation Utility & Rule Verification (ValidationUtil)");
        System.out.println(" 24. Export System Reports to CSV (Decoupled exports/ Directory)");
        System.out.println(" 25. Save All Data to Files");
        System.out.println(" 26. Save & Exit");
        System.out.println("--------------------------------------------------------------------------------");
    }

    // ==========================================
    // Feature 1: Register Student
    // ==========================================

    private void handleRegisterStudent() throws InvalidStudentException {
        System.out.println("\n=== FEATURE 1: REGISTER A NEW STUDENT ===");
        System.out.print("Enter Student Full Name: ");
        String name = scanner.nextLine().trim();

        // Validate using StringUtils
        if (name.isEmpty()) {
            throw new InvalidStudentException("", "Student name cannot be empty.");
        }

        System.out.print("Enter College Email (e.g. name@college.ac.in): ");
        String email = scanner.nextLine().trim();
        if (!StringUtils.isValidEmail(email)) {
            throw new InvalidStudentException("", "Invalid email format: " + email);
        }

        System.out.print("Enter Contact Phone (+91 XXXXX XXXXX): ");
        String phone = scanner.nextLine().trim();

        System.out.print("Enter College / University Name: ");
        String college = scanner.nextLine().trim();

        System.out.print("Enter Academic Department (e.g. CSE, ECE, Mechanical): ");
        String department = scanner.nextLine().trim();

        int semester = readIntChoice("Enter Current Semester (1-8): ", 1, 8);

        Student created = studentService.registerStudent(name, email, phone, college, department, semester);
        if (created != null) {
            System.out.println("\n[SUCCESS] Student registered successfully!");
            created.displayProfile();

            System.out.print("Would you like to switch to this student profile now? (y/n): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                this.currentStudent = created;
                System.out.println("[INFO] Active student context switched to: " + created.getName());
            }
        }
    }

    // ==========================================
    // Feature: Switch Active Student Context
    // ==========================================

    private void handleSwitchStudent() {
        System.out.println("\n=== SWITCH ACTIVE STUDENT CONTEXT ===");
        List<Student> students = studentService.getAllStudents();
        System.out.println("Registered Students in System:");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            String marker = (currentStudent != null && currentStudent.getId().equals(s.getId())) ? " [CURRENT]" : "";
            System.out.printf("  %d. [%s] %-18s | %-20s | Sem %d%s%n",
                    (i + 1), s.getId(), s.getName(), s.getCollege(), s.getSemester(), marker);
        }

        int choice = readIntChoice("Select student number to activate: ", 1, students.size());
        this.currentStudent = students.get(choice - 1);
        System.out.println("\n[SUCCESS] You are now acting as: " + currentStudent.getName() + " (" + currentStudent.getId() + ")");
    }

    // ==========================================
    // Feature 3: View All Available Books
    // ==========================================

    private void handleViewAllBooks() {
        System.out.println("\n=== FEATURE 3: ALL AVAILABLE TEXTBOOKS IN CATALOG ===");
        List<Book> books = bookService.getAvailableBooks();
        if (books.isEmpty()) {
            System.out.println("No books currently available.");
            return;
        }

        System.out.printf("%-7s | %-32s | %-20s | %-6s | %-7s | %-12s | %-16s%n",
                "ID", "Title", "Author", "Sem", "Price", "Type", "Seller");
        System.out.println("---------------------------------------------------------------------------------------------------------");
        for (Book b : books) {
            System.out.printf("%-7s | %-32s | %-20s | Sem %-2d | Rs.%-5.0f | %-12s | %-16s%n",
                    b.getId(),
                    truncate(b.getTitle(), 32),
                    truncate(b.getAuthor(), 20),
                    b.getSemester(),
                    b.getPrice(),
                    b.getListingType().getDisplayName(),
                    truncate(b.getSellerName(), 16));
        }
        System.out.printf("%nTotal active listings: %d books%n", books.size());
    }

    // ==========================================
    // Feature 4: Search Books (Method Overloading)
    // ==========================================

    private void handleSearchBooks() {
        System.out.println("\n=== FEATURE 4: SEARCH TEXTBOOKS (DEMONSTRATES METHOD OVERLOADING) ===");
        System.out.println(" 1. Search by Keyword (Title, Author, Subject, or Description)");
        System.out.println(" 2. Search by Academic Subject & Semester");
        System.out.println(" 3. Filter by Semester (1-8)");
        System.out.println(" 4. Filter by Author Name");
        System.out.println(" 5. Filter by Academic Subject");

        int mode = readIntChoice("Choose search method (1-5): ", 1, 5);
        List<Book> results;

        switch (mode) {
            case 1:
                System.out.print("Enter search keyword (e.g., 'Algorithms', 'Tanenbaum', 'Database'): ");
                String kw = scanner.nextLine().trim();
                results = bookService.searchBooks(kw);
                break;
            case 2:
                System.out.print("Enter Subject name: ");
                String subj = scanner.nextLine().trim();
                int sem = readIntChoice("Enter Semester (1-8): ", 1, 8);
                results = bookService.searchBooks(subj, sem);
                break;
            case 3:
                int s = readIntChoice("Enter Semester to filter (1-8): ", 1, 8);
                results = bookService.searchBooksBySemester(s);
                break;
            case 4:
                System.out.print("Enter Author name: ");
                String auth = scanner.nextLine().trim();
                results = bookService.searchBooksByAuthor(auth);
                break;
            case 5:
                System.out.print("Enter Subject: ");
                String sub = scanner.nextLine().trim();
                results = bookService.searchBooksBySubject(sub);
                break;
            default:
                results = bookService.getAllBooks();
        }

        displayBookListBrief("Search Results", results);
    }

    // ==========================================
    // Feature 5 & 12: Sort Books
    // ==========================================

    private void handleSortBooks() {
        System.out.println("\n=== FEATURE 12: SORT BOOKS (COMPARABLE & COMPARATOR) ===");
        System.out.println(" 1. Natural Sort via Comparable<Book> (Title A to Z)");
        System.out.println(" 2. Custom Sort: Author Name Alphabetical (A to Z)");
        System.out.println(" 3. Custom Sort: Price (Low to High)");
        System.out.println(" 4. Custom Sort: Price (High to Low)");

        int choice = readIntChoice("Select sorting algorithm (1-4): ", 1, 4);
        List<Book> active = bookService.getAvailableBooks();
        List<Book> sorted;

        switch (choice) {
            case 1:
                sorted = bookService.sortBooksNaturally(active);
                displayBookListBrief("Sorted Naturally by Title (A to Z) [Comparable<Book>]", sorted);
                break;
            case 2:
                sorted = bookService.sortByAuthor(active);
                displayBookListBrief("Sorted by Author (A to Z) [BookAuthorComparator]", sorted);
                break;
            case 3:
                sorted = bookService.sortByPriceLowToHigh(active);
                displayBookListBrief("Sorted by Price (Low to High) [BookPriceAscendingComparator]", sorted);
                break;
            case 4:
                sorted = bookService.sortByPriceHighToLow(active);
                displayBookListBrief("Sorted by Price (High to Low) [BookPriceDescendingComparator]", sorted);
                break;
            default:
                break;
        }
    }

    // ==========================================
    // Feature 5: View Book & Seller Details
    // ==========================================

    private void handleViewBookAndSeller() throws BookNotFoundException {
        System.out.println("\n=== FEATURE 5: VIEW BOOK & SELLER DETAILS ===");
        System.out.print("Enter Book ID (e.g. BK201, BK205): ");
        String id = scanner.nextLine().trim();

        Book book = bookService.getBookById(id);
        if (book == null) {
            // Demonstrates throwing custom checked exception
            throw new BookNotFoundException(id);
        }

        // Display book details via Polymorphism (Displayable interface)
        book.displayDetails();

        // Display seller details
        Student seller = studentService.getStudentById(book.getSellerId());
        if (seller != null) {
            System.out.println("\n--- SELLER INFORMATION ---");
            seller.displayProfile();

            // Display seller reviews
            List<Rating> reviews = ratingService.getReviewsForSeller(seller.getId());
            if (!reviews.isEmpty()) {
                System.out.println("Recent Peer Reviews for " + seller.getName() + ":");
                for (Rating r : reviews) {
                    r.displayDetails();
                }
            } else {
                System.out.println("No reviews written for this seller yet.");
            }
        }
    }

    // ==========================================
    // Feature 2: Add Book for Sale or Exchange
    // ==========================================

    private void handleAddBookListing() {
        System.out.println("\n=== FEATURE 2: LIST A BOOK FOR SALE OR EXCHANGE ===");
        if (currentStudent == null) {
            throw new InvalidStudentException("", "Please register or switch to an active student first.");
        }

        System.out.println("Listing book as seller: " + currentStudent.getName() + " (" + currentStudent.getCollege() + ")");

        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Author(s): ");
        String author = scanner.nextLine().trim();

        System.out.print("Enter ISBN (or press Enter for N/A): ");
        String isbn = scanner.nextLine().trim();
        isbn = StringUtils.formatISBN(isbn.isEmpty() ? "N/A" : isbn);

        System.out.print("Enter Academic Subject (e.g. Data Structures, OS, Mathematics): ");
        String subject = scanner.nextLine().trim();

        System.out.print("Enter Department Category (e.g. Computer Science, Electronics): ");
        String category = scanner.nextLine().trim();

        int semester = readIntChoice("Enter Target Semester (1-8): ", 1, 8);

        System.out.print("Enter Book Edition: ");
        String edition = scanner.nextLine().trim();

        System.out.println("Select Condition:");
        System.out.println(" 1. Like New   2. Very Good   3. Good   4. Fair");
        int condChoice = readIntChoice("Condition (1-4): ", 1, 4);
        Condition condition = Condition.values()[condChoice - 1];

        System.out.println("Select Listing Modality:");
        System.out.println(" 1. For Sale   2. For Exchange   3. Both (Sale / Exchange)");
        int typeChoice = readIntChoice("Listing Type (1-3): ", 1, 3);
        ListingType listingType = (typeChoice == 1) ? ListingType.FOR_SALE :
                (typeChoice == 2) ? ListingType.FOR_EXCHANGE : ListingType.BOTH;

        double price = 0.0;
        if (listingType != ListingType.FOR_EXCHANGE) {
            price = readDouble("Enter Price in Rs.: ", 0.0, 50000.0);
        }

        String preferredExchange = "";
        if (listingType != ListingType.FOR_SALE) {
            System.out.print("Enter Preferred Books for Exchange (e.g., 'CLRS algorithms or Sipser TOC'): ");
            preferredExchange = scanner.nextLine().trim();
        }

        System.out.print("Enter Description & Notes (condition, highlights, notes): ");
        String desc = scanner.nextLine().trim();

        Book added = bookService.addBook(
                title, author, isbn, subject, category, semester, edition,
                condition, price, listingType, preferredExchange, desc, currentStudent
        );

        System.out.println("\n[SUCCESS] Book listed successfully on BookLoop!");
        added.displayDetails();
    }

    // ==========================================
    // Feature 6: Update & Delete Book Listings
    // ==========================================

    private void handleUpdateDeleteMyBook() throws BookNotFoundException {
        System.out.println("\n=== FEATURE 6: MANAGE PERSONAL BOOK LISTINGS ===");
        if (currentStudent == null) return;

        List<Book> myBooks = bookService.getBooksBySeller(currentStudent.getId());
        if (myBooks.isEmpty()) {
            System.out.println("You have no active or historical listings under ID: " + currentStudent.getId());
            return;
        }

        System.out.println("Your Current Listings:");
        for (int i = 0; i < myBooks.size(); i++) {
            Book b = myBooks.get(i);
            System.out.printf("  %d. [%s] \"%s\" | Rs.%.0f | %s | Status: %s%n",
                    (i + 1), b.getId(), b.getTitle(), b.getPrice(),
                    b.getListingType().getDisplayName(), b.getStatus());
        }

        int bookIndex = readIntChoice("Select book to manage (1-" + myBooks.size() + "): ", 1, myBooks.size());
        Book selected = myBooks.get(bookIndex - 1);

        System.out.println("\nSelected Book: " + selected.getTitle());
        System.out.println(" 1. Update Price");
        System.out.println(" 2. Update Status (Active / Sold / Exchanged)");
        System.out.println(" 3. Delete Listing from Catalog");
        System.out.println(" 4. Back");

        int action = readIntChoice("Choose action (1-4): ", 1, 4);
        switch (action) {
            case 1:
                double newPrice = readDouble("Enter new Price in Rs.: ", 0.0, 50000.0);
                bookService.updateBookDetails(selected.getId(), null, null, newPrice, null, null, null);
                System.out.println("[SUCCESS] Price updated to Rs." + newPrice);
                break;
            case 2:
                System.out.println("1. Active  2. Sold  3. Exchanged");
                int st = readIntChoice("Select new status (1-3): ", 1, 3);
                String newStatus = (st == 1) ? "Active" : (st == 2) ? "Sold" : "Exchanged";
                bookService.updateBookDetails(selected.getId(), null, null, -1, null, null, newStatus);
                System.out.println("[SUCCESS] Status updated to: " + newStatus);
                break;
            case 3:
                System.out.print("Are you sure you want to permanently delete this listing? (y/n): ");
                String conf = scanner.nextLine().trim();
                if (conf.equalsIgnoreCase("y") || conf.equalsIgnoreCase("yes")) {
                    bookService.deleteBook(selected.getId(), currentStudent.getId());
                    System.out.println("[SUCCESS] Book listing [" + selected.getId() + "] deleted successfully.");
                } else {
                    System.out.println("[CANCELLED] Deletion cancelled.");
                }
                break;
            default:
                break;
        }
    }

    // ==========================================
    // Feature 7: Send Exchange / Purchase Request
    // ==========================================

    private void handleSendExchangeRequest() throws BookNotFoundException, InvalidExchangeRequestException {
        System.out.println("\n=== FEATURE 7: SEND EXCHANGE OR PURCHASE REQUEST ===");
        if (currentStudent == null) {
            throw new InvalidStudentException("", "No active student. Register or switch first.");
        }

        System.out.print("Enter the Book ID you want to acquire (e.g. BK201, BK202): ");
        String bookId = scanner.nextLine().trim();

        Book targetBook = bookService.getBookById(bookId);
        if (targetBook == null) {
            throw new BookNotFoundException(bookId);
        }

        if (targetBook.getSellerId().equalsIgnoreCase(currentStudent.getId())) {
            throw new InvalidExchangeRequestException("You cannot request your own listed book.");
        }

        System.out.println("\nTarget Book Details:");
        System.out.printf("  Title:  \"%s\" by %s%n", targetBook.getTitle(), targetBook.getAuthor());
        System.out.printf("  Price:  Rs.%.0f | Mode: %s%n", targetBook.getPrice(), targetBook.getListingType().getDisplayName());
        System.out.printf("  Seller: %s (%s)%n", targetBook.getSellerName(), targetBook.getSellerCollege());

        System.out.println("\nRequest Type:");
        System.out.println(" 1. Purchase Request");
        System.out.println(" 2. Book Exchange Request");
        int typeChoice = readIntChoice("Choose type (1-2): ", 1, 2);
        String reqType = (typeChoice == 1) ? "Purchase" : "Exchange";

        String offeredBookId = null;
        if ("Exchange".equals(reqType)) {
            List<Book> myBooks = bookService.getBooksBySeller(currentStudent.getId());
            if (myBooks.isEmpty()) {
                System.out.println("\n[NOTE] You have not listed any books yet to offer for exchange.");
                System.out.print("Enter title of book you can offer in person (or press Enter): ");
                scanner.nextLine();
                offeredBookId = null;
            } else {
                System.out.println("\nSelect one of your listed books to offer in exchange:");
                for (int i = 0; i < myBooks.size(); i++) {
                    System.out.printf("  %d. [%s] \"%s\"%n", (i + 1), myBooks.get(i).getId(), myBooks.get(i).getTitle());
                }
                int offIdx = readIntChoice("Select book to offer (1-" + myBooks.size() + "): ", 1, myBooks.size());
                offeredBookId = myBooks.get(offIdx - 1).getId();
            }
        }

        System.out.print("Enter message or meeting preference (e.g., 'Can meet at DTU library'): ");
        String notes = scanner.nextLine().trim();

        ExchangeRequest req = exchangeService.createRequest(bookId, currentStudent, reqType, offeredBookId, notes);
        System.out.println("\n[SUCCESS] Request successfully sent to seller!");
        req.displayDetails();
    }

    // ==========================================
    // Feature 8: Manage Received Requests
    // ==========================================

    private void handleManageReceivedRequests() {
        System.out.println("\n=== FEATURE 8: MANAGE RECEIVED REQUESTS (SELLER VIEW) ===");
        if (currentStudent == null) return;

        List<ExchangeRequest> received = exchangeService.getReceivedRequests(currentStudent.getId());
        if (received.isEmpty()) {
            System.out.println("No incoming purchase or exchange requests received for " + currentStudent.getName());
            return;
        }

        System.out.println("Incoming Requests for your books:");
        for (int i = 0; i < received.size(); i++) {
            ExchangeRequest r = received.get(i);
            System.out.printf("  %d. [%s] %s from %s for \"%s\" | Status: %s (%s)%n",
                    (i + 1), r.getId(), r.getRequestType(), r.getRequesterName(),
                    r.getBookTitle(), r.getStatus().getDisplayName(), r.getTrackingStepDescription());
        }

        int reqIdx = readIntChoice("Select request to process (1-" + received.size() + "): ", 1, received.size());
        ExchangeRequest selected = received.get(reqIdx - 1);

        selected.displayDetails();

        System.out.println("\nAvailable Actions:");
        System.out.println(" 1. Accept Request");
        System.out.println(" 2. Reject Request");
        System.out.println(" 3. Advance Tracking Stage (e.g., Contact Established -> Completed)");
        System.out.println(" 4. Back");

        int act = readIntChoice("Action (1-4): ", 1, 4);
        switch (act) {
            case 1:
                exchangeService.acceptRequest(selected.getId(), currentStudent.getId());
                System.out.println("[SUCCESS] Request accepted! Book marked as Pending. Contact established with requester.");
                break;
            case 2:
                exchangeService.rejectRequest(selected.getId(), currentStudent.getId());
                System.out.println("[SUCCESS] Request marked as Rejected.");
                break;
            case 3:
                boolean adv = exchangeService.advanceStage(selected.getId());
                if (adv) {
                    System.out.println("[SUCCESS] Transaction progress advanced to: " + selected.getTrackingStepDescription());
                } else {
                    System.out.println("[INFO] Request has already reached final stage or is cancelled/rejected.");
                }
                break;
            default:
                break;
        }
    }

    // ==========================================
    // Feature 9 & 10: Track Sent Requests & History
    // ==========================================

    private void handleTrackSentRequests() {
        System.out.println("\n=== FEATURES 9 & 10: TRACK SENT REQUESTS & TRANSACTION HISTORY ===");
        if (currentStudent == null) return;

        List<ExchangeRequest> sent = exchangeService.getSentRequests(currentStudent.getId());
        if (sent.isEmpty()) {
            System.out.println("You have not sent any requests yet.");
            return;
        }

        System.out.println("Requests Sent by You:");
        for (int i = 0; i < sent.size(); i++) {
            ExchangeRequest r = sent.get(i);
            System.out.printf("  %d. [%s] %s for \"%s\" to %s | Status: %s | Progress: %s%n",
                    (i + 1), r.getId(), r.getRequestType(), r.getBookTitle(),
                    r.getSellerName(), r.getStatus().getDisplayName(), r.getTrackingStepDescription());
        }

        System.out.println("\nSelect a request to view timeline or cancel (or 0 to back):");
        int sel = readIntChoice("Choice (0-" + sent.size() + "): ", 0, sent.size());
        if (sel == 0) return;

        ExchangeRequest req = sent.get(sel - 1);
        req.displayDetails();
        printTimeline(req.getTrackingStep());

        if (req.getStatus() == RequestStatus.PENDING) {
            System.out.print("Do you wish to cancel this request? (y/n): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
                exchangeService.cancelRequest(req.getId(), currentStudent.getId());
                System.out.println("[SUCCESS] Request has been cancelled.");
            }
        }
    }

    private void printTimeline(int currentStep) {
        System.out.println("\n--- VISUAL EXCHANGE PROGRESS TIMELINE ---");
        String[] steps = {
                "1. Request Sent",
                "2. Request Accepted",
                "3. Contact Established",
                "4. Exchange Completed",
                "5. Review Submitted"
        };
        for (int i = 0; i < steps.length; i++) {
            int stepNum = i + 1;
            String symbol = (stepNum < currentStep) ? "[DONE]" :
                    (stepNum == currentStep) ? "[ACTIVE]" : "[PENDING]";
            System.out.printf("  %-13s %s%n", symbol, steps[i]);
        }
        System.out.println("----------------------------------------");
    }

    // ==========================================
    // Feature 11: Rate Sellers
    // ==========================================

    private void handleRateSeller() {
        System.out.println("\n=== FEATURE 11: RATE A SELLER (PEER REVIEWS) ===");
        if (currentStudent == null) return;

        System.out.print("Enter the Seller ID to rate (e.g. STU102, STU103): ");
        String sellerId = scanner.nextLine().trim();

        Student seller = studentService.getStudentById(sellerId);
        if (seller == null) {
            throw new InvalidStudentException(sellerId, "Seller not found with ID: " + sellerId);
        }

        if (seller.getId().equalsIgnoreCase(currentStudent.getId())) {
            throw new InvalidStudentException(sellerId, "You cannot submit a rating for yourself.");
        }

        System.out.printf("Rating Seller: %s (%s)%n", seller.getName(), seller.getCollege());
        double stars = readDouble("Enter Rating in Stars (1.0 to 5.0): ", 1.0, 5.0);

        System.out.print("Enter Written Review / Feedback: ");
        String comment = scanner.nextLine().trim();

        Rating r = ratingService.rateSeller(sellerId, currentStudent, stars, comment);
        System.out.println("\n[SUCCESS] Review submitted successfully!");
        System.out.printf("Updated Seller Rep: %.1f stars (%d total reviews)%n",
                seller.getRating(), seller.getReviewCount());
    }

    // ==========================================
    // Activity Log & Reset Data
    // ==========================================

    private void handleSystemInfoAndReset() {
        System.out.println("\n=== SYSTEM ACTIVITY LOG & RESET DATA ===");
        System.out.println(" 1. View In-Memory Activity Audit Log (LinkedList)");
        System.out.println(" 2. View Unique Subjects Discovered (LinkedHashSet)");
        System.out.println(" 3. View Alphabetical Authors Index (TreeSet)");
        System.out.println(" 4. Reset DataStore to Baseline Dummy Dataset");
        System.out.println(" 5. Back");

        int choice = readIntChoice("Option (1-5): ", 1, 5);
        switch (choice) {
            case 1:
                List<String> logs = dataStore.getActivityLog();
                System.out.println("\n--- RECENT ACTIVITY AUDIT TRAIL (LinkedList) ---");
                for (String l : logs) {
                    System.out.println("  " + l);
                }
                break;
            case 2:
                System.out.println("\n--- UNIQUE SUBJECTS (LinkedHashSet) ---");
                for (String s : dataStore.getUniqueSubjects()) {
                    System.out.println("  - " + s);
                }
                break;
            case 3:
                System.out.println("\n--- UNIQUE AUTHORS (TreeSet - Alphabetical) ---");
                for (String a : dataStore.getUniqueAuthors()) {
                    System.out.println("  - " + a);
                }
                break;
            case 4:
                System.out.print("Are you sure you want to reset all data back to original dummy records? (y/n): ");
                String c = scanner.nextLine().trim();
                if (c.equalsIgnoreCase("y") || c.equalsIgnoreCase("yes")) {
                    DummyData.resetToDefault(dataStore);
                    this.currentStudent = studentService.getStudentById("STU101");
                    System.out.println("[SUCCESS] System reset back to baseline dummy dataset successfully!");
                }
                break;
            default:
                break;
        }
    }

    // ==========================================
    // MILESTONE 2: Stream API & Lambda Operations
    // ==========================================

    private void handleStreamOperations() {
        System.out.println("\n=== MILESTONE 2: STREAM API & LAMBDA EXPRESSIONS ===");
        System.out.println(" 1. Filter Books by Subject (Stream filter)");
        System.out.println(" 2. Find Books in Price Range (Stream filter + sorted)");
        System.out.println(" 3. Sort Books by Price using Streams");
        System.out.println(" 4. Display Books by Semester (Stream groupingBy)");
        System.out.println(" 5. Top 5 Most Expensive Books (Stream sorted + limit)");
        System.out.println(" 6. Paginated Book View (Stream skip + limit)");
        System.out.println(" 7. Highly Rated Sellers (Stream filter + map + sorted)");
        System.out.println(" 8. Distinct Subjects (Stream map + distinct)");
        System.out.println(" 9. Catalog Statistics (Stream reduce/sum/average)");
        System.out.println(" 10. Full Stream Pipeline Demo (all operations)");
        System.out.println(" 11. Functional Interfaces Demo");

        int choice = readIntChoice("Option (1-11): ", 1, 11);

        switch (choice) {
            case 1: {
                System.out.print("Enter subject to filter: ");
                String subject = scanner.nextLine().trim();
                List<Book> filtered = streamService.filterBooksBySubject(subject);
                displayBookListBrief("Books in Subject: " + subject, filtered);
                break;
            }
            case 2: {
                double min = readDouble("Enter minimum price (Rs.): ", 0, 50000);
                double max = readDouble("Enter maximum price (Rs.): ", min, 50000);
                List<Book> range = streamService.findBooksInPriceRange(min, max);
                displayBookListBrief("Books in Price Range Rs." + (int) min + " - Rs." + (int) max, range);
                break;
            }
            case 3: {
                List<Book> sorted = streamService.sortBooksByPrice();
                displayBookListBrief("Books Sorted by Price (Low to High) [Stream API]", sorted);
                break;
            }
            case 4: {
                Map<Integer, List<Book>> bySemester = streamService.groupBooksBySemester();
                System.out.println("\n--- BOOKS GROUPED BY SEMESTER ---");
                bySemester.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> {
                            System.out.printf("\n  Semester %d (%d books):%n", entry.getKey(), entry.getValue().size());
                            entry.getValue().forEach(b ->
                                    System.out.printf("    [%s] \"%s\" - Rs.%.0f%n", b.getId(), b.getTitle(), b.getPrice()));
                        });
                break;
            }
            case 5: {
                List<Book> top5 = streamService.getTopExpensiveBooks(5);
                displayBookListBrief("Top 5 Most Expensive Books [sorted + limit]", top5);
                break;
            }
            case 6: {
                int page = readIntChoice("Enter page number (1-5): ", 1, 5);
                List<Book> paginated = streamService.getBooksPaginated(page, 5);
                displayBookListBrief("Page " + page + " (5 per page) [skip + limit]", paginated);
                break;
            }
            case 7: {
                double minRating = readDouble("Enter minimum rating (1.0-5.0): ", 1.0, 5.0);
                List<GenericPair<String, Double>> sellers = streamService.getHighlyRatedSellers(minRating);
                System.out.println("\n--- HIGHLY RATED SELLERS (Rating >= " + minRating + ") ---");
                sellers.forEach(pair ->
                        System.out.printf("  %.1f stars - %s%n", pair.getValue(), pair.getKey()));
                break;
            }
            case 8: {
                List<String> subjects = streamService.getDistinctSubjects();
                System.out.println("\n--- DISTINCT SUBJECTS [map + distinct + sorted] ---");
                subjects.forEach(s -> System.out.println("  - " + s));
                break;
            }
            case 9: {
                System.out.println("\n--- CATALOG STATISTICS ---");
                System.out.printf("Total Catalog Value:    Rs.%.0f%n", streamService.getTotalCatalogValue());
                OptionalDouble avg = streamService.getAverageBookPrice();
                System.out.printf("Average Book Price:     Rs.%.0f%n", avg.orElse(0));
                streamService.findCheapestBook().ifPresent(b ->
                        System.out.printf("Cheapest Book:          \"%s\" (Rs.%.0f)%n", b.getTitle(), b.getPrice()));
                streamService.findMostExpensiveBook().ifPresent(b ->
                        System.out.printf("Most Expensive Book:    \"%s\" (Rs.%.0f)%n", b.getTitle(), b.getPrice()));
                System.out.println("\nBooks by Category:");
                streamService.countBooksByCategory().forEach((cat, count) ->
                        System.out.printf("  %-20s: %d books%n", cat, count));
                break;
            }
            case 10:
                streamService.demonstrateFullStreamPipeline();
                break;
            case 11:
                streamService.demonstrateFunctionalInterfaces();
                break;
        }
    }

    // ==========================================
    // MILESTONE 2: Strings, Arrays, Wrapper Classes
    // ==========================================

    private void handleStringDemos() {
        System.out.println("\n=== MILESTONE 2: STRINGS, ARRAYS & WRAPPER CLASSES ===");
        System.out.println(" 1. String Pool & Immutability Demonstration");
        System.out.println(" 2. String Comparison Methods");
        System.out.println(" 3. StringBuilder vs StringBuffer Demo (Book Report)");
        System.out.println(" 4. Wrapper Classes & Parsing Demo");
        System.out.println(" 5. Multidimensional Array: Semester-Subject Book Matrix");

        int choice = readIntChoice("Option (1-5): ", 1, 5);

        switch (choice) {
            case 1:
                StringUtils.demonstrateStringPoolAndImmutability();
                break;
            case 2: {
                System.out.print("Enter first string: ");
                String a = scanner.nextLine();
                System.out.print("Enter second string: ");
                String b = scanner.nextLine();
                StringUtils.demonstrateStringComparison(a, b);
                break;
            }
            case 3: {
                // StringBuilder demo (non-synchronized, single-threaded)
                System.out.println("\n--- StringBuilder Demo (non-synchronized, faster) ---");
                Book sampleBook = bookService.getAvailableBooks().stream().findFirst().orElse(null);
                if (sampleBook != null) {
                    String report = StringUtils.buildBookReport(
                            sampleBook.getTitle(), sampleBook.getAuthor(), sampleBook.getSubject(),
                            sampleBook.getSemester(), sampleBook.getPrice(),
                            sampleBook.getCondition().getDisplayName());
                    System.out.println(report);
                }

                // StringBuffer demo (synchronized, thread-safe)
                System.out.println("\n--- StringBuffer Demo (synchronized, thread-safe) ---");
                String logEntry = StringUtils.buildActivityLogEntry(
                        java.time.LocalDateTime.now().toString(),
                        "BOOK_LISTED", currentStudent.getId(),
                        "Demo listing for StringBuffer demonstration");
                System.out.println("Log Entry: " + logEntry);
                break;
            }
            case 4:
                StringUtils.demonstrateWrapperClasses();
                break;
            case 5: {
                System.out.println("\n--- MULTIDIMENSIONAL ARRAY: SEMESTER-SUBJECT MATRIX ---");
                List<Book> allBooks = bookService.getAllBooks();
                String[][] matrix = StringUtils.buildSemesterSubjectMatrix(allBooks);
                StringUtils.printMatrix(matrix);
                break;
            }
        }
    }

    // ==========================================
    // MILESTONE 2: Multithreading Demo
    // ==========================================

    private void handleMultithreadingDemo() {
        System.out.println("\n=== MILESTONE 2: MULTITHREADING & CONCURRENCY ===");
        System.out.println("This will demonstrate Producer-Consumer request processing.");
        System.out.println("Concepts: Thread, Runnable, start(), run(), sleep(), join(),");
        System.out.println("          interrupt(), daemon threads, priorities, synchronized,");
        System.out.println("          wait(), notify(), notifyAll(), race condition prevention.");
        System.out.print("\nProceed with multithreading demo? (y/n): ");
        String ans = scanner.nextLine().trim();
        if (ans.equalsIgnoreCase("y") || ans.equalsIgnoreCase("yes")) {
            ExchangeRequestProcessor processor = new ExchangeRequestProcessor(dataStore);
            processor.demonstrateMultithreading();
        }
    }

    // ==========================================
    // MILESTONE 2: Generics Demo
    // ==========================================

    private void handleGenericsDemos() {
        System.out.println("\n=== MILESTONE 2: GENERICS DEMONSTRATIONS ===");

        // 1. GenericCollectionUtils with Book
        System.out.println("\n--- Generic Collection: GenericCollectionUtils<Book> ---");
        GenericCollectionUtils<Book> bookCollection = new GenericCollectionUtils<>(bookService.getAvailableBooks());
        System.out.println("Total books in generic collection: " + bookCollection.size());

        // 2. Generic filter with Predicate
        List<Book> csBooks = bookCollection.filter(b -> "Computer Science".equalsIgnoreCase(b.getCategory()));
        System.out.println("CS books (filtered with generic method): " + csBooks.size());

        // 3. Generic map with Function
        List<String> titles = bookCollection.map(Book::getTitle);
        System.out.println("All titles (mapped with generic method): " + titles.size());

        // 4. Bounded type parameter: findMax/findMin
        System.out.println("\n--- Bounded Type Parameter: <T extends Comparable<T>> ---");
        Book maxBook = GenericCollectionUtils.findMax(bookService.getAvailableBooks());
        Book minBook = GenericCollectionUtils.findMin(bookService.getAvailableBooks());
        if (maxBook != null) System.out.println("Max book (by title): " + maxBook.getTitle());
        if (minBook != null) System.out.println("Min book (by title): " + minBook.getTitle());

        // 5. Wildcard: countMatching
        System.out.println("\n--- Wildcards: ? extends, ? super ---");
        long expensiveCount = GenericCollectionUtils.countMatching(
                bookService.getAvailableBooks(), b -> b.getPrice() > 500);
        System.out.println("Books over Rs.500 (wildcard count): " + expensiveCount);

        // 6. GenericPair
        System.out.println("\n--- GenericPair<K, V> ---");
        GenericPair<String, Double> cheapest = bookService.getAvailableBooks().stream()
                .min(Comparator.comparingDouble(Book::getPrice))
                .map(b -> new GenericPair<>(b.getTitle(), b.getPrice()))
                .orElse(new GenericPair<>("None", 0.0));
        System.out.println("Cheapest book pair: " + cheapest);

        GenericPair<String, Integer> bookCount = new GenericPair<>("Total Active Books", bookService.getAvailableBooks().size());
        System.out.println("Book count pair: " + bookCount);

        // 7. Diamond operator
        System.out.println("\n--- Diamond Operator <> ---");
        GenericCollectionUtils<Student> studentCollection = new GenericCollectionUtils<>(); // Diamond operator
        for (Student s : studentService.getAllStudents()) {
            studentCollection.add(s);
        }
        System.out.println("Students in generic collection: " + studentCollection.size());

        // 8. copyAll with wildcards
        List<Book> destination = new ArrayList<>();
        GenericCollectionUtils.copyAll(csBooks, destination);
        System.out.println("Copied " + destination.size() + " CS books using wildcard copyAll");

        // 9. formatList with generic method
        String formatted = GenericCollectionUtils.formatList(
                csBooks.subList(0, Math.min(3, csBooks.size())),
                b -> b.getTitle(),
                " | ");
        System.out.println("Formatted (generic): " + formatted);
    }

    // ==========================================
    // MILESTONE 2: File Operations
    // ==========================================

    private void handleFileOperations() {
        System.out.println("\n=== MILESTONE 2: FILE HANDLING & PERSISTENCE ===");
        System.out.println(" 1. Save All Data to CSV Files (FileWriter/BufferedWriter)");
        System.out.println(" 2. Load Data from CSV Files (FileReader/BufferedReader)");
        System.out.println(" 3. Export Data Report (FileOutputStream - byte stream)");
        System.out.println(" 4. StreamTokenizer Demonstration");
        System.out.println(" 5. View Saved Data File Info");

        int choice = readIntChoice("Option (1-5): ", 1, 5);

        switch (choice) {
            case 1:
                handleSaveData();
                break;
            case 2: {
                System.out.println("\n--- Loading Data from CSV Files ---");
                try {
                    boolean loaded = dataStore.loadFromFiles();
                    if (loaded) {
                        System.out.println("[SUCCESS] Data loaded from files.");
                        System.out.printf("  Students: %d | Books: %d | Requests: %d | Ratings: %d%n",
                                studentService.getAllStudents().size(),
                                bookService.getAllBooks().size(),
                                exchangeService.getAllRequests().size(),
                                ratingService.getAllReviews().size());
                    } else {
                        System.out.println("[INFO] No saved data files found.");
                    }
                } catch (IOException e) {
                    System.out.println("[ERROR] Failed to load data: " + e.getMessage());
                }
                break;
            }
            case 3: {
                System.out.println("\n--- Exporting Data Report (Byte Stream) ---");
                try {
                    FileStorageManager.exportDataReport(
                            studentService.getAllStudents(),
                            bookService.getAllBooks(),
                            exchangeService.getAllRequests(),
                            new ArrayList<>(ratingService.getAllReviews()));
                } catch (IOException e) {
                    System.out.println("[ERROR] Failed to export report: " + e.getMessage());
                }
                break;
            }
            case 4: {
                System.out.println("\n--- StreamTokenizer Demo ---");
                try {
                    // Ensure data files exist first
                    if (!FileStorageManager.savedDataExists()) {
                        dataStore.saveToFiles();
                    }
                    FileStorageManager.demonstrateStreamTokenizer();
                } catch (IOException e) {
                    System.out.println("[ERROR] StreamTokenizer demo failed: " + e.getMessage());
                }
                break;
            }
            case 5: {
                System.out.println("\n--- Saved Data File Info ---");
                String[] files = {"data/students.csv", "data/books.csv", "data/requests.csv",
                                  "data/ratings.csv", "data/metadata.dat"};
                for (String f : files) {
                    java.io.File file = new java.io.File(f);
                    if (file.exists()) {
                        System.out.printf("  [EXISTS] %-25s | Size: %,d bytes%n", f, file.length());
                    } else {
                        System.out.printf("  [MISSING] %-25s%n", f);
                    }
                }
                break;
            }
        }
    }

    // ==========================================
    // MILESTONE 2: Exception Handling Demo
    // ==========================================

    private void handleExceptionDemo() throws BookNotFoundException, InvalidExchangeRequestException {
        System.out.println("\n=== MILESTONE 2: EXCEPTION HANDLING DEMONSTRATION ===");
        System.out.println(" 1. BookNotFoundException (custom checked exception)");
        System.out.println(" 2. InvalidStudentException (custom unchecked exception)");
        System.out.println(" 3. InvalidExchangeRequestException (custom checked)");
        System.out.println(" 4. Multi-catch & finally demonstration");
        System.out.println(" 5. Exception propagation with throws");
        System.out.println(" 6. try-with-resources demonstration");

        int choice = readIntChoice("Option (1-6): ", 1, 6);

        switch (choice) {
            case 1: {
                // Demonstrates: throw, throws, custom checked exception
                System.out.println("\n--- BookNotFoundException Demo ---");
                System.out.print("Enter a non-existent Book ID (e.g. BK999): ");
                String badId = scanner.nextLine().trim();
                Book book = bookService.getBookById(badId);
                if (book == null) {
                    // throw custom checked exception - propagates up via throws
                    throw new BookNotFoundException(badId,
                            "No book exists with ID '" + badId + "'. Please check the catalog.");
                }
                break;
            }
            case 2: {
                // Demonstrates: throw, custom unchecked exception (RuntimeException)
                System.out.println("\n--- InvalidStudentException Demo ---");
                System.out.print("Enter a non-existent Student ID (e.g. STU999): ");
                String badStuId = scanner.nextLine().trim();
                Student stu = studentService.getStudentById(badStuId);
                if (stu == null) {
                    // Unchecked exception - no throws needed in method signature
                    throw new InvalidStudentException(badStuId);
                }
                break;
            }
            case 3: {
                // Demonstrates: throw, custom checked exception
                System.out.println("\n--- InvalidExchangeRequestException Demo ---");
                throw new InvalidExchangeRequestException("DEMO_REQ",
                        "This is a demonstration of a custom checked exception for invalid requests.");
            }
            case 4: {
                // Demonstrates: try-catch-finally, multi-catch
                System.out.println("\n--- Multi-catch & Finally Demo ---");
                System.out.print("Enter a value (number, text, or empty): ");
                String input = scanner.nextLine().trim();

                try {
                    if (input.isEmpty()) {
                        throw new IllegalArgumentException("Input cannot be empty");
                    }
                    // Try parsing as integer
                    int value = Integer.parseInt(input);
                    System.out.println("Parsed integer value: " + value);

                    // Try using it as a book ID
                    Book book = bookService.getBookById("BK" + value);
                    if (book == null) {
                        throw new BookNotFoundException("BK" + value);
                    }
                    System.out.println("Found book: " + book.getTitle());

                } catch (NumberFormatException | NullPointerException e) {
                    // Multi-catch: catching multiple disjoint exception types in one block
                    System.out.println("[CAUGHT - Multi-catch] " + e.getClass().getSimpleName() + ": " + e.getMessage());
                } catch (BookNotFoundException e) {
                    // Specific catch for custom exception
                    System.out.println("[CAUGHT - BookNotFoundException] " + e.getMessage());
                } finally {
                    // finally block ALWAYS executes
                    System.out.println("[FINALLY] This block always executes, regardless of exceptions.");
                    System.out.println("[FINALLY] Used for cleanup: closing resources, releasing locks, etc.");
                }
                break;
            }
            case 5: {
                // Demonstrates exception propagation
                System.out.println("\n--- Exception Propagation Demo ---");
                System.out.println("Calling method chain: main -> handleExceptionDemo -> validateBookAccess");
                System.out.println("Exception will propagate up the call stack...");
                validateBookAccess("BK_INVALID_999");
                break;
            }
            case 6: {
                // Demonstrates try-with-resources
                System.out.println("\n--- try-with-resources Demo ---");
                System.out.println("Writing a test file and reading it back...");
                String testFile = "data/test_resource.txt";
                try {
                    // Ensure data dir exists
                    new java.io.File("data").mkdirs();

                    // try-with-resources: writer auto-closed
                    try (java.io.FileWriter writer = new java.io.FileWriter(testFile)) {
                        writer.write("BookLoop try-with-resources test\n");
                        writer.write("This file will be auto-closed when the try block exits.\n");
                        writer.write("Timestamp: " + java.time.LocalDateTime.now() + "\n");
                        System.out.println("[SUCCESS] File written: " + testFile);
                    } // writer.close() called automatically here

                    // try-with-resources: reader auto-closed
                    try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(testFile))) {
                        System.out.println("[SUCCESS] Reading file contents:");
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("  > " + line);
                        }
                    } // reader.close() called automatically here

                } catch (IOException e) {
                    System.out.println("[ERROR] I/O error: " + e.getMessage());
                }
                break;
            }
        }
    }

    /**
     * Helper method demonstrating exception propagation with throws.
     * The exception propagates up: validateBookAccess -> handleExceptionDemo -> run().
     */
    private void validateBookAccess(String bookId) throws BookNotFoundException {
        Book book = bookService.getBookById(bookId);
        if (book == null) {
            // Exception propagates up the call stack
            throw new BookNotFoundException(bookId,
                    "Access denied: Book '" + bookId + "' does not exist in catalog. " +
                    "(This exception propagated from validateBookAccess -> handleExceptionDemo -> run)");
        }
    }

    // ==========================================
    // Save Data to Files
    // ==========================================

    private void handleSaveData() {
        System.out.println("\n--- Saving All Data to Files ---");
        // Demonstrates try-catch-finally pattern
        boolean success = false;
        try {
            FileStorageManager.saveStudents(studentService.getAllStudents());
            FileStorageManager.saveBooks(bookService.getAllBooks());
            FileStorageManager.saveRequests(exchangeService.getAllRequests());
            FileStorageManager.saveRatings(new ArrayList<>(ratingService.getAllReviews()));
            success = true;
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to save data: " + e.getMessage());
        } finally {
            if (success) {
                System.out.println("[SUCCESS] All data saved to CSV files in data/ directory.");
                System.out.printf("  Students: %d | Books: %d | Requests: %d | Ratings: %d%n",
                        studentService.getAllStudents().size(),
                        bookService.getAllBooks().size(),
                        exchangeService.getAllRequests().size(),
                        ratingService.getAllReviews().size());
            } else {
                System.out.println("[WARNING] Data save was incomplete. Check error above.");
            }
        }
    }

    // ==========================================
    // Helper Display & Input Methods
    // ==========================================

    private void displayBookListBrief(String title, List<Book> books) {
        System.out.println("\n--- " + title + " (" + books.size() + " books) ---");
        if (books.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }
        System.out.printf("%-7s | %-32s | %-20s | %-6s | %-7s | %-12s%n",
                "ID", "Title", "Author", "Sem", "Price", "Type");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Book b : books) {
            System.out.printf("%-7s | %-32s | %-20s | Sem %-2d | Rs.%-4.0f | %-12s%n",
                    b.getId(),
                    truncate(b.getTitle(), 32),
                    truncate(b.getAuthor(), 20),
                    b.getSemester(),
                    b.getPrice(),
                    b.getListingType().getDisplayName());
        }
    }

    private int readIntChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                // Demonstrates wrapper class parsing: Integer.parseInt
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input. Please try again.");
            }
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                // Demonstrates wrapper class parsing: Double.parseDouble
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Please enter a value between %.1f and %.1f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            }
        }
    }

    // ==========================================
    // MILESTONE 3: Feature 1 - Book Recommendation Engine
    // ==========================================

    private void handleBookRecommendations() {
        System.out.println("\n=== MILESTONE 3: BOOK RECOMMENDATION ENGINE ===");
        System.out.println(" 1. Smart Recommendations for Active Student (" +
                (currentStudent != null ? currentStudent.getName() : "None") + ")");
        System.out.println(" 2. Recommend by Academic Semester (Curriculum Level)");
        System.out.println(" 3. Recommend by Subject(s) of Interest");
        System.out.println(" 4. Recommend within Price / Budget Range");
        System.out.println(" 5. Custom Multi-Criteria Personalized Search");
        System.out.println(" 6. View Distinct Available Subjects");
        System.out.println(" 7. Back to Main Menu");

        int opt = readIntChoice("Choose recommendation mode (1-7): ", 1, 7);
        switch (opt) {
            case 1: {
                if (currentStudent == null) {
                    System.out.println("[INFO] No student active. Showing top platform recommendations:");
                } else {
                    System.out.printf("[ANALYSIS] Matching curriculum books for %s (%s, %s, Sem %d)...%n",
                            currentStudent.getName(), currentStudent.getCollege(),
                            currentStudent.getDepartment(), currentStudent.getSemester());
                }
                List<Book> recs = recommendationService.recommendForStudent(currentStudent, 6);
                displayRecommendations("Smart Personalized Recommendations", recs, currentStudent);
                break;
            }
            case 2: {
                int sem = readIntChoice("Enter Semester to target (1-8): ", 1, 8);
                String excludeId = (currentStudent != null) ? currentStudent.getId() : null;
                List<Book> recs = recommendationService.recommendBySemester(sem, excludeId);
                displayRecommendations("Curriculum Recommendations for Semester " + sem, recs, currentStudent);
                break;
            }
            case 3: {
                System.out.print("Enter subject(s) of interest (comma-separated, e.g., 'Algorithms, OS'): ");
                String input = scanner.nextLine().trim();
                List<String> subjects = Arrays.stream(input.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                String excludeId = (currentStudent != null) ? currentStudent.getId() : null;
                List<Book> recs = recommendationService.recommendBySubjects(subjects, excludeId);
                displayRecommendations("Recommendations for Subject(s): " + input, recs, currentStudent);
                break;
            }
            case 4: {
                double minPrice = readDouble("Enter Minimum Price in Rs.: ", 0.0, 50000.0);
                double maxPrice = readDouble("Enter Maximum Price in Rs.: ", minPrice, 50000.0);
                String excludeId = (currentStudent != null) ? currentStudent.getId() : null;
                List<Book> recs = recommendationService.recommendByPriceRange(minPrice, maxPrice, excludeId);
                displayRecommendations(String.format("Budget Recommendations (Rs. %.0f - Rs. %.0f)", minPrice, maxPrice), recs, currentStudent);
                break;
            }
            case 5: {
                System.out.printf("Custom Multi-Criteria Search for: %s%n",
                        currentStudent != null ? currentStudent.getName() : "General User");
                double maxPrice = readDouble("Enter Max Budget in Rs.: ", 0.0, 50000.0);
                System.out.print("Enter specific subject keyword (or press Enter for any): ");
                String subjKw = scanner.nextLine().trim();
                List<String> subList = subjKw.isEmpty() ? null : Collections.singletonList(subjKw);
                List<Book> recs = recommendationService.recommendPersonalized(currentStudent, maxPrice, subList, 6);
                displayRecommendations("Custom Personalized Recommendations", recs, currentStudent);
                break;
            }
            case 6: {
                List<String> distinctSubjects = recommendationService.getAvailableDistinctSubjects();
                System.out.println("\n--- DISTINCT AVAILABLE SUBJECTS IN ACTIVE CATALOG (Stream distinct) ---");
                for (int i = 0; i < distinctSubjects.size(); i++) {
                    System.out.printf("  %2d. %s%n", (i + 1), distinctSubjects.get(i));
                }
                break;
            }
            case 7:
            default:
                break;
        }
    }

    private void displayRecommendations(String title, List<Book> books, Student student) {
        System.out.println("\n------------------------------------------------------------------------------------------------------------------");
        System.out.printf("  %s (%d books found)%n", title, books.size());
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        if (books.isEmpty()) {
            System.out.println("  No matching recommendations found for the selected criteria.");
            return;
        }
        System.out.printf("  %-7s | %-30s | %-18s | %-6s | %-7s | %-14s | %-10s%n",
                "ID", "Title", "Author", "Sem", "Price", "Seller Rep", "Condition");
        System.out.println("  --------+--------------------------------+--------------------+--------+---------+----------------+-----------");
        for (Book b : books) {
            System.out.printf("  %-7s | %-30s | %-18s | Sem %-2d | Rs.%-4.0f | %4.1fâ˜… (%-5s) | %-10s%n",
                    b.getId(),
                    truncate(b.getTitle(), 30),
                    truncate(b.getAuthor(), 18),
                    b.getSemester(),
                    b.getPrice(),
                    b.getSellerRating(),
                    truncate(b.getSellerName(), 5),
                    b.getCondition().getDisplayName());
            String reason = recommendationService.getRecommendationReason(b, student);
            System.out.println("          â†³ Why recommended: " + reason);
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }

    // ==========================================
    // MILESTONE 3: Feature 2 - Project Analytics & Reports
    // ==========================================

    private void handleProjectAnalytics() {
        System.out.println("\n=== MILESTONE 3: PROJECT ANALYTICS & EXECUTIVE REPORTS ===");
        System.out.println(" 1. Comprehensive Platform Analytics Dashboard (KPIs, Lifecycle, Matrix, Leaderboard)");
        System.out.println(" 2. Subject Popularity Breakdown (Frequency Count & Ranking)");
        System.out.println(" 3. Alphabetical Subject Index (TreeMap Naturally Sorted Keys)");
        System.out.println(" 4. Semester Distribution Matrix (Native 1D Array Analysis)");
        System.out.println(" 5. Most Active Student Sellers Leaderboard");
        System.out.println(" 6. Campus & University Participation Network (Student Counts)");
        System.out.println(" 7. Back to Main Menu");

        int choice = readIntChoice("Choose report view (1-7): ", 1, 7);
        switch (choice) {
            case 1:
                System.out.println(projectReportService.generateComprehensiveReport());
                break;
            case 2: {
                System.out.println("\n--- SUBJECT POPULARITY RANKING (HashMap + Stream grouping & sorting) ---");
                System.out.printf("  %-4s | %-36s | %-12s%n", "Rank", "Subject Name", "Total Books");
                System.out.println("  -----+--------------------------------------+-------------");
                Map<String, Long> popular = projectReportService.getSubjectPopularityMap();
                int r = 1;
                for (Map.Entry<String, Long> e : popular.entrySet()) {
                    System.out.printf("  #%-3d | %-36s | %-12d%n", r++, e.getKey(), e.getValue());
                }
                break;
            }
            case 3: {
                System.out.println("\n--- SUBJECTS SORTED ALPHABETICALLY (TreeMap Naturally Sorted Keys) ---");
                TreeMap<String, Long> sortedSubjects = projectReportService.getSubjectsSortedAlphabetically();
                for (Map.Entry<String, Long> e : sortedSubjects.entrySet()) {
                    System.out.printf("  - %-36s : %2d book(s)%n", e.getKey(), e.getValue());
                }
                break;
            }
            case 4: {
                System.out.println("\n--- TEXTBOOK DISTRIBUTION ACROSS SEMESTERS (Native 1D Array) ---");
                int[] dist = projectReportService.getSemesterDistribution();
                for (int sem = 1; sem <= 8; sem++) {
                    System.out.printf("  Semester %d: %2d books%n", sem, dist[sem]);
                }
                break;
            }
            case 5: {
                System.out.println("\n--- TOP ACTIVE STUDENT SELLERS LEADERBOARD ---");
                System.out.printf("  %-4s | %-18s | %-22s | %-6s | %-7s | %-6s | %-6s | %-8s%n",
                        "Rank", "Student Name", "College", "Rating", "Listed", "Sold", "Swapped", "Score");
                System.out.println("  -----+--------------------+------------------------+--------+---------+--------+--------+---------");
                List<ProjectReportService.SellerActivity> top = projectReportService.getMostActiveSellers(10);
                int rank = 1;
                for (ProjectReportService.SellerActivity sa : top) {
                    System.out.printf("  #%-3d | %-18s | %-22s | %4.1fâ˜…  | %-7d | %-6d | %-6d | %-8d%n",
                            rank++,
                            sa.getStudent().getName(),
                            truncate(sa.getStudent().getCollege(), 22),
                            sa.getRating(),
                            sa.getBooksListed(),
                            sa.getBooksSold(),
                            sa.getBooksExchanged(),
                            sa.getTotalActivityScore());
                }
                break;
            }
            case 6: {
                System.out.println("\n--- CAMPUS PARTICIPATION BY UNIVERSITY (HashMap Aggregation) ---");
                Map<String, Integer> campuses = projectReportService.getCollegeParticipationMap();
                for (Map.Entry<String, Integer> e : campuses.entrySet()) {
                    System.out.printf("  - %-42s : %2d student(s)%n", e.getKey(), e.getValue());
                }
                break;
            }
            case 7:
            default:
                break;
        }
    }

    // ==========================================
    // MILESTONE 3: Feature 3 - Data Validation Utility
    // ==========================================

    private void handleDataValidationDemos() {
        System.out.println("\n=== MILESTONE 3: DATA VALIDATION UTILITY (ValidationUtil) ===");
        System.out.println(" 1. Interactive Student Registration Validator (name, email, phone, semester)");
        System.out.println(" 2. Interactive Book Listing Validator (title, author, price, listing type)");
        System.out.println(" 3. Interactive Exchange Request Validator (self-trade check, book existence)");
        System.out.println(" 4. Interactive Peer Rating Validator (self-rating prevention, 1.0-5.0 scale)");
        System.out.println(" 5. System ID Format Validator (STU, BK, REQ, REV patterns)");
        System.out.println(" 6. Back to Main Menu");

        int choice = readIntChoice("Choose validation test (1-6): ", 1, 6);
        switch (choice) {
            case 1: {
                System.out.println("\n--- Student Details Validation Test ---");
                System.out.print("Enter name to test: ");
                String name = scanner.nextLine();
                System.out.print("Enter email to test: ");
                String email = scanner.nextLine();
                System.out.print("Enter phone to test: ");
                String phone = scanner.nextLine();
                System.out.print("Enter semester to test (1-8): ");
                String semStr = scanner.nextLine();
                try {
                    int sem = ValidationUtil.validateAndParseSemester(semStr);
                    ValidationUtil.validateStudent(name, email, phone, "Test College", "CSE", sem);
                    System.out.println("[SUCCESS] All student registration fields PASSED validation!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION REJECTED] " + e.getMessage());
                    System.out.println(e.getDiagnosticSummary());
                }
                break;
            }
            case 2: {
                System.out.println("\n--- Book Listing Validation Test ---");
                System.out.print("Enter title to test: ");
                String title = scanner.nextLine();
                System.out.print("Enter author to test: ");
                String author = scanner.nextLine();
                System.out.print("Enter price in Rs.: ");
                String priceStr = scanner.nextLine();
                try {
                    double price = ValidationUtil.validateAndParsePrice(priceStr);
                    ValidationUtil.validateBook(title, author, "", "Computer Science", 3, price, ListingType.FOR_SALE);
                    System.out.println("[SUCCESS] All book listing fields PASSED validation!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION REJECTED] " + e.getMessage());
                    System.out.println(e.getDiagnosticSummary());
                }
                break;
            }
            case 3: {
                System.out.println("\n--- Exchange Request Validation Test ---");
                System.out.print("Enter Requested Book ID (e.g. BK201): ");
                String bookId = scanner.nextLine().trim();
                System.out.print("Enter Seller Student ID (e.g. STU102): ");
                String sellerId = scanner.nextLine().trim();
                System.out.print("Enter Requester Student ID (e.g. STU101): ");
                String requesterId = scanner.nextLine().trim();
                System.out.print("Enter Request Type ('Purchase' or 'Exchange'): ");
                String type = scanner.nextLine().trim();
                String offeredId = null;
                if ("exchange".equalsIgnoreCase(type)) {
                    System.out.print("Enter Offered Book ID: ");
                    offeredId = scanner.nextLine().trim();
                }
                try {
                    ValidationUtil.validateExchangeRequest(bookId, sellerId, requesterId, type, offeredId);
                    System.out.println("[SUCCESS] Exchange request PASSED validation! No self-trade and valid arguments.");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION REJECTED] " + e.getMessage());
                    System.out.println(e.getDiagnosticSummary());
                }
                break;
            }
            case 4: {
                System.out.println("\n--- Peer Rating Validation Test ---");
                System.out.print("Enter Seller Student ID: ");
                String sellerId = scanner.nextLine().trim();
                System.out.print("Enter Reviewer Student ID: ");
                String reviewerId = scanner.nextLine().trim();
                System.out.print("Enter Rating Score (1.0 to 5.0): ");
                String scoreStr = scanner.nextLine().trim();
                System.out.print("Enter Review Comment: ");
                String comment = scanner.nextLine().trim();
                try {
                    double score = ValidationUtil.validateAndParseRatingScore(scoreStr);
                    ValidationUtil.validateRating(sellerId, reviewerId, score, comment);
                    System.out.println("[SUCCESS] Peer rating PASSED validation!");
                } catch (ValidationException e) {
                    System.out.println("\n[VALIDATION REJECTED] " + e.getMessage());
                    System.out.println(e.getDiagnosticSummary());
                }
                break;
            }
            case 5: {
                System.out.println("\n--- System ID Pattern Validation Test ---");
                System.out.print("Enter an ID to validate (e.g., STU101, BK205, REQ301, REV401): ");
                String id = scanner.nextLine().trim();
                if (id.startsWith("STU")) {
                    boolean valid = ValidationUtil.isValidStudentId(id);
                    System.out.println("Evaluated as Student ID: " + (valid ? "VALID" : "INVALID"));
                } else if (id.startsWith("BK")) {
                    boolean valid = ValidationUtil.isValidBookId(id);
                    System.out.println("Evaluated as Book ID: " + (valid ? "VALID" : "INVALID"));
                } else {
                    System.out.println("Unknown ID prefix. Expected prefix 'STU' or 'BK'.");
                }
                break;
            }
            case 6:
            default:
                break;
        }
    }

    // ==========================================
    // MILESTONE 3: Feature 4 - CSV Export Functionality
    // ==========================================

    private void handleCsvExports() {
        System.out.println("\n=== MILESTONE 3: CSV EXPORT FUNCTIONALITY (CsvExportService) ===");
        System.out.println("NOTE: All export files are safely saved in the separate 'exports/' directory.");
        System.out.println("      Internal application persistence files in 'data/' are NEVER overwritten.");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. Export All 5 Datasets to CSV (Students, Books, Requests, Ratings, Analytics)");
        System.out.println(" 2. Export Student Directory (exports/students_export.csv)");
        System.out.println(" 3. Export Book Listings Catalog (exports/books_export.csv)");
        System.out.println(" 4. Export Exchange Transaction History (exports/requests_export.csv)");
        System.out.println(" 5. Export Seller Ratings & Reviews (exports/ratings_export.csv)");
        System.out.println(" 6. Export Analytics & Executive Summary (exports/analytics_summary_export.csv)");
        System.out.println(" 7. Back to Main Menu");

        int choice = readIntChoice("Choose export option (1-7): ", 1, 7);
        try {
            switch (choice) {
                case 1: {
                    System.out.println("\nExporting all datasets to exports/ directory...");
                    List<CsvExportService.ExportResult> results = csvExportService.exportAll(dataStore, projectReportService);
                    System.out.println("\n--- BATCH EXPORT SUMMARY ---");
                    for (CsvExportService.ExportResult res : results) {
                        System.out.println("  " + res);
                    }
                    System.out.println("\n[SUCCESS] All 5 CSV datasets successfully exported to exports/ directory.");
                    break;
                }
                case 2: {
                    String path = CsvExportService.DEFAULT_EXPORT_DIR + File.separator + "students_export.csv";
                    CsvExportService.ExportResult res = csvExportService.exportStudents(studentService.getAllStudents(), path);
                    System.out.println("\n[SUCCESS] " + res);
                    break;
                }
                case 3: {
                    String path = CsvExportService.DEFAULT_EXPORT_DIR + File.separator + "books_export.csv";
                    CsvExportService.ExportResult res = csvExportService.exportBooks(bookService.getAllBooks(), path);
                    System.out.println("\n[SUCCESS] " + res);
                    break;
                }
                case 4: {
                    String path = CsvExportService.DEFAULT_EXPORT_DIR + File.separator + "requests_export.csv";
                    CsvExportService.ExportResult res = csvExportService.exportRequests(exchangeService.getAllRequests(), path);
                    System.out.println("\n[SUCCESS] " + res);
                    break;
                }
                case 5: {
                    String path = CsvExportService.DEFAULT_EXPORT_DIR + File.separator + "ratings_export.csv";
                    CsvExportService.ExportResult res = csvExportService.exportRatings(ratingService.getAllReviews(), path);
                    System.out.println("\n[SUCCESS] " + res);
                    break;
                }
                case 6: {
                    String path = CsvExportService.DEFAULT_EXPORT_DIR + File.separator + "analytics_summary_export.csv";
                    CsvExportService.ExportResult res = csvExportService.exportAnalyticsReport(projectReportService, path);
                    System.out.println("\n[SUCCESS] " + res);
                    break;
                }
                case 7:
                default:
                    break;
            }
        } catch (IOException e) {
            System.out.println("\n[ERROR] File export I/O failure: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\n[SECURITY ERROR] " + e.getMessage());
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}
