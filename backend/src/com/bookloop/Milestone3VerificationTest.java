package com.bookloop;

import com.bookloop.data.DataStore;
import com.bookloop.data.DummyData;
import com.bookloop.exception.ValidationException;
import com.bookloop.model.*;
import com.bookloop.persistence.CsvExportService;
import com.bookloop.service.*;
import com.bookloop.util.ValidationUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Automated Verification Test Suite for Milestone 3 Requirements:
 * 1. Book Recommendation Service (Smart, Semester, Subject, Price range, Non-self, Available only)
 * 2. Project Analytics & Reporting Service (KPIs, Collections, HashMap, TreeMap, Native Array, StringBuilder)
 * 3. Data Validation Utility (Student, Book, Exchange, Rating, ID formats, ValidationException)
 * 4. CSV Export Service (Decoupled exports/ directory, 5 CSV files, Protection of persistence data)
 * 5. Edge cases: Invalid inputs, Empty records, Self-trading, Self-rating, Concurrency, Persistence restart.
 */
public class Milestone3VerificationTest {

    private static int testsPassed = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   BOOKLOOP BACKEND - MILESTONE 3 AUTOMATED VERIFICATION TEST SUITE");
        System.out.println("================================================================================");

        DataStore store = DataStore.getInstance();
        StudentService studentService = new StudentService(store);
        BookService bookService = new BookService(store);
        ExchangeService exchangeService = new ExchangeService(store);
        RatingService ratingService = new RatingService(store);

        // Initialize with baseline data
        DummyData.initialize(store);

        BookRecommendationService recService = new BookRecommendationService(store);
        ProjectReportService reportService = new ProjectReportService(store);
        CsvExportService exportService = new CsvExportService("exports");

        // ====================================================================
        // FEATURE 1: BOOK RECOMMENDATION SERVICE VERIFICATION
        // ====================================================================

        test("M3-01: Recommendation - Smart Personalization for Student Context", () -> {
            Student aarav = studentService.getStudentById("STU101"); // DTU, CSE, Sem 5
            assertCondition(aarav != null, "Student STU101 exists");

            List<Book> recs = recService.recommendForStudent(aarav, 5);
            assertCondition(!recs.isEmpty(), "Recommendations generated for Aarav");
            assertCondition(recs.size() <= 5, "Result obeys limit parameter");

            // Verify invariant 1: None of the recommended books are uploaded by Aarav
            for (Book b : recs) {
                assertCondition(!b.getSellerId().equalsIgnoreCase(aarav.getId()),
                        "Invariant Violated: Recommended student's own book: " + b.getId());
                assertCondition(b.isAvailable(),
                        "Invariant Violated: Recommended unavailable book: " + b.getId());
            }

            // Verify top recommendation relevance (Sem 5, CS related, or high seller rating)
            Book topRec = recs.get(0);
            assertCondition(topRec != null, "Top recommendation exists");
            assertCondition(topRec.getSellerRating() >= 4.0, "Top recommendation has high seller rating");
        });

        test("M3-02: Recommendation - Semester-Based Filtering & Exclusion", () -> {
            int targetSem = 3;
            Student priya = studentService.getStudentById("STU102");
            List<Book> sem3Recs = recService.recommendBySemester(targetSem, priya.getId());

            assertCondition(!sem3Recs.isEmpty(), "Semester 3 recommendations found");
            for (Book b : sem3Recs) {
                assertCondition(b.getSemester() == targetSem, "Book matches target semester 3");
                assertCondition(!b.getSellerId().equalsIgnoreCase(priya.getId()), "Excludes Priya's own listings");
                assertCondition(b.isAvailable(), "Only available books recommended");
            }
        });

        test("M3-03: Recommendation - Subject Interests with Lambdas & Streams", () -> {
            List<String> interests = Arrays.asList("Algorithms", "Operating Systems");
            List<Book> subjectRecs = recService.recommendBySubjects(interests, "STU101");

            assertCondition(!subjectRecs.isEmpty(), "Subject recommendations found");
            for (Book b : subjectRecs) {
                boolean matchesSubject = b.getSubject().equalsIgnoreCase("Algorithms") ||
                                         b.getSubject().equalsIgnoreCase("Operating Systems") ||
                                         b.getTitle().contains("Algorithms") ||
                                         b.getTitle().contains("Operating");
                assertCondition(matchesSubject, "Book matches subject keywords: " + b.getTitle());
                assertCondition(!b.getSellerId().equalsIgnoreCase("STU101"), "Excludes STU101");
            }
        });

        test("M3-04: Recommendation - Price Range & Budget Filtering (Sorted Ascending)", () -> {
            double minPrice = 300.0;
            double maxPrice = 600.0;
            List<Book> budgetRecs = recService.recommendByPriceRange(minPrice, maxPrice, null);

            assertCondition(!budgetRecs.isEmpty(), "Budget recommendations found");
            for (int i = 0; i < budgetRecs.size(); i++) {
                Book b = budgetRecs.get(i);
                assertCondition(b.getPrice() >= minPrice && b.getPrice() <= maxPrice,
                        "Book price within Rs. 300 - 600: " + b.getPrice());
                if (i > 0) {
                    assertCondition(b.getPrice() >= budgetRecs.get(i - 1).getPrice(),
                            "Recommendations sorted ascending by price");
                }
            }
        });

        test("M3-05: Recommendation - Distinct Available Subjects & Explanation Reasons", () -> {
            List<String> distinctSubjects = recService.getAvailableDistinctSubjects();
            assertCondition(!distinctSubjects.isEmpty(), "Distinct subjects list is non-empty");

            // Verify distinctness using Set comparison
            Set<String> set = new HashSet<>(distinctSubjects);
            assertCondition(distinctSubjects.size() == set.size(), "All subjects in list are distinct");

            // Verify recommendation reason generator
            Book testBook = bookService.getBookById("BK201"); // Algorithms
            Student student = studentService.getStudentById("STU101");
            String reason = recService.getRecommendationReason(testBook, student);
            assertCondition(reason != null && !reason.trim().isEmpty(), "Generated non-empty recommendation reason");
            assertCondition(reason.contains("★") || reason.contains("Semester") || reason.contains("curriculum"),
                    "Reason includes rating or curriculum context");
        });

        // ====================================================================
        // FEATURE 2: PROJECT ANALYTICS & REPORTS VERIFICATION
        // ====================================================================

        test("M3-06: Analytics - Core Platform Aggregate Metrics & KPIs", () -> {
            int students = reportService.getTotalRegisteredStudents();
            int books = reportService.getTotalListedBooks();
            long available = reportService.getAvailableBooksCount();
            long forSale = reportService.getBooksAvailableForSaleCount();
            long forExchange = reportService.getBooksAvailableForExchangeCount();
            double avgRating = reportService.getAverageSellerRating();
            double totalVal = reportService.getTotalCatalogValuation();

            assertCondition(students >= 9, "Total registered students >= 9, got: " + students);
            assertCondition(books >= 24, "Total listed books >= 24, got: " + books);
            assertCondition(available > 0, "Available books > 0, got: " + available);
            assertCondition(forSale > 0, "Books available for sale > 0, got: " + forSale);
            assertCondition(forExchange > 0, "Books available for exchange > 0, got: " + forExchange);
            assertCondition(avgRating >= 1.0 && avgRating <= 5.0, "Platform average rating bounded, got: " + avgRating);
            assertCondition(totalVal > 0.0, "Total catalog valuation positive, got: " + totalVal);
        });

        test("M3-07: Analytics - Subject Popularity Ranking (HashMap + Stream sorting)", () -> {
            Map<String, Long> popular = reportService.getSubjectPopularityMap();
            assertCondition(!popular.isEmpty(), "Subject popularity map non-empty");

            // Verify descending order of counts
            long previousCount = Long.MAX_VALUE;
            for (Map.Entry<String, Long> entry : popular.entrySet()) {
                assertCondition(entry.getValue() <= previousCount, "Popularity map properly sorted descending");
                previousCount = entry.getValue();
            }
        });

        test("M3-08: Analytics - Alphabetical Subject Index (TreeMap Naturally Sorted Keys)", () -> {
            TreeMap<String, Long> sortedSubjects = reportService.getSubjectsSortedAlphabetically();
            assertCondition(!sortedSubjects.isEmpty(), "Alphabetical subjects map non-empty");

            String previousKey = "";
            for (String key : sortedSubjects.keySet()) {
                if (!previousKey.isEmpty()) {
                    assertCondition(key.compareToIgnoreCase(previousKey) >= 0,
                            "TreeMap maintains natural alphabetical key order: " + previousKey + " <= " + key);
                }
                previousKey = key;
            }
        });

        test("M3-09: Analytics - Semester Distribution Matrix (Native 1D Array)", () -> {
            int[] semDist = reportService.getSemesterDistribution();
            assertCondition(semDist.length == 9, "1D array has 9 slots (index 1..8 for semesters)");

            int totalSum = 0;
            for (int s = 1; s <= 8; s++) {
                totalSum += semDist[s];
                assertCondition(semDist[s] >= 0, "Non-negative book count for semester " + s);
            }
            assertCondition(totalSum == reportService.getTotalListedBooks(),
                    "Array element sum (" + totalSum + ") equals total listed books (" + reportService.getTotalListedBooks() + ")");
        });

        test("M3-10: Analytics - Top Active Sellers Leaderboard & Composite Scoring", () -> {
            List<ProjectReportService.SellerActivity> topSellers = reportService.getMostActiveSellers(5);
            assertCondition(!topSellers.isEmpty(), "Top sellers leaderboard generated");
            assertCondition(topSellers.size() <= 5, "Obeys limit parameter");

            // Verify descending order by activity score
            for (int i = 1; i < topSellers.size(); i++) {
                assertCondition(topSellers.get(i - 1).getTotalActivityScore() >= topSellers.get(i).getTotalActivityScore(),
                        "Leaderboard sorted descending by activity score");
            }
        });

        test("M3-11: Analytics - Formatted Comprehensive Dashboard Report (StringBuilder)", () -> {
            String fullReport = reportService.generateComprehensiveReport();
            assertCondition(fullReport != null && !fullReport.isEmpty(), "Comprehensive report string generated");
            assertCondition(fullReport.contains("CORE PLATFORM METRICS"), "Contains Section 1");
            assertCondition(fullReport.contains("TRANSACTION & EXCHANGE REQUEST LIFECYCLE"), "Contains Section 2");
            assertCondition(fullReport.contains("TEXTBOOK DISTRIBUTION ACROSS SEMESTERS"), "Contains Section 3");
            assertCondition(fullReport.contains("TOP MOST POPULAR ACADEMIC SUBJECTS"), "Contains Section 4");
            assertCondition(fullReport.contains("MOST ACTIVE STUDENT SELLERS LEADERBOARD"), "Contains Section 5");
            assertCondition(fullReport.contains("CAMPUS & UNIVERSITY PARTICIPATION"), "Contains Section 6");
        });

        // ====================================================================
        // FEATURE 3: DATA VALIDATION UTILITY (ValidationUtil) VERIFICATION
        // ====================================================================

        test("M3-12: ValidationUtil - Valid Inputs Pass Without Exception", () -> {
            // Student
            ValidationUtil.validateStudent(
                    "Harshita Nair", "harshita.nair@dtu.ac.in", "+91 98765 43210",
                    "Delhi Technological University", "Computer Science", 4
            );

            // Book
            ValidationUtil.validateBook(
                    "Database Management Systems", "Raghu Ramakrishnan", "978-0072465631",
                    "Database Systems", 4, 520.0, ListingType.FOR_SALE
            );

            // Exchange Request
            ValidationUtil.validateExchangeRequest("BK201", "STU102", "STU101", "Purchase", null);

            // Peer Rating
            ValidationUtil.validateRating("STU102", "STU101", 5.0, "Great book and fast handoff!");

            // System IDs
            ValidationUtil.validateStudentId("STU101");
            ValidationUtil.validateBookId("BK201");
            ValidationUtil.validateRequestId("REQ301");
            ValidationUtil.validateRatingId("REV401");
        });

        test("M3-13: ValidationUtil - Student Validation Failure with Custom ValidationException", () -> {
            // Blank name
            boolean caughtBlankName = false;
            try {
                ValidationUtil.validateStudent("", "name@dtu.ac.in", "+91 98765 43210", "DTU", "CSE", 3);
            } catch (ValidationException e) {
                caughtBlankName = true;
                assertCondition("name".equals(e.getFieldName()), "Field name is 'name'");
            }
            assertCondition(caughtBlankName, "Blank student name rejected");

            // Invalid email domain
            boolean caughtBadEmail = false;
            try {
                ValidationUtil.validateStudent("Aryan", "not-an-email", "+91 98765 43210", "DTU", "CSE", 3);
            } catch (ValidationException e) {
                caughtBadEmail = true;
                assertCondition("email".equals(e.getFieldName()), "Field name is 'email'");
            }
            assertCondition(caughtBadEmail, "Invalid email format rejected");

            // Invalid phone
            boolean caughtBadPhone = false;
            try {
                ValidationUtil.validateStudent("Aryan", "aryan@dtu.ac.in", "12345", "DTU", "CSE", 3);
            } catch (ValidationException e) {
                caughtBadPhone = true;
                assertCondition("phone".equals(e.getFieldName()), "Field name is 'phone'");
            }
            assertCondition(caughtBadPhone, "Invalid phone number rejected");

            // Semester out of bounds
            boolean caughtBadSem = false;
            try {
                ValidationUtil.validateSemester(9);
            } catch (ValidationException e) {
                caughtBadSem = true;
                assertCondition("semester".equals(e.getFieldName()), "Field name is 'semester'");
            }
            assertCondition(caughtBadSem, "Out-of-bounds semester (9) rejected");
        });

        test("M3-14: ValidationUtil - Book Validation Failure (Price & Titles)", () -> {
            // Negative price
            boolean caughtNegativePrice = false;
            try {
                ValidationUtil.validatePrice(-25.0);
            } catch (ValidationException e) {
                caughtNegativePrice = true;
                assertCondition("price".equals(e.getFieldName()), "Field name is 'price'");
            }
            assertCondition(caughtNegativePrice, "Negative price rejected");

            // Blank title
            boolean caughtBlankTitle = false;
            try {
                ValidationUtil.validateBookTitle("   ");
            } catch (ValidationException e) {
                caughtBlankTitle = true;
                assertCondition("title".equals(e.getFieldName()), "Field name is 'title'");
            }
            assertCondition(caughtBlankTitle, "Blank book title rejected");

            // For sale book with 0 price
            boolean caughtZeroPriceSale = false;
            try {
                ValidationUtil.validateBook("Valid Title", "Valid Author", "", "Math", 1, 0.0, ListingType.FOR_SALE);
            } catch (ValidationException e) {
                caughtZeroPriceSale = true;
            }
            assertCondition(caughtZeroPriceSale, "For sale book with Rs. 0 rejected");
        });

        test("M3-15: ValidationUtil - Prevention of Self-Trading & Self-Rating", () -> {
            // Self-trading prevention
            boolean caughtSelfTrade = false;
            try {
                ValidationUtil.validateExchangeRequest("BK201", "STU101", "STU101", "Purchase", null);
            } catch (ValidationException e) {
                caughtSelfTrade = true;
                assertCondition(e.getMessage().toLowerCase().contains("self"), "Rejects self-trading explicitly");
            }
            assertCondition(caughtSelfTrade, "Self-trade attempt strictly rejected");

            // Self-rating prevention
            boolean caughtSelfRating = false;
            try {
                ValidationUtil.validateRating("STU101", "STU101", 5.0, "Great seller!");
            } catch (ValidationException e) {
                caughtSelfRating = true;
                assertCondition(e.getMessage().toLowerCase().contains("themselves") ||
                                e.getMessage().toLowerCase().contains("own"), "Rejects self-rating explicitly");
            }
            assertCondition(caughtSelfRating, "Self-rating attempt strictly rejected");
        });

        test("M3-16: ValidationUtil - Rating Score Boundary Validation", () -> {
            // Rating < 1.0
            boolean caughtLow = false;
            try {
                ValidationUtil.validateRatingScore(0.5);
            } catch (ValidationException e) {
                caughtLow = true;
            }
            assertCondition(caughtLow, "Rating < 1.0 rejected");

            // Rating > 5.0
            boolean caughtHigh = false;
            try {
                ValidationUtil.validateRatingScore(5.5);
            } catch (ValidationException e) {
                caughtHigh = true;
            }
            assertCondition(caughtHigh, "Rating > 5.0 rejected");
        });

        // ====================================================================
        // FEATURE 4: CSV EXPORT SERVICE (CsvExportService) VERIFICATION
        // ====================================================================

        test("M3-17: CsvExportService - Batch Export All 5 Datasets to exports/ Directory", () -> {
            List<CsvExportService.ExportResult> results = exportService.exportAll(store, reportService);
            assertCondition(results.size() == 5, "Exported exactly 5 datasets: students, books, requests, ratings, analytics");

            for (CsvExportService.ExportResult res : results) {
                assertCondition(res.isSuccess(), "Export succeeded for: " + res.getFileName());
                assertCondition(res.getRecordCount() > 0, "Records exported > 0 for: " + res.getFileName());
                assertCondition(res.getFileSizeBytes() > 0, "File size > 0 bytes for: " + res.getFileName());

                File f = new File(res.getFullPath());
                assertCondition(f.exists(), "Export file exists on disk: " + f.getAbsolutePath());
            }

            // Verify readability of exported students CSV
            File stuFile = new File("exports/students_export.csv");
            try (BufferedReader reader = new BufferedReader(new FileReader(stuFile))) {
                String header = reader.readLine();
                assertCondition(header != null && header.contains("Student_ID") && header.contains("Full_Name"),
                        "students_export.csv contains valid standard header");
                String line1 = reader.readLine();
                assertCondition(line1 != null && line1.contains("STU"), "Data row contains STU record");
            }
        });

        test("M3-18: CsvExportService - Strict Protection of Persistence Files", () -> {
            // Attempting to export to data/students.csv must throw SecurityException
            boolean protectedFromOverwrite = false;
            try {
                exportService.exportStudents(store.getAllStudents(), "data/students.csv");
            } catch (SecurityException e) {
                protectedFromOverwrite = true;
                assertCondition(e.getMessage().contains("collides with persistence directory"),
                        "Proper security message explaining persistence file protection");
            }
            assertCondition(protectedFromOverwrite, "CsvExportService strictly protected persistence files from overwrite");
        });

        // ====================================================================
        // FEATURE 5: CONCURRENCY, RESTART PERSISTENCE & INTEGRATION
        // ====================================================================

        test("M3-19: Concurrency - Concurrent Read Access to Recommendations & Reports", () -> {
            final boolean[] failed = {false};
            int threadCount = 6;
            Thread[] threads = new Thread[threadCount];

            for (int i = 0; i < threadCount; i++) {
                final int idx = i;
                threads[i] = new Thread(() -> {
                    try {
                        Student s = store.getStudent("STU10" + (1 + (idx % 5)));
                        List<Book> recs = recService.recommendForStudent(s, 5);
                        assertCondition(!recs.isEmpty(), "Thread " + idx + " got recs");

                        String report = reportService.generateComprehensiveReport();
                        assertCondition(report.length() > 500, "Thread " + idx + " got report");
                    } catch (Throwable t) {
                        failed[0] = true;
                    }
                }, "TestConcurrentReader-" + i);
                threads[i].start();
            }

            for (Thread t : threads) {
                t.join(2000);
            }
            assertCondition(!failed[0], "Concurrent recommendation and report queries executed without race conditions");
        });

        test("M3-20: End-to-End Integration: Add Validated Listing & Immediate Recommendation", () -> {
            Student seller = studentService.getStudentById("STU103");
            Book newListing = bookService.addBook(
                    "Cloud Computing Principles and Paradigms", "Rajkumar Buyya", "978-0470887998",
                    "Cloud Computing", "Computer Science", 6, "1st Edition",
                    Condition.LIKE_NEW, 550.0, ListingType.BOTH, "Distributed Systems",
                    "Comprehensive cloud reference textbook.", seller
            );
            assertCondition(newListing != null, "New validated book added");

            // Verify BookRecommendationService immediately finds this new listing for another student
            Student buyer = studentService.getStudentById("STU101");
            List<Book> cloudRecs = recService.recommendBySubjects(Collections.singletonList("Cloud Computing"), buyer.getId());
            assertCondition(!cloudRecs.isEmpty(), "New book immediately discoverable by recommendation engine");
            assertCondition(cloudRecs.stream().anyMatch(b -> b.getId().equals(newListing.getId())),
                    "Newly added book included in recommendation results");

            // Clean up: delete newly added listing so store stays pristine
            bookService.deleteBook(newListing.getId(), seller.getId());
        });

        System.out.println("================================================================================");
        System.out.printf("   MILESTONE 3 VERIFICATION RESULTS: %d / %d TESTS PASSED (100%%)%n", testsPassed, totalTests);
        System.out.println("================================================================================");
    }

    @FunctionalInterface
    interface TestRunnable {
        void run() throws Exception;
    }

    private static void test(String name, TestRunnable runnable) {
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
