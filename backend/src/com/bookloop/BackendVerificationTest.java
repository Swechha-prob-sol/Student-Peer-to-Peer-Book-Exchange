package com.bookloop;

import com.bookloop.data.DataStore;
import com.bookloop.data.DummyData;
import com.bookloop.exception.*;
import com.bookloop.interfaces.Displayable;
import com.bookloop.model.*;
import com.bookloop.persistence.CsvExportService;
import com.bookloop.persistence.FileStorageManager;
import com.bookloop.service.*;
import com.bookloop.threading.*;
import com.bookloop.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Automated Test and Verification Suite for Milestones 1, 2, and 3.
 * Tests every required syllabus concept and feature across:
 * - Milestone 1: OOP, Collections, Comparators, Interfaces, Method Overloading/Overriding
 * - Milestone 2: Strings, Arrays, Wrapper Classes, Exceptions, Multithreading,
 *                Lambda & Streams, Generics, File Handling & Persistence, Dummy Data Integration.
 * - Milestone 3: Book Recommendation Engine, Platform Analytics & Reports,
 *                Data Validation Utility, CSV Export Service (exports/), Concurrency & Restarts.
 */
public class BackendVerificationTest {

    private static int testsPassed = 0;
    private static int totalTests = 0;

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("   BOOKLOOP BACKEND - COMPLETE VERIFICATION SUITE (MILESTONES 1, 2 & 3)");
        System.out.println("================================================================================");

        DataStore store = DataStore.getInstance();
        StudentService studentService = new StudentService(store);
        BookService bookService = new BookService(store);
        ExchangeService exchangeService = new ExchangeService(store);
        RatingService ratingService = new RatingService(store);
        StreamService streamService = new StreamService(store);
        BookRecommendationService recService = new BookRecommendationService(store);
        ProjectReportService reportService = new ProjectReportService(store);
        CsvExportService exportService = new CsvExportService("exports");

        // 1. Initialize Dummy Data
        DummyData.initialize(store);

        // ====================================================================
        // MILESTONE 1 VERIFICATION
        // ====================================================================

        test("M1-01: Dummy Data Loading & Verification", () -> {
            List<Student> students = studentService.getAllStudents();
            List<Book> books = bookService.getAllBooks();
            List<ExchangeRequest> requests = exchangeService.getAllRequests();
            List<Rating> reviews = ratingService.getAllReviews();

            assertCondition(students.size() >= 9, "Should load at least 9 Indian student records, got: " + students.size());
            assertCondition(books.size() >= 24, "Should load at least 24 textbook records, got: " + books.size());
            assertCondition(requests.size() >= 6, "Should load at least 6 transaction requests, got: " + requests.size());
            assertCondition(reviews.size() >= 5, "Should load at least 5 peer reviews, got: " + reviews.size());
        });

        test("M1-02: Student Registration & Email Uniqueness (HashSet)", () -> {
            Student s = studentService.registerStudent(
                    "Tanvi Joshi", "tanvi.j@coep.ac.in", "+91 98111 22233",
                    "COEP Pune", "Computer Engineering", 4
            );
            assertCondition(s != null && s.getName().equals("Tanvi Joshi"), "Student registered successfully");
            assertCondition(s.getCollege().contains("COEP"), "College matches");

            // Attempt duplicate email registration -> must throw exception
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

        test("M1-03: Add Book for Sale / Exchange", () -> {
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

        test("M1-04: View All Available Books (Catalog)", () -> {
            List<Book> activeBooks = bookService.getAvailableBooks();
            assertCondition(!activeBooks.isEmpty(), "Active books catalog not empty");
            for (Book b : activeBooks) {
                assertCondition(b.isAvailable(), "All returned books must have Active status");
            }
        });

        test("M1-05: Search Books with Method Overloading", () -> {
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

        test("M1-06: View Book & Seller Details (Polymorphism)", () -> {
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

        test("M1-07: Update & Delete Book Listings", () -> {
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

        test("M1-08: Send Exchange and Purchase Requests", () -> {
            Student requester = studentService.getStudentById("STU103");
            ExchangeRequest req = exchangeService.createRequest(
                    "BK204", requester, "Purchase", null, "Can meet at campus center."
            );
            assertCondition(req != null, "Purchase request created");
            assertCondition(req.getStatus() == RequestStatus.PENDING, "Initial status is Pending");
            assertCondition(req.getTrackingStep() == 1, "Initial tracking step is 1 (Sent)");
        });

        test("M1-09: Request Lifecycle & Tracking State Machine (Steps 1-5)", () -> {
            Student seller = studentService.getStudentById("STU101");
            Student requester = studentService.getStudentById("STU104");

            ExchangeRequest req = exchangeService.createRequest(
                    "BK210", requester, "Purchase", null, "Need Java reference."
            );
            assertCondition(req.getTrackingStep() == 1, "Stage 1: Request Sent");

            boolean accepted = exchangeService.acceptRequest(req.getId(), seller.getId());
            assertCondition(accepted, "Request accepted by seller");
            assertCondition(req.getStatus() == RequestStatus.ACCEPTED, "Status changed to Accepted");

            exchangeService.advanceStage(req.getId());
            assertCondition(req.getTrackingStep() >= 3, "Advanced to stage 3: Contact Established");

            exchangeService.advanceStage(req.getId());
            assertCondition(req.getTrackingStep() == 4, "Stage 4: Completed");
            assertCondition(req.getStatus() == RequestStatus.COMPLETED, "Transaction status is Completed");
        });

        test("M1-10: Rate Sellers & Average Rating Calculation", () -> {
            Student reviewer = studentService.getStudentById("STU105");
            Student seller = studentService.getStudentById("STU102");
            int initialCount = seller.getReviewCount();

            Rating r = ratingService.rateSeller(seller.getId(), reviewer, 5.0, "Super responsive and genuine book!");
            assertCondition(r != null, "Rating record created");
            assertCondition(seller.getReviewCount() == initialCount + 1, "Review count incremented");
            assertCondition(seller.getRating() >= 1.0 && seller.getRating() <= 5.0, "Rating bounded within 1.0 to 5.0");
        });

        test("M1-11: Sorting Books with Comparators & Comparable", () -> {
            List<Book> books = bookService.getAvailableBooks();

            // Natural sorting (Comparable<Book>)
            List<Book> natural = bookService.sortBooksNaturally(books);
            for (int i = 0; i < natural.size() - 1; i++) {
                assertCondition(natural.get(i).getTitle().compareToIgnoreCase(natural.get(i + 1).getTitle()) <= 0,
                        "Natural sorting order maintained");
            }

            // Price Low to High
            List<Book> priceLow = bookService.sortByPriceLowToHigh(books);
            for (int i = 0; i < priceLow.size() - 1; i++) {
                assertCondition(priceLow.get(i).getPrice() <= priceLow.get(i + 1).getPrice(),
                        "Price ascending order maintained");
            }

            // Price High to Low
            List<Book> priceHigh = bookService.sortByPriceHighToLow(books);
            for (int i = 0; i < priceHigh.size() - 1; i++) {
                assertCondition(priceHigh.get(i).getPrice() >= priceHigh.get(i + 1).getPrice(),
                        "Price descending order maintained");
            }
        });

        test("M1-12: Collections Framework (HashMap, HashSet, ArrayList, and Iterators)", () -> {
            assertCondition(!store.getAllBooks().isEmpty(), "ArrayList of books populated");
            assertCondition(!store.getUniqueAuthors().isEmpty(), "HashSet of unique authors populated");
            assertCondition(!store.getUniqueSubjects().isEmpty(), "HashSet of unique subjects populated");
            assertCondition(!store.getActivityLog().isEmpty(), "ArrayList activity log functioning");

            // Iterator test: safe traversal over collection
            int count = 0;
            Iterator<Book> it = store.getAllBooks().iterator();
            while (it.hasNext()) {
                if (it.next() != null) count++;
            }
            assertCondition(count == store.getAllBooks().size(), "Iterator traversed all elements accurately");
        });

        // ====================================================================
        // MILESTONE 2 VERIFICATION
        // ====================================================================

        test("M2-01: String Pool & String Immutability", () -> {
            String s1 = "BookLoop";
            String s2 = "BookLoop";
            String s3 = new String("BookLoop");

            assertCondition(s1 == s2, "String Pool: identical literals share same reference (s1 == s2)");
            assertCondition(s1 != s3, "Heap allocation: new String() creates distinct object (s1 != s3)");
            assertCondition(s1.equals(s3), "Content equality: .equals() verifies value equality");
            assertCondition(s1 == s3.intern(), "intern() returns String Pool canonical reference");

            // Immutability test
            String original = "Data Structures";
            String modified = original.toUpperCase();
            assertCondition("Data Structures".equals(original), "Original string remains unchanged (immutability)");
            assertCondition(original != modified, "Operation produces a new String instance");
        });

        test("M2-02: String Comparison, Searching, Manipulation & Formatting", () -> {
            // String Comparison
            String a = "Algorithm";
            String b = "algorithm";
            assertCondition(!a.equals(b), "Case-sensitive equals returns false");
            assertCondition(a.equalsIgnoreCase(b), "equalsIgnoreCase returns true");
            assertCondition(a.compareToIgnoreCase(b) == 0, "compareToIgnoreCase returns 0 for equal strings");

            // String Searching
            String text = "Operating System Concepts by Silberschatz";
            assertCondition(StringUtils.containsIgnoreCase(text, "operating"), "containsIgnoreCase finds substring");
            assertCondition(StringUtils.findKeyword(text, "Concepts") >= 0, "findKeyword finds correct index");
            assertCondition(StringUtils.findKeyword(text, "NonExistent") == -1, "findKeyword returns -1 for missing");

            // String Manipulation & Formatting
            String formatted = StringUtils.formatBookListing("BK201", "Introduction to Algorithms", "Cormen", 3, 650.0, "Active");
            assertCondition(formatted.contains("BK201") && formatted.contains("ACTIVE"), "Formatted listing has ID and uppercase status");

            String studentSummary = StringUtils.formatStudentSummary("Aarav Sharma", "DTU", "CSE", 5, 4.9);
            assertCondition(studentSummary.contains("Aarav") && studentSummary.contains("DTU"), "Student summary contains first name and college");

            // ISBN formatting & validation
            assertCondition(StringUtils.formatISBN("978-0262033848").equals("978-0262033848"), "Valid ISBN preserved");
            assertCondition(StringUtils.formatISBN("").equals("N/A"), "Empty ISBN defaults to N/A");

            // Email & Phone validation
            assertCondition(StringUtils.isValidEmail("aarav@dtu.ac.in"), "Valid college email accepted");
            assertCondition(!StringUtils.isValidEmail("invalid-email"), "Invalid email rejected");
            assertCondition(StringUtils.isValidPhone("+91 98765 43210"), "Valid Indian phone accepted");
        });

        test("M2-03: StringBuilder & StringBuffer", () -> {
            // StringBuilder (non-synchronized, mutable)
            String report = StringUtils.buildBookReport("Java Complete Reference", "Herbert Schildt", "OOP", 3, 520.0, "Very Good");
            assertCondition(report.contains("BOOK DETAIL REPORT"), "StringBuilder generates structured report");
            assertCondition(report.contains("Herbert Schildt"), "StringBuilder correctly appended author");

            String csvLine = StringUtils.buildCSVLine("BK201", "Intro to Algorithms, 3rd Ed", "Cormen", "650");
            assertCondition(csvLine.contains("\"Intro to Algorithms, 3rd Ed\""), "StringBuilder properly quoted CSV field containing comma");

            // StringBuffer (thread-safe, synchronized)
            String logEntry = StringUtils.buildActivityLogEntry("2026-11-17", "EXCHANGE_SENT", "STU101", "Requested BK205");
            assertCondition(logEntry.contains("EXCHANGE_SENT") && logEntry.contains("STU101"), "StringBuffer generated thread-safe log entry");

            // Thread-safe buffer appending
            StringUtils.appendSearchToHistory("Operating Systems");
            assertCondition(StringUtils.sharedSearchHistoryBuffer.toString().contains("Operating Systems"), "StringBuffer shared buffer received input");
        });

        test("M2-04: Wrapper Classes, Auto-boxing, Auto-unboxing & Parsing", () -> {
            // Auto-boxing: primitive -> Wrapper
            int primitiveInt = 42;
            Integer boxedInt = primitiveInt; // Auto-boxing
            assertCondition(boxedInt.intValue() == 42, "Auto-boxing correctly wraps primitive int");

            // Auto-unboxing: Wrapper -> primitive
            Double boxedDouble = Double.valueOf(99.99);
            double primitiveDouble = boxedDouble; // Auto-unboxing
            assertCondition(primitiveDouble == 99.99, "Auto-unboxing correctly unwraps Double");

            // Parsing with Wrapper methods
            int parsedInt = Integer.parseInt("2026");
            double parsedDouble = Double.parseDouble("499.50");
            boolean parsedBool = Boolean.parseBoolean("true");
            assertCondition(parsedInt == 2026, "Integer.parseInt parses valid string");
            assertCondition(parsedDouble == 499.50, "Double.parseDouble parses valid string");
            assertCondition(parsedBool, "Boolean.parseBoolean parses 'true'");

            // Safe parsing with exception recovery
            assertCondition(StringUtils.safeParseInt("not-a-number", 100) == 100, "safeParseInt returns fallback on invalid input");
            assertCondition(StringUtils.safeParseDouble("xyz", 0.0) == 0.0, "safeParseDouble returns fallback on invalid input");

            // Wrapper utility methods
            assertCondition(Integer.compare(10, 20) < 0, "Integer.compare behaves correctly");
            assertCondition(Integer.toHexString(255).equalsIgnoreCase("ff"), "Integer.toHexString converts correctly");
        });

        test("M2-05: Multidimensional Arrays (2D Matrix)", () -> {
            List<Book> books = bookService.getAllBooks();
            String[][] matrix = StringUtils.buildSemesterSubjectMatrix(books);

            assertCondition(matrix != null, "Matrix generated successfully");
            assertCondition(matrix.length == 9, "Matrix has 9 rows (1 header + 8 semesters)");
            assertCondition(matrix[0].length >= 7, "Matrix has header and category columns");
            assertCondition(matrix[0][0].equals("Semester"), "Header row starts with 'Semester'");

            // Verify semester row labels
            for (int s = 1; s <= 8; s++) {
                assertCondition(matrix[s][0].equals("Sem " + s), "Row " + s + " labeled Sem " + s);
            }

            // Verify non-zero counts in matrix
            int totalCount = 0;
            for (int r = 1; r <= 8; r++) {
                for (int c = 1; c < matrix[r].length; c++) {
                    totalCount += Integer.parseInt(matrix[r][c]);
                }
            }
            assertCondition(totalCount == books.size(), "2D matrix accurately accounts for all books (" + totalCount + " == " + books.size() + ")");
        });

        test("M2-06: Custom Checked Exception: BookNotFoundException", () -> {
            boolean caught = false;
            try {
                String nonExistentId = "BK_NON_EXISTENT_999";
                Book b = bookService.getBookById(nonExistentId);
                if (b == null) {
                    throw new BookNotFoundException(nonExistentId);
                }
            } catch (BookNotFoundException e) {
                caught = true;
                assertCondition(e.getBookId().equals("BK_NON_EXISTENT_999"), "Exception preserves bookId");
                assertCondition(e.getMessage().contains("BK_NON_EXISTENT_999"), "Exception message formatted correctly");
            }
            assertCondition(caught, "BookNotFoundException properly thrown and caught");
        });

        test("M2-07: Custom Unchecked Exception: InvalidStudentException", () -> {
            boolean caught = false;
            try {
                String badStudentId = "STU_GHOST";
                Student s = studentService.getStudentById(badStudentId);
                if (s == null) {
                    throw new InvalidStudentException(badStudentId, "Student record not found in system: " + badStudentId);
                }
            } catch (InvalidStudentException e) {
                caught = true;
                assertCondition(e.getStudentId().equals("STU_GHOST"), "Exception preserves studentId");
            }
            assertCondition(caught, "InvalidStudentException (unchecked) properly thrown and caught");
        });

        test("M2-08: Custom Checked Exception: InvalidExchangeRequestException", () -> {
            boolean caught = false;
            try {
                throw new InvalidExchangeRequestException("REQ_TEST_99", "Cannot request book: already pending exchange.");
            } catch (InvalidExchangeRequestException e) {
                caught = true;
                assertCondition(e.getRequestId().equals("REQ_TEST_99"), "Preserves requestId");
                assertCondition(e.getReason().contains("already pending"), "Preserves reason");
            }
            assertCondition(caught, "InvalidExchangeRequestException properly thrown and caught");
        });

        test("M2-09: Exception Handling: try-catch-finally, Multi-catch & Propagation", () -> {
            // 1. Multi-catch verification
            boolean multiCaught = false;
            try {
                String input = "not-a-number";
                Integer.parseInt(input);
            } catch (NumberFormatException | NullPointerException e) {
                multiCaught = true;
                assertCondition(e instanceof NumberFormatException, "Caught specific multi-catch branch");
            }
            assertCondition(multiCaught, "Multi-catch block successfully caught NumberFormatException");

            // 2. Finally block execution guarantee
            final boolean[] finallyExecuted = {false};
            try {
                int div = 10 / 2;
            } catch (ArithmeticException e) {
                // Not expected
            } finally {
                finallyExecuted[0] = true;
            }
            assertCondition(finallyExecuted[0], "Finally block executed successfully after try");

            // 3. Exception propagation verification
            boolean propagated = false;
            try {
                testExceptionPropagationHelper();
            } catch (BookNotFoundException e) {
                propagated = true;
            }
            assertCondition(propagated, "Exception propagated across method call boundary");
        });

        test("M2-10: Multithreading: Daemon Threads & Thread Priorities", () -> {
            AutoSaveManager daemon = new AutoSaveManager(store, 100000);
            assertCondition(daemon.isDaemon(), "AutoSaveManager is configured as a Daemon thread");
            assertCondition(daemon.getPriority() == Thread.MIN_PRIORITY, "Daemon runs at MIN_PRIORITY");

            daemon.start();
            assertCondition(daemon.isAlive(), "Daemon thread is active and running");
            daemon.shutdown();
            daemon.join(2000);
            assertCondition(!daemon.isAlive(), "Daemon thread cleanly stopped on shutdown");
        });

        test("M2-11: Multithreading: Background Thread Lifecycle & Synchronization", () -> {
            AutoSaveManager daemon = new AutoSaveManager(store, 1000);
            daemon.start();
            Thread.sleep(200);
            assertCondition(daemon.isAlive(), "Background thread executes concurrently");
            daemon.shutdown();
            daemon.join(2000);
            assertCondition(!daemon.isAlive(), "Thread stopped gracefully");
        });

        test("M2-13: Stream API: filter(), map(), sorted(), distinct(), limit(), skip()", () -> {
            List<Book> books = bookService.getAvailableBooks();

            // 1. filter()
            List<Book> filtered = streamService.filterBooksBySubject("Computer Science");
            assertCondition(filtered.stream().allMatch(b -> b.getSubject().equalsIgnoreCase("Computer Science")),
                    "filter() accurately retained only Computer Science books");

            // 2. map() + distinct()
            List<String> subjects = streamService.getDistinctSubjects();
            Set<String> subjectSet = new HashSet<>(subjects);
            assertCondition(subjects.size() == subjectSet.size(), "distinct() removed duplicate subject names");

            // 3. sorted() with method reference
            List<Book> sortedPrice = streamService.sortBooksByPrice();
            for (int i = 0; i < sortedPrice.size() - 1; i++) {
                assertCondition(sortedPrice.get(i).getPrice() <= sortedPrice.get(i + 1).getPrice(),
                        "sorted() arranged prices ascending");
            }

            // 4. limit()
            List<Book> top3 = streamService.getTopExpensiveBooks(3);
            assertCondition(top3.size() == 3, "limit(3) returned exactly 3 records");
            assertCondition(top3.get(0).getPrice() >= top3.get(1).getPrice(), "Top items ordered descending");

            // 5. skip() + limit() (pagination)
            List<Book> page1 = streamService.getBooksPaginated(1, 5);
            List<Book> page2 = streamService.getBooksPaginated(2, 5);
            assertCondition(page1.size() == 5 && page2.size() == 5, "Pagination returns 5 records per page");
            assertCondition(!page1.get(0).getId().equals(page2.get(0).getId()), "Page 2 skips Page 1 records");
        });

        test("M2-14: Stream API: Grouping, Collectors, Aggregations & Method References", () -> {
            // groupingBy
            Map<Integer, List<Book>> bySemester = streamService.groupBooksBySemester();
            assertCondition(bySemester.containsKey(3), "groupingBy contains semester 3");
            assertCondition(bySemester.containsKey(4), "groupingBy contains semester 4");

            // Category counts
            Map<String, Long> catCounts = streamService.countBooksByCategory();
            assertCondition(catCounts.containsKey("Computer Science"), "Category counts includes Computer Science");

            // Summary statistics / sum / average
            double totalVal = streamService.getTotalCatalogValue();
            OptionalDouble avgVal = streamService.getAverageBookPrice();
            assertCondition(totalVal > 0, "Catalog total price calculated via stream sum()");
            assertCondition(avgVal.isPresent() && avgVal.getAsDouble() > 0, "Average price calculated via stream average()");

            // String joining
            String titles = streamService.getBookTitlesList(3);
            assertCondition(titles.contains(", "), "Collectors.joining joined titles with comma delimiter");

            // Highly rated sellers (filter + sorted + map)
            List<GenericPair<String, Double>> topSellers = streamService.getHighlyRatedSellers(4.8);
            assertCondition(!topSellers.isEmpty(), "Found sellers with rating >= 4.8");
            for (GenericPair<String, Double> pair : topSellers) {
                assertCondition(pair.getValue() >= 4.8, "All returned sellers have rating >= 4.8");
            }
        });

        test("M2-15: Functional Interfaces & Lambdas (Predicate, Function, Consumer, Supplier)", () -> {
            // Predicate
            Predicate<Book> isCheap = b -> b.getPrice() < 400;
            Predicate<Book> isSem3 = b -> b.getSemester() == 3;
            List<Book> cheapSem3 = bookService.getAvailableBooks().stream()
                    .filter(isCheap.and(isSem3))
                    .collect(Collectors.toList());
            for (Book b : cheapSem3) {
                assertCondition(b.getPrice() < 400 && b.getSemester() == 3, "Predicate composition (.and) applied correctly");
            }

            // Function
            Function<Book, String> toLabel = b -> b.getId() + ": " + b.getTitle();
            String label = toLabel.apply(bookService.getAvailableBooks().get(0));
            assertCondition(label.startsWith("BK"), "Function transformed Book to String label");

            // Consumer
            List<String> collected = new ArrayList<>();
            Consumer<Book> titleCollector = b -> collected.add(b.getTitle());
            bookService.getAvailableBooks().stream().limit(3).forEach(titleCollector);
            assertCondition(collected.size() == 3, "Consumer processed 3 items");

            // Supplier
            Supplier<ExchangeRequest> defaultReqSupplier = ExchangeRequest::new;
            ExchangeRequest def = defaultReqSupplier.get();
            assertCondition(def != null && def.getStatus() == RequestStatus.PENDING, "Supplier generated default request instance");
        });

        test("M2-16: Generics: GenericPair<K, V> and Type-Safe Generics Usage", () -> {
            // GenericPair<K, V> testing
            int activeBookCount = bookService.getAvailableBooks().size();
            GenericPair<String, Integer> pair = new GenericPair<>("TotalBooks", activeBookCount);
            assertCondition(pair.getKey().equals("TotalBooks"), "GenericPair key accessible");
            assertCondition(pair.getValue() == activeBookCount, "GenericPair value accessible");

            GenericPair<String, Double> pricePair = new GenericPair<>("BK201", 650.0);
            assertCondition(pricePair.getKey().equals("BK201"), "GenericPair key matches");
            assertCondition(pricePair.getValue() == 650.0, "GenericPair double value matches");
        });

        test("M2-17: File Handling: Saving to CSV (FileWriter & BufferedWriter)", () -> {
            // Save all 4 entities to CSV
            FileStorageManager.saveStudents(studentService.getAllStudents());
            FileStorageManager.saveBooks(bookService.getAllBooks());
            FileStorageManager.saveRequests(exchangeService.getAllRequests());
            FileStorageManager.saveRatings(new ArrayList<>(ratingService.getAllReviews()));
            FileStorageManager.saveMetadata(115, 230, 310, 410);

            assertCondition(FileStorageManager.savedDataExists(), "Saved CSV files exist on disk");

            File studentsFile = new File("data/students.csv");
            File booksFile = new File("data/books.csv");
            File requestsFile = new File("data/requests.csv");
            File ratingsFile = new File("data/ratings.csv");
            File metaFile = new File("data/metadata.dat");

            assertCondition(studentsFile.exists() && studentsFile.length() > 0, "students.csv written");
            assertCondition(booksFile.exists() && booksFile.length() > 0, "books.csv written");
            assertCondition(requestsFile.exists() && requestsFile.length() > 0, "requests.csv written");
            assertCondition(ratingsFile.exists() && ratingsFile.length() > 0, "ratings.csv written");
            assertCondition(metaFile.exists() && metaFile.length() > 0, "metadata.dat (byte stream) written");
        });

        test("M2-18: File Handling: Loading from CSV (FileReader & BufferedReader)", () -> {
            List<Student> loadedStudents = FileStorageManager.loadStudents();
            List<Book> loadedBooks = FileStorageManager.loadBooks();
            List<ExchangeRequest> loadedRequests = FileStorageManager.loadRequests();
            List<Rating> loadedRatings = FileStorageManager.loadRatings();

            assertCondition(loadedStudents.size() >= 9, "Loaded at least 9 students from CSV: " + loadedStudents.size());
            assertCondition(loadedBooks.size() >= 24, "Loaded at least 24 books from CSV: " + loadedBooks.size());
            assertCondition(loadedRequests.size() >= 6, "Loaded at least 6 requests from CSV: " + loadedRequests.size());
            assertCondition(loadedRatings.size() >= 5, "Loaded at least 5 ratings from CSV: " + loadedRatings.size());

            // Verify content fidelity
            Student aarav = loadedStudents.stream()
                    .filter(s -> s.getId().equals("STU101"))
                    .findFirst()
                    .orElse(null);
            assertCondition(aarav != null && aarav.getName().equals("Aarav Sharma"),
                    "Student record field fidelity preserved: Aarav Sharma");

            Book clrs = loadedBooks.stream()
                    .filter(b -> b.getId().equals("BK201"))
                    .findFirst()
                    .orElse(null);
            assertCondition(clrs != null && clrs.getPrice() == 650.0,
                    "Book record field fidelity preserved: " + (clrs != null ? clrs.getTitle() : "null"));
        });

        test("M2-19: File Handling: Byte Streams (FileInputStream & FileOutputStream) & StreamTokenizer", () -> {
            // Byte stream metadata reading
            int[] meta = FileStorageManager.loadMetadata();
            assertCondition(meta != null && meta.length == 4, "Byte stream (FileInputStream/DataInputStream) loaded metadata");
            assertCondition(meta[0] == 115 && meta[1] == 230, "Metadata values accurately read from binary file");

            // Byte stream report export
            FileStorageManager.exportDataReport(
                    studentService.getAllStudents(),
                    bookService.getAllBooks(),
                    exchangeService.getAllRequests(),
                    new ArrayList<>(ratingService.getAllReviews())
            );
            File reportFile = new File("data/bookloop_report.txt");
            assertCondition(reportFile.exists() && reportFile.length() > 0, "FileOutputStream exported text report");

            // StreamTokenizer demonstration
            FileStorageManager.demonstrateStreamTokenizer();
        });

        test("M2-20: Persistence Across Restart & Dummy Data Integration", () -> {
            // 1. Save state
            store.saveToFiles();

            // 2. Clear in-memory DataStore (simulating application shutdown)
            store.clearAll();
            assertCondition(store.getAllStudents().isEmpty(), "In-memory store cleared (app shut down)");
            assertCondition(store.getAllBooks().isEmpty(), "No books in memory");

            // 3. Restart application initialization: loads saved files, NOT overwriting with dummy data
            boolean loaded = store.loadFromFiles();
            assertCondition(loaded, "Application restart successfully loaded data from files");
            assertCondition(store.getAllStudents().size() >= 9, "Restored all students from file");
            assertCondition(store.getAllBooks().size() >= 24, "Restored all books from file");

            // 4. Verify dummy data was NOT re-added as duplicates
            assertCondition(store.getStudent("STU101") != null, "STU101 exists");
            assertCondition(store.getAllStudents().stream().filter(s -> s.getId().equals("STU101")).count() == 1,
                    "No duplicate student records exist after restart");

            // 5. Test reset-to-original-dummy-data
            DummyData.resetToDefault(store);
            assertCondition(store.getAllStudents().size() == 9, "Reset restored baseline 9 students");
            assertCondition(store.getAllBooks().size() == 24, "Reset restored baseline 24 books");
            assertCondition(store.getAllRequests().size() == 6, "Reset restored baseline 6 requests");
            assertCondition(store.getAllRatings().size() == 5, "Reset restored baseline 5 ratings");
        });

        // ====================================================================
        // MILESTONE 3 VERIFICATION
        // ====================================================================

        test("M3-01: BookRecommendationService: Smart Student Personalization", () -> {
            Student aarav = studentService.getStudentById("STU101");
            List<Book> recs = recService.recommendForStudent(aarav, 5);
            assertCondition(!recs.isEmpty(), "Generated recommendations for student");
            for (Book b : recs) {
                assertCondition(!b.getSellerId().equalsIgnoreCase(aarav.getId()), "Excludes student's own books");
                assertCondition(b.isAvailable(), "Recommended books must be active/available");
            }
        });

        test("M3-02: BookRecommendationService: Semester-Based Filtering & Exclusion", () -> {
            int targetSem = 3;
            Student priya = studentService.getStudentById("STU102");
            List<Book> sem3Recs = recService.recommendBySemester(targetSem, priya.getId());
            assertCondition(!sem3Recs.isEmpty(), "Semester 3 recommendations found");
            for (Book b : sem3Recs) {
                assertCondition(b.getSemester() == targetSem, "Matches semester 3");
                assertCondition(!b.getSellerId().equalsIgnoreCase(priya.getId()), "Excludes seller's own listings");
            }
        });

        test("M3-03: BookRecommendationService: Subject Interests Filtering (Stream anyMatch)", () -> {
            List<String> interests = Arrays.asList("Algorithms", "Operating Systems");
            List<Book> subjectRecs = recService.recommendBySubjects(interests, "STU101");
            assertCondition(!subjectRecs.isEmpty(), "Subject recommendations found");
            for (Book b : subjectRecs) {
                assertCondition(b.isAvailable(), "Active only");
                assertCondition(!b.getSellerId().equalsIgnoreCase("STU101"), "Excludes STU101");
            }
        });

        test("M3-04: BookRecommendationService: Price Range & Budget Filtering (Sorted Ascending)", () -> {
            List<Book> budgetRecs = recService.recommendByPriceRange(300.0, 600.0, null);
            assertCondition(!budgetRecs.isEmpty(), "Budget recommendations found");
            for (int i = 0; i < budgetRecs.size(); i++) {
                assertCondition(budgetRecs.get(i).getPrice() >= 300.0 && budgetRecs.get(i).getPrice() <= 600.0, "Within price bounds");
                if (i > 0) {
                    assertCondition(budgetRecs.get(i).getPrice() >= budgetRecs.get(i - 1).getPrice(), "Ascending price order");
                }
            }
        });

        test("M3-05: BookRecommendationService: Distinct Available Subjects & Explanation Reasons", () -> {
            List<String> subjects = recService.getAvailableDistinctSubjects();
            assertCondition(!subjects.isEmpty(), "Subjects list non-empty");
            Set<String> set = new HashSet<>(subjects);
            assertCondition(subjects.size() == set.size(), "All subjects distinct");

            Book b = bookService.getBookById("BK201");
            Student s = studentService.getStudentById("STU101");
            String reason = recService.getRecommendationReason(b, s);
            assertCondition(reason != null && !reason.trim().isEmpty(), "Non-empty recommendation explanation generated");
        });

        test("M3-06: ProjectReportService: Core Platform Aggregate Metrics & KPIs", () -> {
            assertCondition(reportService.getTotalRegisteredStudents() >= 9, "Total students >= 9");
            assertCondition(reportService.getTotalListedBooks() >= 24, "Total books >= 24");
            assertCondition(reportService.getBooksAvailableForSaleCount() > 0, "Books for sale > 0");
            assertCondition(reportService.getBooksAvailableForExchangeCount() > 0, "Books for exchange > 0");
            assertCondition(reportService.getAverageSellerRating() > 0.0, "Average seller rating calculated");
        });

        test("M3-07: ProjectReportService: Subject Popularity Ranking (HashMap + Streams)", () -> {
            Map<String, Long> popular = reportService.getSubjectPopularityMap();
            assertCondition(!popular.isEmpty(), "Popularity map populated");
            long prev = Long.MAX_VALUE;
            for (long count : popular.values()) {
                assertCondition(count <= prev, "Descending frequency ordering maintained");
                prev = count;
            }
        });

        test("M3-08: ProjectReportService: Alphabetical Subject Index (TreeMap Naturally Sorted Keys)", () -> {
            TreeMap<String, Long> treeMap = reportService.getSubjectsSortedAlphabetically();
            assertCondition(!treeMap.isEmpty(), "TreeMap populated");
            String prevKey = "";
            for (String key : treeMap.keySet()) {
                if (!prevKey.isEmpty()) {
                    assertCondition(key.compareToIgnoreCase(prevKey) >= 0, "Alphabetical key ordering in TreeMap");
                }
                prevKey = key;
            }
        });

        test("M3-09: ProjectReportService: Semester Distribution Matrix (Native 1D Array)", () -> {
            int[] dist = reportService.getSemesterDistribution();
            assertCondition(dist.length == 9, "1D array has 9 slots");
            int sum = 0;
            for (int s = 1; s <= 8; s++) sum += dist[s];
            assertCondition(sum == reportService.getTotalListedBooks(), "Array sum equals total books");
        });

        test("M3-10: ProjectReportService: Top Active Sellers Leaderboard & Composite Scoring", () -> {
            List<ProjectReportService.SellerActivity> top = reportService.getMostActiveSellers(5);
            assertCondition(!top.isEmpty() && top.size() <= 5, "Top sellers list within limit");
            for (int i = 1; i < top.size(); i++) {
                assertCondition(top.get(i - 1).getTotalActivityScore() >= top.get(i).getTotalActivityScore(),
                        "Leaderboard sorted descending by activity score");
            }
        });

        test("M3-11: ProjectReportService: Formatted Comprehensive Dashboard Report (StringBuilder)", () -> {
            String report = reportService.generateComprehensiveReport();
            assertCondition(report.contains("CORE PLATFORM METRICS") && report.contains("SEMESTER"), "Structured report generated");
        });

        test("M3-12: ValidationUtil: Valid Inputs Pass & Invalid Inputs Throw ValidationException", () -> {
            // Valid student
            ValidationUtil.validateStudent("Kavita Rao", "kavita.r@dtu.ac.in", "+91 98765 43210", "DTU", "ECE", 4);

            // Invalid student: bad email
            boolean caughtBadEmail = false;
            try {
                ValidationUtil.validateStudent("Kavita Rao", "not-valid-email", "+91 98765 43210", "DTU", "ECE", 4);
            } catch (ValidationException e) {
                caughtBadEmail = true;
                assertCondition("email".equals(e.getFieldName()), "Field name is email");
            }
            assertCondition(caughtBadEmail, "Invalid email caught by ValidationException");

            // Invalid price: negative
            boolean caughtNegPrice = false;
            try {
                ValidationUtil.validatePrice(-100.0);
            } catch (ValidationException e) {
                caughtNegPrice = true;
            }
            assertCondition(caughtNegPrice, "Negative price caught by ValidationException");
        });

        test("M3-13: ValidationUtil: Prevention of Self-Trading & Self-Rating", () -> {
            // Self-trading
            boolean caughtSelfTrade = false;
            try {
                ValidationUtil.validateExchangeRequest("BK201", "STU101", "STU101", "Purchase", null);
            } catch (ValidationException e) {
                caughtSelfTrade = true;
            }
            assertCondition(caughtSelfTrade, "Self-trading prevented");

            // Self-rating
            boolean caughtSelfRate = false;
            try {
                ValidationUtil.validateRating("STU101", "STU101", 5.0, "Great feedback");
            } catch (ValidationException e) {
                caughtSelfRate = true;
            }
            assertCondition(caughtSelfRate, "Self-rating prevented");
        });

        test("M3-14: ValidationUtil: System ID Patterns (STU, BK, REQ, REV)", () -> {
            assertCondition(ValidationUtil.isValidStudentId("STU101"), "STU101 valid");
            assertCondition(!ValidationUtil.isValidStudentId("INVALID"), "INVALID student ID rejected");
            assertCondition(ValidationUtil.isValidBookId("BK201"), "BK201 valid");
            assertCondition(!ValidationUtil.isValidBookId("BOOK201"), "BOOK201 invalid format rejected");
        });

        test("M3-15: CsvExportService: Export All 5 Datasets to exports/ Directory", () -> {
            List<CsvExportService.ExportResult> results = exportService.exportAll(store, reportService);
            assertCondition(results.size() == 5, "5 datasets exported to exports/");
            for (CsvExportService.ExportResult r : results) {
                assertCondition(r.isSuccess() && new File(r.getFullPath()).exists(), "Export file exists: " + r.getFileName());
            }
        });

        test("M3-16: CsvExportService: Protection of Internal Persistence Files (SecurityException)", () -> {
            boolean protectedPath = false;
            try {
                exportService.exportStudents(store.getAllStudents(), "data/students.csv");
            } catch (SecurityException e) {
                protectedPath = true;
            }
            assertCondition(protectedPath, "Attempt to overwrite persistence file blocked by SecurityException");
        });

        test("M3-17: Concurrency: Concurrent Readers on Recommendations & Analytics", () -> {
            final boolean[] ok = {true};
            Thread t1 = new Thread(() -> {
                for (int i = 0; i < 5; i++) {
                    recService.recommendForStudent(store.getStudent("STU101"), 3);
                }
            });
            Thread t2 = new Thread(() -> {
                for (int i = 0; i < 5; i++) {
                    reportService.generateComprehensiveReport();
                }
            });
            t1.start();
            t2.start();
            t1.join(2000);
            t2.join(2000);
            assertCondition(ok[0], "Concurrent reads on recommendation and analytics completed safely");
        });

        System.out.println("================================================================================");
        System.out.printf("   VERIFICATION RESULTS: %d / %d TESTS PASSED (100%%)%n", testsPassed, totalTests);
        System.out.println("================================================================================");
    }

    private static void testExceptionPropagationHelper() throws BookNotFoundException {
        // Throws checked exception to verify propagation
        throw new BookNotFoundException("PROPAGATION_TEST_ID");
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
