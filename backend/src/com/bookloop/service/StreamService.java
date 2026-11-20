package com.bookloop.service;

import com.bookloop.data.DataStore;
import com.bookloop.model.*;
import com.bookloop.util.GenericPair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Service demonstrating Lambda Expressions and Stream API for BookLoop operations.
 * Demonstrates:
 * - Functional interfaces (Predicate, Function, Consumer, Supplier, Comparator)
 * - Lambda expressions (single-line and block lambdas)
 * - Method references (static, instance, constructor)
 * - Stream API operations:
 *   - filter(), map(), sorted(), distinct(), limit(), skip()
 *   - collect(), forEach(), count(), reduce()
 *   - Collectors.toList(), Collectors.groupingBy(), Collectors.joining()
 */
public class StreamService {

    private final DataStore store;

    public StreamService(DataStore store) {
        this.store = store;
    }

    // ==========================================
    // Functional Interfaces & Lambda Expressions
    // ==========================================

    /**
     * Demonstrates functional interfaces with lambdas.
     */
    public void demonstrateFunctionalInterfaces() {
        System.out.println("\n--- FUNCTIONAL INTERFACES & LAMBDA EXPRESSIONS ---");

        // 1. Predicate<T> - takes T, returns boolean
        Predicate<Book> isExpensive = book -> book.getPrice() > 500.0;
        // Block lambda with multiple statements
        Predicate<Book> isCSBook = (Book book) -> {
            String category = book.getCategory();
            return category != null && category.equalsIgnoreCase("Computer Science");
        };

        // 2. Function<T, R> - takes T, returns R
        Function<Book, String> bookSummary = book ->
                String.format("[%s] \"%s\" - Rs.%.0f", book.getId(), book.getTitle(), book.getPrice());

        // 3. Consumer<T> - takes T, returns void
        Consumer<Book> printBook = book ->
                System.out.printf("  > %s by %s (Rs.%.0f)%n", book.getTitle(), book.getAuthor(), book.getPrice());

        // 4. Supplier<T> - takes nothing, returns T
        Supplier<String> timestampSupplier = () ->
                java.time.LocalDateTime.now().toString();

        // Apply functional interfaces
        List<Book> books = store.getActiveBooks();
        System.out.println("\nExpensive CS Books (using Predicate):");
        books.stream()
             .filter(isExpensive.and(isCSBook))  // Predicate composition with .and()
             .forEach(printBook);                 // Consumer

        System.out.println("\nBook summaries (using Function):");
        books.stream()
             .limit(5)
             .map(bookSummary)                    // Function
             .forEach(System.out::println);       // Method reference

        System.out.println("\nTimestamp: " + timestampSupplier.get());
    }

    // ==========================================
    // Stream API Operations
    // ==========================================

    /**
     * Filters books by academic subject.
     * Demonstrates: filter() with lambda.
     */
    public List<Book> filterBooksBySubject(String subject) {
        return store.getActiveBooks().stream()
                .filter(book -> book.getSubject().equalsIgnoreCase(subject))
                .collect(Collectors.toList());
    }

    /**
     * Finds books within a price range.
     * Demonstrates: filter() with compound predicate.
     */
    public List<Book> findBooksInPriceRange(double minPrice, double maxPrice) {
        return store.getActiveBooks().stream()
                .filter(book -> book.getPrice() >= minPrice && book.getPrice() <= maxPrice)
                .sorted(Comparator.comparingDouble(Book::getPrice))  // Method reference
                .collect(Collectors.toList());
    }

    /**
     * Sorts books by price (ascending).
     * Demonstrates: sorted() with Comparator and method reference.
     */
    public List<Book> sortBooksByPrice() {
        return store.getActiveBooks().stream()
                .sorted(Comparator.comparingDouble(Book::getPrice))  // Method reference
                .collect(Collectors.toList());
    }

    /**
     * Sorts books by price descending.
     * Demonstrates: sorted() with Comparator.reversed().
     */
    public List<Book> sortBooksByPriceDescending() {
        return store.getActiveBooks().stream()
                .sorted(Comparator.comparingDouble(Book::getPrice).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Displays books grouped by semester.
     * Demonstrates: Collectors.groupingBy(), forEach on Map.
     */
    public Map<Integer, List<Book>> groupBooksBySemester() {
        return store.getActiveBooks().stream()
                .collect(Collectors.groupingBy(Book::getSemester));  // Method reference
    }

    /**
     * Gets distinct subject categories.
     * Demonstrates: map() + distinct().
     */
    public List<String> getDistinctSubjects() {
        return store.getActiveBooks().stream()
                .map(Book::getSubject)         // Method reference: extracts subject
                .distinct()                     // Removes duplicates
                .sorted()                       // Natural alphabetical sort
                .collect(Collectors.toList());
    }

    /**
     * Gets the top N most expensive books.
     * Demonstrates: sorted() + limit().
     */
    public List<Book> getTopExpensiveBooks(int n) {
        return store.getActiveBooks().stream()
                .sorted(Comparator.comparingDouble(Book::getPrice).reversed())
                .limit(n)                       // Take only top N
                .collect(Collectors.toList());
    }

    /**
     * Skips first N books and returns the rest (pagination).
     * Demonstrates: skip() + limit().
     */
    public List<Book> getBooksPaginated(int page, int pageSize) {
        return store.getActiveBooks().stream()
                .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .skip((long) (page - 1) * pageSize)  // Skip previous pages
                .limit(pageSize)                       // Take only current page
                .collect(Collectors.toList());
    }

    /**
     * Generates list of highly rated sellers (rating >= threshold).
     * Demonstrates: filter() + sorted() + map() chained pipeline.
     */
    public List<GenericPair<String, Double>> getHighlyRatedSellers(double minRating) {
        return store.getAllStudents().stream()
                .filter(s -> s.getRating() >= minRating)
                .sorted(Comparator.comparingDouble(Student::getRating).reversed())
                .map(s -> new GenericPair<>(
                        s.getName() + " (" + s.getCollege() + ")",
                        s.getRating()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Gets total value of all active book listings.
     * Demonstrates: mapToDouble() + sum() (terminal operation).
     */
    public double getTotalCatalogValue() {
        return store.getActiveBooks().stream()
                .mapToDouble(Book::getPrice)   // Method reference
                .sum();
    }

    /**
     * Gets average book price.
     * Demonstrates: mapToDouble() + average().
     */
    public OptionalDouble getAverageBookPrice() {
        return store.getActiveBooks().stream()
                .mapToDouble(Book::getPrice)
                .average();
    }

    /**
     * Counts books per category.
     * Demonstrates: Collectors.groupingBy() + Collectors.counting().
     */
    public Map<String, Long> countBooksByCategory() {
        return store.getActiveBooks().stream()
                .collect(Collectors.groupingBy(Book::getCategory, Collectors.counting()));
    }

    /**
     * Gets a comma-separated list of book titles.
     * Demonstrates: Collectors.joining().
     */
    public String getBookTitlesList(int limit) {
        return store.getActiveBooks().stream()
                .map(Book::getTitle)            // Method reference
                .limit(limit)
                .collect(Collectors.joining(", "));  // Join with separator
    }

    /**
     * Finds the cheapest book.
     * Demonstrates: min() with Comparator.
     */
    public Optional<Book> findCheapestBook() {
        return store.getActiveBooks().stream()
                .min(Comparator.comparingDouble(Book::getPrice));
    }

    /**
     * Finds the most expensive book.
     * Demonstrates: max() with Comparator.
     */
    public Optional<Book> findMostExpensiveBook() {
        return store.getActiveBooks().stream()
                .max(Comparator.comparingDouble(Book::getPrice));
    }

    /**
     * Gets books by listing type using method reference.
     * Demonstrates: filter with enum comparison.
     */
    public List<Book> getBooksByListingType(ListingType type) {
        return store.getActiveBooks().stream()
                .filter(b -> b.getListingType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Gets exchange requests by status.
     * Demonstrates: filter() on enum.
     */
    public List<ExchangeRequest> getRequestsByStatus(RequestStatus status) {
        return store.getAllRequests().stream()
                .filter(r -> r.getStatus() == status)
                .sorted(Comparator.comparing(ExchangeRequest::getRequestDate).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Full stream pipeline demo: filters, transforms, sorts, limits, and collects.
     */
    public void demonstrateFullStreamPipeline() {
        System.out.println("\n--- FULL STREAM API PIPELINE DEMONSTRATION ---");

        List<Book> books = store.getActiveBooks();

        // Pipeline 1: filter -> map -> sorted -> distinct -> limit -> collect
        System.out.println("\n[Pipeline 1] Distinct subjects from CS books (limit 5):");
        List<String> csSubjects = books.stream()
                .filter(b -> "Computer Science".equalsIgnoreCase(b.getCategory()))  // filter
                .map(Book::getSubject)                                               // map (method ref)
                .distinct()                                                          // distinct
                .sorted()                                                            // sorted
                .limit(5)                                                            // limit
                .collect(Collectors.toList());                                        // collect
        csSubjects.forEach(s -> System.out.println("  - " + s));

        // Pipeline 2: sorted -> skip -> limit (pagination)
        System.out.println("\n[Pipeline 2] Books page 2 (skip 5, take 5):");
        books.stream()
                .sorted(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER))
                .skip(5)
                .limit(5)
                .forEach(b -> System.out.printf("  [%s] \"%s\" - Rs.%.0f%n",
                        b.getId(), b.getTitle(), b.getPrice()));

        // Pipeline 3: filter -> sorted -> map -> collect with joining
        System.out.println("\n[Pipeline 3] Affordable books (<= Rs.400) titles:");
        String affordableTitles = books.stream()
                .filter(b -> b.getPrice() <= 400.0)
                .sorted(Comparator.comparingDouble(Book::getPrice))
                .map(b -> b.getTitle() + " (Rs." + (int) b.getPrice() + ")")
                .collect(Collectors.joining(" | "));
        System.out.println("  " + affordableTitles);

        // Pipeline 4: groupingBy -> forEach
        System.out.println("\n[Pipeline 4] Book count by semester:");
        Map<Integer, Long> semesterCounts = books.stream()
                .collect(Collectors.groupingBy(Book::getSemester, Collectors.counting()));
        semesterCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.printf("  Semester %d: %d books%n", e.getKey(), e.getValue()));

        // Pipeline 5: Statistics
        System.out.println("\n[Pipeline 5] Price statistics:");
        DoubleSummaryStatistics priceStats = books.stream()
                .mapToDouble(Book::getPrice)
                .summaryStatistics();
        System.out.printf("  Total: Rs.%.0f | Avg: Rs.%.0f | Min: Rs.%.0f | Max: Rs.%.0f | Count: %d%n",
                priceStats.getSum(), priceStats.getAverage(),
                priceStats.getMin(), priceStats.getMax(), priceStats.getCount());

        // Pipeline 6: Highly rated sellers
        System.out.println("\n[Pipeline 6] Top 5 highly rated sellers:");
        store.getAllStudents().stream()
                .filter(s -> s.getReviewCount() > 0)
                .sorted(Comparator.comparingDouble(Student::getRating).reversed())
                .limit(5)
                .forEach(s -> System.out.printf("  %s (%.1f★, %d reviews) - %s%n",
                        s.getName(), s.getRating(), s.getReviewCount(), s.getCollege()));
    }
}
