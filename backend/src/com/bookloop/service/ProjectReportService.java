package com.bookloop.service;

import com.bookloop.data.DataStore;
import com.bookloop.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analytics and Executive Reporting Service for BookLoop.
 * Generates platform intelligence and operational analytics:
 * - Registered student population and college distributions
 * - Catalog inventory metrics (sale vs exchange vs dual-mode)
 * - Transaction lifecycle tracking (pending, accepted, rejected, completed)
 * - Academic subject popularity rankings
 * - Seller reputation and activity leaderboards
 * - Native array semester distribution matrices
 *
 * Demonstrates:
 * - Collections Framework (List, Map, Set)
 * - HashMap for dynamic aggregation and counting
 * - TreeMap for sorted indexing by academic subject
 * - Native primitive arrays for semester distribution modeling
 * - Loops and method abstraction
 * - Streams and Lambda expressions (groupingBy, counting, averagingDouble, sorted)
 * - High-fidelity formatting with String and StringBuilder
 */
public class ProjectReportService {

    private final DataStore store;

    public ProjectReportService(DataStore store) {
        this.store = store;
    }

    // ==========================================
    // Core Aggregate Metrics
    // ==========================================

    public int getTotalRegisteredStudents() {
        return store.getAllStudents().size();
    }

    public int getTotalListedBooks() {
        return store.getAllBooks().size();
    }

    public long getAvailableBooksCount() {
        return store.getActiveBooks().size();
    }

    /**
     * Books available for sale (FOR_SALE or BOTH listing type).
     * Demonstrates: Stream filter with compound lambda predicate and count().
     */
    public long getBooksAvailableForSaleCount() {
        return store.getActiveBooks().stream()
                .filter(b -> b.getListingType() == ListingType.FOR_SALE || b.getListingType() == ListingType.BOTH)
                .count();
    }

    /**
     * Books available for exchange (FOR_EXCHANGE or BOTH listing type).
     */
    public long getBooksAvailableForExchangeCount() {
        return store.getActiveBooks().stream()
                .filter(b -> b.getListingType() == ListingType.FOR_EXCHANGE || b.getListingType() == ListingType.BOTH)
                .count();
    }

    public long getBooksAvailableBothCount() {
        return store.getActiveBooks().stream()
                .filter(b -> b.getListingType() == ListingType.BOTH)
                .count();
    }

    public long getTotalPendingRequests() {
        return store.getAllRequests().stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .count();
    }

    public long getTotalAcceptedRequests() {
        return store.getAllRequests().stream()
                .filter(r -> r.getStatus() == RequestStatus.ACCEPTED)
                .count();
    }

    public long getTotalRejectedRequests() {
        return store.getAllRequests().stream()
                .filter(r -> r.getStatus() == RequestStatus.REJECTED)
                .count();
    }

    public long getTotalCompletedRequests() {
        return store.getAllRequests().stream()
                .filter(r -> r.getStatus() == RequestStatus.COMPLETED)
                .count();
    }

    /**
     * Calculates the overall platform average seller rating across all registered students.
     * Demonstrates: Stream mapToDouble with average() and OptionalDouble.
     */
    public double getAverageSellerRating() {
        List<Student> students = store.getAllStudents();
        return students.stream()
                .mapToDouble(Student::getRating)
                .average()
                .orElse(0.0);
    }

    public double getTotalCatalogValuation() {
        return store.getAllBooks().stream()
                .mapToDouble(Book::getPrice)
                .sum();
    }

    // ==========================================
    // Subject Popularity Analytics (HashMap & TreeMap)
    // ==========================================

    /**
     * Aggregates book counts by academic subject, sorted by popularity (highest count first).
     * Demonstrates:
     * - HashMap creation
     * - Stream groupingBy + counting()
     * - Sorting Map.Entry with lambda
     * - LinkedHashMap to preserve descending popularity order
     */
    public Map<String, Long> getSubjectPopularityMap() {
        // Step 1: Group books by subject using HashMap & Streams
        Map<String, Long> frequencyMap = store.getAllBooks().stream()
                .collect(Collectors.groupingBy(Book::getSubject, HashMap::new, Collectors.counting()));

        // Step 2: Sort by count descending, then by name alphabetically
        return frequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Returns subjects organized in natural alphabetical order using TreeMap.
     * Demonstrates: TreeMap implementation for sorted key iteration.
     */
    public TreeMap<String, Long> getSubjectsSortedAlphabetically() {
        TreeMap<String, Long> treeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (Book b : store.getAllBooks()) {
            treeMap.put(b.getSubject(), treeMap.getOrDefault(b.getSubject(), 0L) + 1L);
        }
        return treeMap;
    }

    // ==========================================
    // Semester Distribution (Native Primitive Array)
    // ==========================================

    /**
     * Generates textbook counts per semester (Semesters 1 through 8).
     * Demonstrates:
     * - 1D primitive int array
     * - Traditional for-each loop traversal
     * - Array boundary indexing
     *
     * @return int array where index [1..8] represents Semester 1..8 counts. Index 0 is unused (0).
     */
    public int[] getSemesterDistribution() {
        int[] semesterCounts = new int[9]; // index 0 unused, 1..8 for semesters

        for (Book book : store.getAllBooks()) {
            int sem = book.getSemester();
            if (sem >= 1 && sem <= 8) {
                semesterCounts[sem]++;
            }
        }
        return semesterCounts;
    }

    // ==========================================
    // Most Active Sellers Leaderboard
    // ==========================================

    /**
     * Model representing a seller's engagement and transaction activity.
     */
    public static class SellerActivity {
        private final Student student;
        private final int booksListed;
        private final int booksSold;
        private final int booksExchanged;
        private final double rating;
        private final int totalActivityScore;

        public SellerActivity(Student student, int booksListed, int booksSold,
                              int booksExchanged, double rating) {
            this.student = student;
            this.booksListed = booksListed;
            this.booksSold = booksSold;
            this.booksExchanged = booksExchanged;
            this.rating = rating;
            // Activity score: listed * 2 + sold * 5 + exchanged * 4 + (rating * 3)
            this.totalActivityScore = (int) (booksListed * 2 + booksSold * 5 + booksExchanged * 4 + (rating * 3));
        }

        public Student getStudent() { return student; }
        public int getBooksListed() { return booksListed; }
        public int getBooksSold() { return booksSold; }
        public int getBooksExchanged() { return booksExchanged; }
        public double getRating() { return rating; }
        public int getTotalActivityScore() { return totalActivityScore; }
    }

    /**
     * Computes and returns the top active student sellers.
     * Demonstrates:
     * - HashMap for counting listed books per seller
     * - Traditional for loops and iterative counting
     * - Stream sorting by composite activity score descending
     */
    public List<SellerActivity> getMostActiveSellers(int limit) {
        // Step 1: Count listed books per seller using HashMap
        Map<String, Integer> sellerBookCounts = new HashMap<>();
        for (Book b : store.getAllBooks()) {
            sellerBookCounts.put(b.getSellerId(), sellerBookCounts.getOrDefault(b.getSellerId(), 0) + 1);
        }

        // Step 2: Build SellerActivity records
        List<SellerActivity> activities = new ArrayList<>();
        for (Student s : store.getAllStudents()) {
            int listed = sellerBookCounts.getOrDefault(s.getId(), 0);
            activities.add(new SellerActivity(
                    s, listed, s.getBooksSold(), s.getBooksExchanged(), s.getRating()
            ));
        }

        // Step 3: Sort by activity score descending, then rating descending using Stream
        return activities.stream()
                .sorted(Comparator.comparingInt(SellerActivity::getTotalActivityScore)
                        .thenComparingDouble(SellerActivity::getRating)
                        .reversed())
                .limit(limit > 0 ? limit : 5)
                .collect(Collectors.toList());
    }

    /**
     * Aggregates student counts by college/university.
     * Demonstrates: HashMap aggregation.
     */
    public Map<String, Integer> getCollegeParticipationMap() {
        Map<String, Integer> collegeMap = new HashMap<>();
        for (Student s : store.getAllStudents()) {
            collegeMap.put(s.getCollege(), collegeMap.getOrDefault(s.getCollege(), 0) + 1);
        }
        return collegeMap;
    }

    // ==========================================
    // Formatted Text & Dashboard Generation
    // ==========================================

    /**
     * Generates a comprehensive, human-readable terminal dashboard report.
     * Demonstrates:
     * - Extensive StringBuilder usage (append, appendLine)
     * - String.format() with table column alignments
     * - Array iteration
     * - Map iteration
     */
    public String generateComprehensiveReport() {
        StringBuilder sb = new StringBuilder();

        sb.append("================================================================================\n");
        sb.append("                BOOKLOOP PLATFORM ANALYTICS & EXECUTIVE REPORT                  \n");
        sb.append("                Generated: ").append(java.time.LocalDateTime.now()).append("\n");
        sb.append("================================================================================\n\n");

        // Section 1: Core Platform Overview
        sb.append("1. CORE PLATFORM METRICS\n");
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format("  %-38s : %d students%n", "Total Registered Students", getTotalRegisteredStudents()));
        sb.append(String.format("  %-38s : %d books%n", "Total Textbooks Listed (All-Time)", getTotalListedBooks()));
        sb.append(String.format("  %-38s : %d books%n", "Active Books Currently in Catalog", getAvailableBooksCount()));
        sb.append(String.format("  %-38s : %d books%n", "Books Available for Sale", getBooksAvailableForSaleCount()));
        sb.append(String.format("  %-38s : %d books%n", "Books Available for Exchange/Swap", getBooksAvailableForExchangeCount()));
        sb.append(String.format("  %-38s : %d books%n", "Dual-Option Books (Sale & Swap)", getBooksAvailableBothCount()));
        sb.append(String.format("  %-38s : Rs. %,.2f%n", "Total Catalog Valuation", getTotalCatalogValuation()));
        sb.append(String.format("  %-38s : %.2f / 5.00 ★%n", "Platform Average Seller Rating", getAverageSellerRating()));

        // Section 2: Transaction Lifecycle
        sb.append("\n2. TRANSACTION & EXCHANGE REQUEST LIFECYCLE\n");
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format("  %-38s : %d requests%n", "Pending Requests (Awaiting Seller)", getTotalPendingRequests()));
        sb.append(String.format("  %-38s : %d requests%n", "Accepted Requests (In Progress)", getTotalAcceptedRequests()));
        sb.append(String.format("  %-38s : %d requests%n", "Completed Transactions", getTotalCompletedRequests()));
        sb.append(String.format("  %-38s : %d requests%n", "Rejected Requests", getTotalRejectedRequests()));

        // Section 3: Semester Distribution (Native Array)
        sb.append("\n3. TEXTBOOK DISTRIBUTION ACROSS SEMESTERS (1D ARRAY)\n");
        sb.append("--------------------------------------------------------------------------------\n");
        int[] semDist = getSemesterDistribution();
        for (int sem = 1; sem <= 8; sem++) {
            int count = semDist[sem];
            int barLen = count * 2;
            StringBuilder bar = new StringBuilder();
            for (int b = 0; b < barLen; b++) bar.append("■");
            sb.append(String.format("  Semester %d : %2d books | %s%n", sem, count, bar));
        }

        // Section 4: Most Popular Academic Subjects (HashMap & Streams)
        sb.append("\n4. TOP MOST POPULAR ACADEMIC SUBJECTS (FREQUENCY RANKING)\n");
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format("  %-4s | %-36s | %-12s%n", "Rank", "Subject Name", "Total Books"));
        sb.append("  -----+--------------------------------------+-------------\n");
        Map<String, Long> popularSubjects = getSubjectPopularityMap();
        int rank = 1;
        for (Map.Entry<String, Long> entry : popularSubjects.entrySet()) {
            sb.append(String.format("  #%-3d | %-36s | %-12d%n", rank++, entry.getKey(), entry.getValue()));
            if (rank > 10) break; // Top 10
        }

        // Section 5: Most Active Sellers Leaderboard
        sb.append("\n5. MOST ACTIVE STUDENT SELLERS LEADERBOARD\n");
        sb.append("--------------------------------------------------------------------------------\n");
        sb.append(String.format("  %-4s | %-18s | %-20s | %-6s | %-7s | %-6s | %-6s%n",
                "Rank", "Student Name", "College", "Rating", "Listed", "Sold", "Swapped"));
        sb.append("  -----+--------------------+----------------------+--------+---------+--------+--------\n");
        List<SellerActivity> topSellers = getMostActiveSellers(5);
        int sellerRank = 1;
        for (SellerActivity sa : topSellers) {
            sb.append(String.format("  #%-3d | %-18s | %-20s | %4.1f★  | %-7d | %-6d | %-6d%n",
                    sellerRank++,
                    sa.getStudent().getName(),
                    truncate(sa.getStudent().getCollege(), 20),
                    sa.getRating(),
                    sa.getBooksListed(),
                    sa.getBooksSold(),
                    sa.getBooksExchanged()));
        }

        // Section 6: College Participation Distribution
        sb.append("\n6. CAMPUS & UNIVERSITY PARTICIPATION (STUDENT NETWORK)\n");
        sb.append("--------------------------------------------------------------------------------\n");
        Map<String, Integer> colleges = getCollegeParticipationMap();
        for (Map.Entry<String, Integer> entry : colleges.entrySet()) {
            sb.append(String.format("  - %-42s : %2d student(s)%n", entry.getKey(), entry.getValue()));
        }

        sb.append("================================================================================\n");
        return sb.toString();
    }

    private String truncate(String str, int max) {
        if (str == null) return "";
        return str.length() <= max ? str : str.substring(0, max - 3) + "...";
    }
}
