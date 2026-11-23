package com.bookloop;

import com.bookloop.data.DataStore;
import com.bookloop.data.DummyData;
import com.bookloop.exception.*;
import com.bookloop.model.*;
import com.bookloop.persistence.CsvExportService;
import com.bookloop.persistence.FileStorageManager;
import com.bookloop.service.*;
import com.bookloop.threading.AutoSaveManager;
import com.bookloop.util.StringUtils;
import com.bookloop.util.ValidationUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Main Entry Point: BookLoop - Student Peer-to-Peer Book Exchange Platform.
 * Menu-driven Java console application demonstrating:
 * - Clean separation of UI (menus), Business Logic (Services), and Data (DataStore/DummyData)
 * - Dynamic student context switching (defaults to Aarav Sharma, DTU)
 * - Genuine student marketplace features (Browse, Search, Sort, Request, Rate, Recommend, Analyze)
 * - Robust input validation and user-friendly error recovery
 * - Background auto-save daemon thread for seamless persistence
 * - File I/O with clean CSV import and export
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

        // Initialize data: loads from files if available, otherwise baseline dummy data
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
            int choice = readIntChoice("Enter your choice (1-17): ", 1, 17);

            try {
                switch (choice) {
                    case 1:
                        handleViewAllBooks();
                        break;
                    case 2:
                        handleSearchBooks();
                        break;
                    case 3:
                        handleSortBooks();
                        break;
                    case 4:
                        handleViewBookAndSeller();
                        break;
                    case 5:
                        handleBookRecommendations();
                        break;
                    case 6:
                        handleAddBookListing();
                        break;
                    case 7:
                        handleUpdateDeleteMyBook();
                        break;
                    case 8:
                        handleSendExchangeRequest();
                        break;
                    case 9:
                        handleManageReceivedRequests();
                        break;
                    case 10:
                        handleTrackSentRequests();
                        break;
                    case 11:
                        handleRateSeller();
                        break;
                    case 12:
                        handleRegisterStudent();
                        break;
                    case 13:
                        handleSwitchStudent();
                        break;
                    case 14:
                        handleProjectAnalytics();
                        break;
                    case 15:
                        handleCsvExports();
                        break;
                    case 16:
                        handleSystemInfoAndReset();
                        break;
                    case 17:
                        handleSaveData();
                        if (autoSaveManager != null) autoSaveManager.shutdown();
                        running = false;
                        System.out.println("\nThank you for using BookLoop! Happy studying and exchanging.");
                        break;
                    default:
                        System.out.println("Invalid selection. Please choose an option from the menu.");
                }
            } catch (ValidationException e) {
                System.out.println("\n[VALIDATION ERROR] " + e.getMessage());
                if (e.getFieldName() != null) {
                    System.out.println("  Field: '" + e.getFieldName() + "' | Value: \"" + e.getInvalidValue() + "\"");
                }
            } catch (BookNotFoundException e) {
                System.out.println("\n[ERROR] " + e.getMessage() + " (Book ID: " + e.getBookId() + ")");
            } catch (InvalidStudentException e) {
                System.out.println("\n[ERROR] " + e.getMessage());
            } catch (InvalidExchangeRequestException e) {
                System.out.println("\n[ERROR] " + e.getMessage());
            } catch (SecurityException | IllegalStateException e) {
                System.out.println("\n[ERROR] " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("\n[ERROR] Invalid input: " + e.getMessage());
            } catch (Exception e) {
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
        System.out.println(" === BOOK MARKETPLACE ===");
        System.out.println(" 1.  Browse All Available Books (Catalog)");
        System.out.println(" 2.  Search Books (by Title, Subject, Semester, or Author)");
        System.out.println(" 3.  Sort Books (by Title, Author, Price Low-High, Price High-Low)");
        System.out.println(" 4.  View Detailed Book & Seller Profile");
        System.out.println(" 5.  Smart Book Recommendations (by Semester, Subject, or Budget)");
        System.out.println(" 6.  List / Add a Book for Sale or Exchange");
        System.out.println(" 7.  Manage Personal Book Listings (Update Price/Status or Delete)");
        System.out.println(" === TRANSACTIONS & REQUESTS ===");
        System.out.println(" 8.  Send Exchange or Purchase Request");
        System.out.println(" 9.  Manage Received Requests (Accept / Reject / Advance Stage)");
        System.out.println(" 10. Track Sent Requests & View Timeline History");
        System.out.println(" 11. Rate a Seller (Peer Reviews & Star Ratings)");
        System.out.println(" === USER PROFILE & PLATFORM ===");
        System.out.println(" 12. Register a New Student");
        System.out.println(" 13. Switch Active Student Context");
        System.out.println(" 14. Platform Analytics & Executive Report (KPIs, Subject Trends)");
        System.out.println(" 15. Export System Reports to CSV");
        System.out.println(" 16. System Activity Log & Reset Data to Baseline");
        System.out.println(" 17. Save & Exit (Persists Data to CSV Files)");
        System.out.println("--------------------------------------------------------------------------------");
    }

    // ==========================================
    // Feature 1: Browse Catalog
    // ==========================================

    private void handleViewAllBooks() {
        System.out.println("\n=== AVAILABLE TEXTBOOKS IN CATALOG ===");
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
    // Feature 2: Search Books
    // ==========================================

    private void handleSearchBooks() {
        System.out.println("\n=== SEARCH TEXTBOOKS (OVERLOADED SEARCH) ===");
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
    // Feature 3: Sort Books
    // ==========================================

    private void handleSortBooks() {
        System.out.println("\n=== SORT BOOKS (COMPARABLE & COMPARATOR) ===");
        System.out.println(" 1. Natural Sort via Comparable<Book> (Title A to Z)");
        System.out.println(" 2. Custom Sort: Author Name Alphabetical (A to Z)");
        System.out.println(" 3. Custom Sort: Price (Low to High)");
        System.out.println(" 4. Custom Sort: Price (High to Low)");

        int choice = readIntChoice("Select sorting option (1-4): ", 1, 4);
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
    // Feature 4: View Book & Seller Details
    // ==========================================

    private void handleViewBookAndSeller() throws BookNotFoundException {
        System.out.println("\n=== VIEW BOOK & SELLER DETAILS ===");
        System.out.print("Enter Book ID (e.g. BK201, BK205): ");
        String id = scanner.nextLine().trim();

        Book book = bookService.getBookById(id);
        if (book == null) {
            throw new BookNotFoundException(id);
        }

        book.displayDetails();

        Student seller = studentService.getStudentById(book.getSellerId());
        if (seller != null) {
            System.out.println("\n--- SELLER INFORMATION ---");
            seller.displayProfile();

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
    // Feature 5: Smart Book Recommendations
    // ==========================================

    private void handleBookRecommendations() {
        System.out.println("\n=== SMART BOOK RECOMMENDATIONS (STREAM API) ===");
        System.out.println(" 1. Personalized Recommendations for Current Student (" +
                (currentStudent != null ? currentStudent.getName() + " - Sem " + currentStudent.getSemester() : "No active student") + ")");
        System.out.println(" 2. Filter Recommendations by Semester");
        System.out.println(" 3. Filter Recommendations by Academic Subject");
        System.out.println(" 4. Filter Recommendations by Budget / Price Range");
        System.out.println(" 5. Back to Main Menu");

        int choice = readIntChoice("Choose recommendation view (1-5): ", 1, 5);

        switch (choice) {
            case 1: {
                if (currentStudent == null) {
                    System.out.println("[INFO] Please select an active student first.");
                    break;
                }
                List<Book> recs = recommendationService.recommendForStudent(currentStudent, 8);
                displayRecommendations("Personalized Recommendations for " + currentStudent.getName(), recs, currentStudent);
                break;
            }
            case 2: {
                int targetSem = readIntChoice("Enter Target Semester (1-8): ", 1, 8);
                String excludeId = (currentStudent != null) ? currentStudent.getId() : null;
                List<Book> recs = recommendationService.recommendBySemester(targetSem, excludeId);
                displayRecommendations("Recommended Textbooks for Semester " + targetSem, recs, currentStudent);
                break;
            }
            case 3: {
                System.out.print("Enter Subject keyword (e.g., 'Operating Systems', 'Algorithms'): ");
                String subj = scanner.nextLine().trim();
                String excludeId = (currentStudent != null) ? currentStudent.getId() : null;
                List<Book> recs = recommendationService.recommendBySubjects(Collections.singletonList(subj), excludeId);
                displayRecommendations("Recommendations for Subject: " + subj, recs, currentStudent);
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
            case 5:
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
            System.out.printf("  %-7s | %-30s | %-18s | Sem %-2d | Rs.%-4.0f | %4.1f★ (%-5s) | %-10s%n",
                    b.getId(),
                    truncate(b.getTitle(), 30),
                    truncate(b.getAuthor(), 18),
                    b.getSemester(),
                    b.getPrice(),
                    b.getSellerRating(),
                    truncate(b.getSellerName(), 5),
                    b.getCondition().getDisplayName());
            String reason = recommendationService.getRecommendationReason(b, student);
            System.out.println("          ↳ Why recommended: " + reason);
        }
        System.out.println("------------------------------------------------------------------------------------------------------------------");
    }

    // ==========================================
    // Feature 6: Add Book Listing
    // ==========================================

    private void handleAddBookListing() throws ValidationException {
        System.out.println("\n=== LIST A BOOK FOR SALE OR EXCHANGE ===");
        if (currentStudent == null) {
            System.out.println("[ERROR] Please register or switch to an active student first.");
            return;
        }

        System.out.println("Listing book as seller: " + currentStudent.getName() + " (" + currentStudent.getCollege() + ")");

        System.out.print("Enter Book Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter Author(s): ");
        String author = scanner.nextLine().trim();

        System.out.print("Enter ISBN (or press Enter for N/A): ");
        String isbn = scanner.nextLine().trim();
        isbn = StringUtils.formatISBN(isbn.isEmpty() ? "N/A" : isbn);

        System.out.print("Enter Academic Subject (e.g. Data Structures, Operating Systems): ");
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

        System.out.print("Enter Description & Notes: ");
        String desc = scanner.nextLine().trim();

        Book added = bookService.addBook(
                title, author, isbn, subject, category, semester, edition,
                condition, price, listingType, preferredExchange, desc, currentStudent
        );

        System.out.println("\n[SUCCESS] Book listed successfully on BookLoop!");
        added.displayDetails();
    }

    // ==========================================
    // Feature 7: Manage Personal Listings
    // ==========================================

    private void handleUpdateDeleteMyBook() {
        System.out.println("\n=== MANAGE PERSONAL BOOK LISTINGS ===");
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
    // Feature 8: Send Exchange Request
    // ==========================================

    private void handleSendExchangeRequest() throws BookNotFoundException, InvalidExchangeRequestException {
        System.out.println("\n=== SEND EXCHANGE OR PURCHASE REQUEST ===");
        if (currentStudent == null) return;

        System.out.print("Enter the Book ID you want to acquire (e.g. BK201, BK202): ");
        String bookId = scanner.nextLine().trim();

        Book targetBook = bookService.getBookById(bookId);
        if (targetBook == null) {
            throw new BookNotFoundException(bookId);
        }

        if (targetBook.getSellerId().equalsIgnoreCase(currentStudent.getId())) {
            throw new InvalidExchangeRequestException("SELF_TRADE",
                    "You cannot send an exchange or purchase request for your own listed book.");
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
    // Feature 9: Manage Received Requests
    // ==========================================

    private void handleManageReceivedRequests() {
        System.out.println("\n=== MANAGE RECEIVED REQUESTS (SELLER VIEW) ===");
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
    // Feature 10: Track Sent Requests
    // ==========================================

    private void handleTrackSentRequests() {
        System.out.println("\n=== TRACK SENT REQUESTS & TRANSACTION HISTORY ===");
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
            String symbol = (stepNum < currentStep) ? "[✔ DONE]" :
                    (stepNum == currentStep) ? "[➤ ACTIVE]" : "[  PENDING ]";
            System.out.printf("  %-13s %s%n", symbol, steps[i]);
        }
        System.out.println("----------------------------------------");
    }

    // ==========================================
    // Feature 11: Rate a Seller
    // ==========================================

    private void handleRateSeller() throws ValidationException {
        System.out.println("\n=== RATE A SELLER (PEER REVIEWS) ===");
        if (currentStudent == null) return;

        System.out.print("Enter the Seller ID to rate (e.g. STU102, STU103): ");
        String sellerId = scanner.nextLine().trim();

        Student seller = studentService.getStudentById(sellerId);
        if (seller == null) {
            System.out.println("[ERROR] Seller not found with ID: " + sellerId);
            return;
        }

        if (seller.getId().equalsIgnoreCase(currentStudent.getId())) {
            throw new ValidationException("selfRating", sellerId, "Students cannot submit a peer rating for themselves.");
        }

        System.out.printf("Rating Seller: %s (%s)%n", seller.getName(), seller.getCollege());
        double stars = readDouble("Enter Rating in Stars (1.0 to 5.0): ", 1.0, 5.0);

        System.out.print("Enter Written Review / Feedback: ");
        String comment = scanner.nextLine().trim();

        ratingService.rateSeller(sellerId, currentStudent, stars, comment);
        System.out.println("\n[SUCCESS] Review submitted successfully!");
        System.out.printf("Updated Seller Rep: %.1f ★ (%d total reviews)%n",
                seller.getRating(), seller.getReviewCount());
    }

    // ==========================================
    // Feature 12: Register Student
    // ==========================================

    private void handleRegisterStudent() throws InvalidStudentException {
        System.out.println("\n=== REGISTER A NEW STUDENT ===");
        System.out.print("Enter Student Full Name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            throw new InvalidStudentException("", "Student name cannot be empty.");
        }

        System.out.print("Enter College Email (e.g. name@college.ac.in): ");
        String email = scanner.nextLine().trim();

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
    // Feature 13: Switch Student
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
    // Feature 14: Platform Analytics
    // ==========================================

    private void handleProjectAnalytics() {
        System.out.println("\n=== PLATFORM ANALYTICS & EXECUTIVE REPORTS ===");
        System.out.println(" 1. Comprehensive Platform Analytics Dashboard (KPIs, Metrics, Leaderboard)");
        System.out.println(" 2. Subject Popularity Breakdown (Frequency Count & Ranking)");
        System.out.println(" 3. Alphabetical Subject Index (TreeMap Sorted Keys)");
        System.out.println(" 4. Semester Distribution Matrix (1D Array Analysis)");
        System.out.println(" 5. Top Active Student Sellers Leaderboard");
        System.out.println(" 6. Back to Main Menu");

        int choice = readIntChoice("Choose report view (1-6): ", 1, 6);
        switch (choice) {
            case 1:
                System.out.println(projectReportService.generateComprehensiveReport());
                break;
            case 2: {
                System.out.println("\n--- SUBJECT POPULARITY RANKING (HashMap + Stream grouping) ---");
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
                System.out.println("\n--- SUBJECTS SORTED ALPHABETICALLY (TreeMap Sorted Keys) ---");
                TreeMap<String, Long> sortedSubjects = projectReportService.getSubjectsSortedAlphabetically();
                for (Map.Entry<String, Long> e : sortedSubjects.entrySet()) {
                    System.out.printf("  - %-36s : %2d book(s)%n", e.getKey(), e.getValue());
                }
                break;
            }
            case 4: {
                System.out.println("\n--- TEXTBOOK DISTRIBUTION ACROSS SEMESTERS (1D Array) ---");
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
                    System.out.printf("  #%-3d | %-18s | %-22s | %4.1f★  | %-7d | %-6d | %-6d | %-8d%n",
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
            case 6:
            default:
                break;
        }
    }

    // ==========================================
    // Feature 15: CSV Reports Export
    // ==========================================

    private void handleCsvExports() {
        System.out.println("\n=== EXPORT SYSTEM REPORTS TO CSV (FILE I/O) ===");
        System.out.println("NOTE: All exported files are written to the 'exports/' directory.");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println(" 1. Export All 5 Datasets to CSV (Students, Books, Requests, Ratings, Analytics)");
        System.out.println(" 2. Export Student Directory (exports/students_export.csv)");
        System.out.println(" 3. Export Book Listings Catalog (exports/books_export.csv)");
        System.out.println(" 4. Export Exchange Transaction History (exports/requests_export.csv)");
        System.out.println(" 5. Export Seller Ratings & Reviews (exports/ratings_export.csv)");
        System.out.println(" 6. Back to Main Menu");

        int choice = readIntChoice("Choose export option (1-6): ", 1, 6);
        try {
            switch (choice) {
                case 1: {
                    System.out.println("\nExporting all datasets to exports/ directory...");
                    List<CsvExportService.ExportResult> results = csvExportService.exportAll(dataStore, projectReportService);
                    System.out.println("\n--- BATCH EXPORT SUMMARY ---");
                    for (CsvExportService.ExportResult res : results) {
                        System.out.println("  " + res);
                    }
                    System.out.println("\n[SUCCESS] All datasets successfully exported to exports/ directory.");
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
                    CsvExportService.ExportResult res = csvExportService.exportRatings(new ArrayList<>(ratingService.getAllReviews()), path);
                    System.out.println("\n[SUCCESS] " + res);
                    break;
                }
                case 6:
                default:
                    break;
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to export CSV file: " + e.getMessage());
        }
    }

    // ==========================================
    // Feature 16: Activity Log & Reset Data
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
                    System.out.println("  • " + s);
                }
                break;
            case 3:
                System.out.println("\n--- UNIQUE AUTHORS (TreeSet - Alphabetical) ---");
                for (String a : dataStore.getUniqueAuthors()) {
                    System.out.println("  • " + a);
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
    // Feature 17: Save Data to Files
    // ==========================================

    private void handleSaveData() {
        System.out.println("\n--- Saving All Data to Files ---");
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
            System.out.printf("%-7s | %-32s | %-20s | Sem %-2d | Rs.%-5.0f | %-12s%n",
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
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Please enter a number between %d and %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric input. Please enter a valid number.");
            }
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.printf("Please enter a value between %.1f and %.1f.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid decimal number.");
            }
        }
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }
}
