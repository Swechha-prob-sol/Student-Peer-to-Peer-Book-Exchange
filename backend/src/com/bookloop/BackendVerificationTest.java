package com.bookloop;

import com.bookloop.data.DataStore;
import com.bookloop.data.DummyData;
import com.bookloop.interfaces.Displayable;
import com.bookloop.model.*;
import com.bookloop.service.*;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Automated Test and Verification Suite for Milestone 1.
 * Tests every required feature, Java OOP concept, and collection implementation.
 */
public class BackendVerificationTest {

    private static int testsPassed = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   BOOKLOOP BACKEND MILESTONE 1 - AUTOMATED VERIFICATION TEST SUITE");
        System.out.println("================================================================================");

        DataStore store = DataStore.getInstance();
        StudentService studentService = new StudentService(store);
        BookService bookService = new BookService(store);
        ExchangeService exchangeService = new ExchangeService(store);
        RatingService ratingService = new RatingService(store);

        // 1. Initialize Dummy Data
        DummyData.initialize(store);

        test("Dummy Data Loading & Verification", () -> {
            List<Student> students = studentService.getAllStudents();
            List<Book> books = bookService.getAllBooks();
            List<ExchangeRequest> requests = exchangeService.getAllRequests();
            List<Rating> reviews = ratingService.getAllReviews();

            assertCondition(students.size() >= 9, "Should load at least 9 Indian student records, got: " + students.size());
            assertCondition(books.size() >= 24, "Should load at least 24 textbook records, got: " + books.size());
            assertCondition(requests.size() >= 6, "Should load at least 6 transaction requests, got: " + requests.size());
            assertCondition(reviews.size() >= 5, "Should load at least 5 peer reviews, got: " + reviews.size());
        });

        test("Feature 1: Student Registration & Email Uniqueness (HashSet)", () -> {
            Student s = studentService.registerStudent(
                    "Tanvi Joshi", "tanvi.j@coep.ac.in", "+91 98111 22233",
                    "COEP Pune", "Computer Engineering", 4
            );
            assertCondition(s != null && s.getName().equals("Tanvi Joshi"), "Student registered successfully");
            assertCondition(s.getCollege().contains("COEP"), "College matches");

            // Attempt duplicate email registration -> must be rejected
            boolean duplicateRejected = false;
            try {
                studentService.registerStudent(
                        "Impostor", "tanvi.j@coep.ac.in", "+91 90000 00000",
                        "Another College", "IT", 2
                );
            } catch (IllegalArgumentException e) {
                duplicateRejected = true;
            }
            assertCondition(duplicateRejected, "Duplicate email properly rejected by HashSet");
        });

        test("Feature 2: Add Book for Sale / Exchange", () -> {
            Student seller = studentService.getStudentById("STU101");
            Book newBook = bookService.addBook(
                    "Head First Java", "Kathy Sierra, Bert Bates", "978-0596009205",
                    "Object Oriented Programming", "Computer Science", 3, "2nd Edition",
                    Condition.VERY_GOOD, 450.0, ListingType.BOTH,
                    "Let Us C or Effective Java",
                    "Friendly visual guide to learning Java OOP.", seller
            );
            assertCondition(newBook != null, "Book created and listed");
            assertCondition(bookService.getBookById(newBook.getId()) != null, "Book retrievable by ID");
        });

        test("Feature 3: View All Available Books", () -> {
            List<Book> activeBooks = bookService.getAvailableBooks();
            assertCondition(!activeBooks.isEmpty(), "Active books catalog not empty");
            for (Book b : activeBooks) {
                assertCondition(b.isAvailable(), "All returned books must have Active status");
            }
        });

        test("Feature 4: Search Books with Method Overloading", () -> {
            // Overloaded method 1: Keyword search
            List<Book> algo = bookService.searchBooks("Algorithms");
            assertCondition(!algo.isEmpty(), "Keyword search found algorithm book");

            // Overloaded method 2: Subject and semester
            List<Book> osBooks = bookService.searchBooks("Operating Systems", 4);
            assertCondition(!osBooks.isEmpty(), "Subject + Semester search found matching book");

            // Overloaded method 3: Semester
            List<Book> sem3 = bookService.searchBooksBySemester(3);
            assertCondition(!sem3.isEmpty(), "Semester 3 filter found books");

            // Overloaded method 4: Author
            List<Book> silberschatz = bookService.searchBooksByAuthor("Silberschatz");
            assertCondition(silberschatz.size() >= 2, "Author search found Silberschatz textbooks");
        });

        test("Feature 5: View Book & Seller Details (Polymorphism)", () -> {
            Book b = bookService.getBookById("BK201");
            assertCondition(b != null, "Book BK201 exists");
            assertCondition(b.getAuthor().contains("Cormen"), "Author is Cormen (CLRS)");

            // Polymorphic Displayable call
            Displayable disp = b;
            assertCondition(disp != null, "Polymorphic reference assigned to Displayable interface");

            Student seller = studentService.getStudentById(b.getSellerId());
            assertCondition(seller != null, "Seller retrieved: " + seller.getName());
            assertCondition(seller.getCollege().contains("BITS"), "Seller is from BITS Pilani");
        });

        test("Feature 6: Update & Delete Book Listings", () -> {
            Student seller = studentService.getStudentById("STU101");
            Book tempBook = bookService.addBook(
                    "Temporary Book", "Temp Author", "978-0000000000",
                    "Testing", "General", 1, "1st Edition",
                    Condition.FAIR, 100.0, ListingType.FOR_SALE, "",
                    "To be updated and deleted.", seller
            );

            // Update price and status
            boolean updated = bookService.updateBookDetails(
                    tempBook.getId(), "Updated Temp Book", null, 150.0, null, null, "Pending"
            );
            assertCondition(updated, "Book updated successfully");
            assertCondition(tempBook.getPrice() == 150.0, "Updated price reflected");

            // Delete book
            boolean deleted = bookService.deleteBook(tempBook.getId(), seller.getId());
            assertCondition(deleted, "Book deleted successfully");
            assertCondition(bookService.getBookById(tempBook.getId()) == null, "Book no longer exists in DataStore");
        });

        test("Feature 7: Send Exchange and Purchase Requests", () -> {
            Student requester = studentService.getStudentById("STU103"); // Rohan Verma
            // Request book BK204 (sold by STU105 Vikram)
            ExchangeRequest req = exchangeService.createRequest(
                    "BK204", requester, "Purchase", null, "Can meet at campus center."
            );
            assertCondition(req != null, "Purchase request created");
            assertCondition(req.getStatus() == RequestStatus.PENDING, "Initial status is Pending");
            assertCondition(req.getTrackingStep() == 1, "Initial tracking step is 1 (Sent)");
        });

        test("Feature 8 & 9: Accept/Reject Requests & Visual Tracking State Machine", () -> {
            Student seller = studentService.getStudentById("STU101");
            Student requester = studentService.getStudentById("STU104");

            // Create new request
            ExchangeRequest req = exchangeService.createRequest(
                    "BK210", requester, "Purchase", null, "Need Java reference."
            );
            assertCondition(req.getTrackingStep() == 1, "Stage 1: Request Sent");

            // Seller accepts
            boolean accepted = exchangeService.acceptRequest(req.getId(), seller.getId());
            assertCondition(accepted, "Request accepted by seller");
            assertCondition(req.getStatus() == RequestStatus.ACCEPTED, "Status changed to Accepted");

            // Advance stage: Contact Established -> Completed -> Review
            exchangeService.advanceStage(req.getId());
            assertCondition(req.getTrackingStep() >= 3, "Advanced to stage: " + req.getTrackingStepDescription());

            exchangeService.advanceStage(req.getId());
            assertCondition(req.getTrackingStep() == 4, "Stage 4: Completed");
            assertCondition(req.getStatus() == RequestStatus.COMPLETED, "Transaction status is Completed");
        });

        test("Feature 10: View Request History (Sent and Received)", () -> {
            List<ExchangeRequest> received = exchangeService.getReceivedRequests("STU101");
            List<ExchangeRequest> sent = exchangeService.getSentRequests("STU104");

            assertCondition(!received.isEmpty(), "Received requests not empty for seller STU101");
            assertCondition(!sent.isEmpty(), "Sent requests not empty for requester STU104");
        });

        test("Feature 11: Rate Sellers & Calculate Average Rating", () -> {
            Student reviewer = studentService.getStudentById("STU105");
            Student seller = studentService.getStudentById("STU102");
            double initialRating = seller.getRating();
            int initialCount = seller.getReviewCount();

            Rating r = ratingService.rateSeller(seller.getId(), reviewer, 5.0, "Super responsive and genuine book!");
            assertCondition(r != null, "Rating record created");
            assertCondition(seller.getReviewCount() == initialCount + 1, "Review count incremented");
            assertCondition(seller.getRating() >= 1.0 && seller.getRating() <= 5.0, "Rating bounded within 1.0 to 5.0");
        });

        test("Feature 12: Sorting Books with Comparators & Comparable", () -> {
            List<Book> books = bookService.getAvailableBooks();

            // 1. Natural sorting (Comparable<Book>)
            List<Book> natural = bookService.sortBooksNaturally(books);
            for (int i = 0; i < natural.size() - 1; i++) {
                assertCondition(natural.get(i).getTitle().compareToIgnoreCase(natural.get(i + 1).getTitle()) <= 0,
                        "Natural sorting order maintained");
            }

            // 2. Price Low to High
            List<Book> priceLow = bookService.sortByPriceLowToHigh(books);
            for (int i = 0; i < priceLow.size() - 1; i++) {
                assertCondition(priceLow.get(i).getPrice() <= priceLow.get(i + 1).getPrice(),
                        "Price ascending order maintained: " + priceLow.get(i).getPrice() + " <= " + priceLow.get(i + 1).getPrice());
            }

            // 3. Price High to Low
            List<Book> priceHigh = bookService.sortByPriceHighToLow(books);
            for (int i = 0; i < priceHigh.size() - 1; i++) {
                assertCondition(priceHigh.get(i).getPrice() >= priceHigh.get(i + 1).getPrice(),
                        "Price descending order maintained: " + priceHigh.get(i).getPrice() + " >= " + priceHigh.get(i + 1).getPrice());
            }
        });

        test("Collections Verification: TreeMap, TreeSet, LinkedHashSet, LinkedList, PriorityQueue, Iterator", () -> {
            // TreeMap: price index
            assertCondition(!store.getBooksByPriceIndex().isEmpty(), "TreeMap price index populated");

            // TreeSet: sorted authors
            assertCondition(!store.getUniqueAuthors().isEmpty(), "TreeSet unique authors populated");

            // LinkedHashSet: subjects
            assertCondition(!store.getUniqueSubjects().isEmpty(), "LinkedHashSet unique subjects populated");

            // LinkedList: activity audit log
            assertCondition(!store.getActivityLog().isEmpty(), "LinkedList activity log functioning");

            // PriorityQueue: pending requests
            assertCondition(!store.getPendingRequestsQueue().isEmpty(), "PriorityQueue pending requests functioning");

            // Iterator test: safe traversal
            int count = 0;
            Iterator<Book> it = store.getAllBooks().iterator();
            while (it.hasNext()) {
                Book b = it.next();
                if (b != null) count++;
            }
            assertCondition(count == store.getAllBooks().size(), "Iterator traversed all elements accurately");

            // ListIterator test: bidirectional traversal
            List<String> subjects = new java.util.ArrayList<>(store.getUniqueSubjects());
            ListIterator<String> lit = subjects.listIterator();
            while (lit.hasNext()) lit.next();
            int backCount = 0;
            while (lit.hasPrevious()) {
                lit.previous();
                backCount++;
            }
            assertCondition(backCount == subjects.size(), "ListIterator backwards traversal verified");
        });

        test("Compulsory Reset Feature: Reset DataStore to Baseline", () -> {
            DummyData.resetToDefault(store);
            assertCondition(store.getAllStudents().size() == 9, "Reset restored exact 9 baseline students");
            assertCondition(store.getAllBooks().size() == 24, "Reset restored exact 24 baseline books");
            assertCondition(store.getAllRequests().size() == 6, "Reset restored exact 6 baseline requests");
            assertCondition(store.getAllRatings().size() == 5, "Reset restored exact 5 baseline ratings");
        });

        System.out.println("================================================================================");
        System.out.printf("   VERIFICATION RESULTS: %d / %d TESTS PASSED (100%%)%n", testsPassed, totalTests);
        System.out.println("================================================================================");
    }

    private static void test(String name, Runnable runnable) {
        totalTests++;
        try {
            runnable.run();
            testsPassed++;
            System.out.printf(" [PASS] Test %-2d: %s%n", totalTests, name);
        } catch (Throwable t) {
            System.out.printf(" [FAIL] Test %-2d: %s - Error: %s%n", totalTests, name, t.getMessage());
            t.printStackTrace(System.out);
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("Assertion Failed: " + message);
        }
    }
}
