package com.bookloop.service;

import com.bookloop.data.DataStore;
import com.bookloop.model.Book;
import com.bookloop.model.Condition;
import com.bookloop.model.Student;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Intelligent Book Recommendation Engine for BookLoop.
 * Recommends academic textbooks to students based on:
 * - Current semester alignment and curriculum progression
 * - Academic department & subject interest affinities
 * - Student budget and customized price ranges
 * - Automatic exclusion of books listed by the current student
 * - Strict exclusion of sold, exchanged, or unavailable books
 * - Seller reputation and physical book condition scores
 *
 * Demonstrates:
 * - ArrayList and HashMap data structures
 * - Lambda expressions and functional programming
 * - Stream API: filter(), map(), sorted(), distinct(), collect()
 * - Comparator creation, reverse ordering, and Comparator chaining
 * - Zero external framework dependencies
 */
public class BookRecommendationService {

    private final DataStore store;

    // HashMap: maps academic department keywords to typical curriculum subjects
    private final Map<String, List<String>> departmentSubjectMap = new HashMap<>();

    public BookRecommendationService(DataStore store) {
        this.store = store;
        initializeDepartmentSubjectMap();
    }

    /**
     * Populates department to subject affinities using ArrayList and HashMap.
     */
    private void initializeDepartmentSubjectMap() {
        departmentSubjectMap.put("computer science", Arrays.asList(
                "Algorithms", "Data Structures", "Operating Systems", "Database Systems",
                "Computer Networks", "Artificial Intelligence", "Theory of Computation",
                "Object Oriented Programming", "Software Engineering", "Cybersecurity", "Compilers"
        ));

        departmentSubjectMap.put("information technology", Arrays.asList(
                "Database Systems", "Computer Networks", "Web Technologies", "Cloud Computing",
                "Information Security", "Operating Systems", "Object Oriented Programming", "Software Engineering"
        ));

        departmentSubjectMap.put("data science", Arrays.asList(
                "Machine Learning", "Artificial Intelligence", "Deep Learning", "Data Mining",
                "Big Data Analytics", "Statistics", "Python for Data Science", "Algorithms"
        ));

        departmentSubjectMap.put("electrical", Arrays.asList(
                "Circuit Analysis", "Electric Machines", "Power Systems", "Control Systems",
                "Signals and Systems", "Electromagnetics", "Analog Electronics"
        ));

        departmentSubjectMap.put("electronics", Arrays.asList(
                "Digital Electronics", "Microprocessors", "VLSI Design", "Signal Processing",
                "Communication Systems", "Analog Circuits", "Electromagnetics"
        ));

        departmentSubjectMap.put("mechanical", Arrays.asList(
                "Thermodynamics", "Fluid Mechanics", "Strength of Materials",
                "Machine Design", "Manufacturing Processes", "Theory of Machines"
        ));
    }

    // ==========================================
    // Core Recommendation Method 1: Smart Personalization
    // ==========================================

    /**
     * Generates top recommended books tailored for a specific student.
     * Evaluates:
     * 1. Semester matching (exact semester +40, adjacent semester +20)
     * 2. Department curriculum subject relevance (+30)
     * 3. Seller trust score / rating (+ sellerRating * 4)
     * 4. Physical book condition (+5 to +10)
     *
     * Invariants enforced:
     * - Only available books (isAvailable() == true)
     * - Excludes books listed by the requesting student
     *
     * Demonstrates: HashMap, ArrayList, Lambdas, Stream filter, map, sorted, Comparator.
     */
    public List<Book> recommendForStudent(Student student, int limit) {
        if (student == null) {
            return getPopularAvailableBooks(limit);
        }

        List<Book> availableBooks = store.getActiveBooks();
        String studentId = student.getId();

        // 1. Filter out books uploaded by the same student & ensure availability
        List<Book> candidates = availableBooks.stream()
                .filter(Book::isAvailable)
                .filter(b -> !b.getSellerId().equalsIgnoreCase(studentId))
                .collect(Collectors.toCollection(ArrayList::new));

        if (candidates.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. HashMap: compute and cache composite recommendation score for each candidate
        Map<String, Double> scoreMap = new HashMap<>();
        List<String> coreSubjects = getCoreSubjectsForDepartment(student.getDepartment());

        for (Book b : candidates) {
            double score = 0.0;

            // Semester relevance
            if (b.getSemester() == student.getSemester()) {
                score += 40.0; // Exact semester match
            } else if (Math.abs(b.getSemester() - student.getSemester()) == 1) {
                score += 20.0; // Next or previous semester
            } else {
                score += 5.0;  // General curriculum
            }

            // Subject / Department relevance
            boolean subjectMatch = coreSubjects.stream()
                    .anyMatch(core -> b.getSubject().toLowerCase().contains(core.toLowerCase()) ||
                                      b.getCategory().toLowerCase().contains(core.toLowerCase()) ||
                                      b.getTitle().toLowerCase().contains(core.toLowerCase()));
            if (subjectMatch) {
                score += 30.0;
            }

            // Seller rating bonus (1.0 to 5.0 stars -> 4.0 to 20.0 points)
            score += (b.getSellerRating() * 4.0);

            // Condition bonus
            if (b.getCondition() == Condition.LIKE_NEW) {
                score += 10.0;
            } else if (b.getCondition() == Condition.VERY_GOOD) {
                score += 7.0;
            } else if (b.getCondition() == Condition.GOOD) {
                score += 4.0;
            }

            // Affordable price bonus (books under Rs. 500 get minor boost)
            if (b.getPrice() > 0 && b.getPrice() <= 500.0) {
                score += 5.0;
            }

            scoreMap.put(b.getId(), score);
        }

        // 3. Comparator: sort by recommendation score descending, then by price ascending
        Comparator<Book> scoreComparator = Comparator
                .comparingDouble((Book b) -> scoreMap.getOrDefault(b.getId(), 0.0))
                .reversed()
                .thenComparingDouble(Book::getPrice);

        // 4. Stream pipeline: sorted -> distinct -> limit -> collect to ArrayList
        return candidates.stream()
                .sorted(scoreComparator)
                .distinct()
                .limit(limit > 0 ? limit : 10)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ==========================================
    // Core Recommendation Method 2: By Semester
    // ==========================================

    /**
     * Recommends available books matching a specific semester.
     * Avoids recommending books uploaded by the current student.
     * Demonstrates: Stream filter, sorted with Comparator chaining, distinct.
     */
    public List<Book> recommendBySemester(int semester, String excludeStudentId) {
        return store.getActiveBooks().stream()
                .filter(Book::isAvailable)
                .filter(b -> excludeStudentId == null || !b.getSellerId().equalsIgnoreCase(excludeStudentId))
                .filter(b -> b.getSemester() == semester)
                .sorted(Comparator.comparingDouble(Book::getSellerRating).reversed()
                        .thenComparingDouble(Book::getPrice))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ==========================================
    // Core Recommendation Method 3: By Subjects
    // ==========================================

    /**
     * Recommends books based on a list of subjects the student is interested in.
     * Avoids self-recommendation and unavailable books.
     * Demonstrates: Lambda expressions, anyMatch, distinct, sorted.
     */
    public List<Book> recommendBySubjects(List<String> subjects, String excludeStudentId) {
        if (subjects == null || subjects.isEmpty()) {
            return Collections.emptyList();
        }

        // Clean & lowercase subject queries
        List<String> queryList = subjects.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        return store.getActiveBooks().stream()
                .filter(Book::isAvailable)
                .filter(b -> excludeStudentId == null || !b.getSellerId().equalsIgnoreCase(excludeStudentId))
                .filter(b -> queryList.stream().anyMatch(q ->
                        b.getSubject().toLowerCase().contains(q) ||
                        b.getCategory().toLowerCase().contains(q) ||
                        b.getTitle().toLowerCase().contains(q)))
                .sorted(Comparator.comparingDouble(Book::getSellerRating).reversed())
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ==========================================
    // Core Recommendation Method 4: By Price Range
    // ==========================================

    /**
     * Recommends books strictly within a student-selected budget/price range.
     * Avoids self-recommendation and unavailable books.
     * Demonstrates: filter with compound predicates, sorted by price ascending.
     */
    public List<Book> recommendByPriceRange(double minPrice, double maxPrice, String excludeStudentId) {
        return store.getActiveBooks().stream()
                .filter(Book::isAvailable)
                .filter(b -> excludeStudentId == null || !b.getSellerId().equalsIgnoreCase(excludeStudentId))
                .filter(b -> b.getPrice() >= minPrice && b.getPrice() <= maxPrice)
                .sorted(Comparator.comparingDouble(Book::getPrice)
                        .thenComparing(Comparator.comparingDouble(Book::getSellerRating).reversed()))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ==========================================
    // Core Recommendation Method 5: Multi-Factor Personalized
    // ==========================================

    /**
     * Advanced personalized search combining semester, budget cap, and optional subject preferences.
     * Demonstrates: Streams, lambdas, map, filter, sorted.
     */
    public List<Book> recommendPersonalized(Student student, Double maxPrice,
                                            List<String> preferredSubjects, int limit) {
        String studentId = (student != null) ? student.getId() : null;
        int targetSemester = (student != null) ? student.getSemester() : 0;

        return store.getActiveBooks().stream()
                .filter(Book::isAvailable)
                .filter(b -> studentId == null || !b.getSellerId().equalsIgnoreCase(studentId))
                .filter(b -> maxPrice == null || b.getPrice() <= maxPrice)
                .filter(b -> {
                    if (preferredSubjects == null || preferredSubjects.isEmpty()) return true;
                    return preferredSubjects.stream().anyMatch(sub ->
                            b.getSubject().toLowerCase().contains(sub.toLowerCase()) ||
                            b.getTitle().toLowerCase().contains(sub.toLowerCase()));
                })
                .sorted(Comparator.comparingInt((Book b) -> (targetSemester > 0 && b.getSemester() == targetSemester) ? 0 : 1)
                        .thenComparing(Comparator.comparingDouble(Book::getSellerRating).reversed())
                        .thenComparingDouble(Book::getPrice))
                .distinct()
                .limit(limit > 0 ? limit : 8)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // ==========================================
    // Distinct Category & Subject Discovery
    // ==========================================

    /**
     * Returns distinct academic subjects available for a student to explore.
     * Demonstrates: map(), distinct(), sorted().
     */
    public List<String> getAvailableDistinctSubjects() {
        return store.getActiveBooks().stream()
                .map(Book::getSubject)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns top fallback popular available books (highest rated sellers).
     */
    public List<Book> getPopularAvailableBooks(int limit) {
        return store.getActiveBooks().stream()
                .filter(Book::isAvailable)
                .sorted(Comparator.comparingDouble(Book::getSellerRating).reversed()
                        .thenComparingDouble(Book::getPrice))
                .distinct()
                .limit(limit > 0 ? limit : 6)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Generates a descriptive human-readable reason why a book is recommended.
     */
    public String getRecommendationReason(Book book, Student student) {
        if (student == null) {
            return String.format("High seller rating (%.1f★) | Popular on campus", book.getSellerRating());
        }

        StringBuilder reason = new StringBuilder();
        if (book.getSemester() == student.getSemester()) {
            reason.append("Direct match for your Semester ").append(student.getSemester()).append(" curriculum");
        } else if (Math.abs(book.getSemester() - student.getSemester()) == 1) {
            reason.append("Relevant for Semester ").append(book.getSemester());
        } else {
            reason.append("Popular in ").append(book.getSubject());
        }

        if (book.getSellerRating() >= 4.8) {
            reason.append(" | Highly-rated seller (").append(book.getSellerRating()).append("★)");
        }
        if (book.getCondition() == Condition.LIKE_NEW || book.getCondition() == Condition.VERY_GOOD) {
            reason.append(" | Condition: ").append(book.getCondition().getDisplayName());
        }
        return reason.toString();
    }

    // ==========================================
    // Helper Methods
    // ==========================================

    private List<String> getCoreSubjectsForDepartment(String department) {
        if (department == null) return Collections.emptyList();
        String deptLower = department.toLowerCase();

        for (Map.Entry<String, List<String>> entry : departmentSubjectMap.entrySet()) {
            if (deptLower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        // Fallback default subjects
        return Arrays.asList("Algorithms", "Mathematics", "Programming", "Computer", "Engineering");
    }
}
