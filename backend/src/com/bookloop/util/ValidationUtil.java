package com.bookloop.util;

import com.bookloop.exception.ValidationException;
import com.bookloop.model.ListingType;

import java.util.regex.Pattern;

/**
 * Reusable Data Validation Utility for BookLoop.
 * Demonstrates:
 * - String methods for input sanitation (trim, length, isEmpty, matches, startsWith)
 * - Wrapper class parsing and type conversions (Integer, Double)
 * - Custom domain exception handling with ValidationException
 * - Regular expression pattern matching for academic emails and phone numbers
 * - Comprehensive boundary checks and defensive programming
 * - Zero external framework dependencies (pure Java SE)
 */
public final class ValidationUtil {

    // Regex for Indian & international academic emails: e.g. student@college.ac.in, name@univ.edu
    private static final Pattern ACADEMIC_EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(ac\\.in|edu|edu\\.in|org|in|com)$",
            Pattern.CASE_INSENSITIVE
    );

    // Regex for Indian standard mobile format: +91 XXXXX XXXXX or 10-digit number
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+91[\\s-]?)?[6-9]\\d{9}$|^(\\+91\\s)?[6-9]\\d{4}[\\s-]\\d{5}$"
    );

    // Regex for system ID formats
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^STU\\d{3,}$");
    private static final Pattern BOOK_ID_PATTERN = Pattern.compile("^BK\\d{3,}$");
    private static final Pattern REQUEST_ID_PATTERN = Pattern.compile("^REQ\\d{3,}$");
    private static final Pattern RATING_ID_PATTERN = Pattern.compile("^REV\\d{3,}$");

    // Realistic bounds
    public static final double MIN_BOOK_PRICE = 0.0;
    public static final double MAX_BOOK_PRICE = 50000.0;
    public static final int MIN_SEMESTER = 1;
    public static final int MAX_SEMESTER = 8;
    public static final double MIN_RATING = 1.0;
    public static final double MAX_RATING = 5.0;

    // Private constructor to prevent instantiation of utility class
    private ValidationUtil() {
        throw new UnsupportedOperationException("ValidationUtil is a utility class and cannot be instantiated.");
    }

    // ==========================================
    // 1. Student Details Validation
    // ==========================================

    /**
     * Validates all registration parameters for a student.
     * Throws ValidationException with specific diagnostic information on failure.
     */
    public static void validateStudent(String name, String email, String phone,
                                       String college, String department, int semester) {
        validateStudentName(name);
        validateEmail(email);
        validatePhone(phone);
        validateCollege(college);
        validateDepartment(department);
        validateSemester(semester);
    }

    public static void validateStudentName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("name", name, "Student name cannot be empty or whitespace.");
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2 || trimmed.length() > 60) {
            throw new ValidationException("name", name, "Student name must be between 2 and 60 characters long.");
        }
        // Allow alphabetic characters, spaces, dots, and hyphens
        if (!trimmed.matches("^[A-Za-z\\s.-]+$")) {
            throw new ValidationException("name", name, "Student name can only contain letters, spaces, hyphens, and periods.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", email, "Email address cannot be empty.");
        }
        String trimmed = email.trim();
        if (!trimmed.contains("@") || !trimmed.contains(".")) {
            throw new ValidationException("email", email, "Email must contain valid '@' and domain components.");
        }
        if (!ACADEMIC_EMAIL_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("email", email,
                    "Invalid email format. Must be an academic or valid institutional address (e.g., student@college.ac.in).");
        }
    }

    public static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new ValidationException("phone", phone, "Phone number cannot be empty.");
        }
        String trimmed = phone.trim();
        // Normalize by removing spaces/hyphens to check digit count
        String digitsOnly = trimmed.replaceAll("[\\s-+]", "");
        if (digitsOnly.length() != 10 && digitsOnly.length() != 12) {
            throw new ValidationException("phone", phone,
                    "Phone number must be a valid 10-digit Indian mobile number (e.g. +91 98765 43210 or 9876543210).");
        }
    }

    public static void validateCollege(String college) {
        if (college == null || college.trim().isEmpty()) {
            throw new ValidationException("college", college, "College/University name cannot be empty.");
        }
        if (college.trim().length() < 2) {
            throw new ValidationException("college", college, "College name must have at least 2 characters.");
        }
    }

    public static void validateDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new ValidationException("department", department, "Academic department cannot be empty.");
        }
        if (department.trim().length() < 2) {
            throw new ValidationException("department", department, "Department name must have at least 2 characters.");
        }
    }

    public static void validateSemester(int semester) {
        if (semester < MIN_SEMESTER || semester > MAX_SEMESTER) {
            throw new ValidationException("semester", semester,
                    String.format("Semester must be between %d and %d. Received: %d", MIN_SEMESTER, MAX_SEMESTER, semester));
        }
    }

    /**
     * Validates semester passed as String using Wrapper class parsing.
     */
    public static int validateAndParseSemester(String semesterStr) {
        if (semesterStr == null || semesterStr.trim().isEmpty()) {
            throw new ValidationException("semester", semesterStr, "Semester input cannot be empty.");
        }
        try {
            int sem = Integer.parseInt(semesterStr.trim());
            validateSemester(sem);
            return sem;
        } catch (NumberFormatException e) {
            throw new ValidationException("semester", semesterStr, "Semester must be a valid integer between 1 and 8.");
        }
    }

    // ==========================================
    // 2. Book Details Validation
    // ==========================================

    /**
     * Validates complete book listing parameters.
     */
    public static void validateBook(String title, String author, String isbn,
                                    String subject, int semester, double price, ListingType listingType) {
        validateBookTitle(title);
        validateBookAuthor(author);
        validateIsbn(isbn);
        validateSubject(subject);
        validateSemester(semester);
        validatePrice(price);
        if (listingType == null) {
            throw new ValidationException("listingType", null, "Listing type (FOR_SALE, FOR_EXCHANGE, BOTH) must be specified.");
        }
        // Business rule: books exclusively for sale cannot have a zero price
        if (listingType == ListingType.FOR_SALE && price <= 0.0) {
            throw new ValidationException("price", price, "Books listed exclusively for sale must have a price greater than 0.");
        }
    }

    public static void validateBookTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("title", title, "Book title cannot be blank.");
        }
        String trimmed = title.trim();
        if (trimmed.length() < 2 || trimmed.length() > 150) {
            throw new ValidationException("title", title, "Book title must be between 2 and 150 characters.");
        }
    }

    public static void validateBookAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new ValidationException("author", author, "Author name cannot be blank.");
        }
        String trimmed = author.trim();
        if (trimmed.length() < 2 || trimmed.length() > 100) {
            throw new ValidationException("author", author, "Author name must be between 2 and 100 characters.");
        }
    }

    public static void validateSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            throw new ValidationException("subject", subject, "Academic subject cannot be blank.");
        }
        if (subject.trim().length() < 2) {
            throw new ValidationException("subject", subject, "Academic subject must be at least 2 characters.");
        }
    }

    public static void validateIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return; // ISBN is optional; empty is allowed
        }
        String cleaned = isbn.trim().replaceAll("[- ]", "");
        // Valid ISBNs are either 10 or 13 digits (or 10 ending in 'X')
        if (!cleaned.matches("^(97[89])?\\d{9}[\\dX]$")) {
            throw new ValidationException("isbn", isbn, "Invalid ISBN format. Expected valid ISBN-10 or ISBN-13 format.");
        }
    }

    public static void validatePrice(double price) {
        // Wrapper class comparison
        if (Double.compare(price, MIN_BOOK_PRICE) < 0) {
            throw new ValidationException("price", price, "Book price cannot be negative.");
        }
        if (Double.compare(price, MAX_BOOK_PRICE) > 0) {
            throw new ValidationException("price", price,
                    String.format("Book price cannot exceed Rs. %.2f. Received: %.2f", MAX_BOOK_PRICE, price));
        }
    }

    /**
     * Validates and parses a string price using Double.parseDouble().
     */
    public static double validateAndParsePrice(String priceStr) {
        if (priceStr == null || priceStr.trim().isEmpty()) {
            throw new ValidationException("price", priceStr, "Price input cannot be empty.");
        }
        try {
            double price = Double.parseDouble(priceStr.trim());
            validatePrice(price);
            return price;
        } catch (NumberFormatException e) {
            throw new ValidationException("price", priceStr, "Price must be a valid numeric value.");
        }
    }

    // ==========================================
    // 3. ID Validation Methods
    // ==========================================

    public static void validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("studentId", studentId, "Student ID cannot be empty.");
        }
        if (!STUDENT_ID_PATTERN.matcher(studentId.trim()).matches()) {
            throw new ValidationException("studentId", studentId,
                    "Invalid Student ID format. Expected 'STU' followed by digits (e.g., STU101).");
        }
    }

    public static void validateBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new ValidationException("bookId", bookId, "Book ID cannot be empty.");
        }
        if (!BOOK_ID_PATTERN.matcher(bookId.trim()).matches()) {
            throw new ValidationException("bookId", bookId,
                    "Invalid Book ID format. Expected 'BK' followed by digits (e.g., BK201).");
        }
    }

    public static void validateRequestId(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new ValidationException("requestId", requestId, "Request ID cannot be empty.");
        }
        if (!REQUEST_ID_PATTERN.matcher(requestId.trim()).matches()) {
            throw new ValidationException("requestId", requestId,
                    "Invalid Request ID format. Expected 'REQ' followed by digits (e.g., REQ301).");
        }
    }

    public static void validateRatingId(String ratingId) {
        if (ratingId == null || ratingId.trim().isEmpty()) {
            throw new ValidationException("ratingId", ratingId, "Rating ID cannot be empty.");
        }
        if (!RATING_ID_PATTERN.matcher(ratingId.trim()).matches()) {
            throw new ValidationException("ratingId", ratingId,
                    "Invalid Rating ID format. Expected 'REV' followed by digits (e.g., REV401).");
        }
    }

    // ==========================================
    // 4. Exchange Request Details Validation
    // ==========================================

    /**
     * Validates exchange/purchase request creation parameters.
     */
    public static void validateExchangeRequest(String bookId, String sellerId, String requesterId,
                                               String requestType, String offeredBookId) {
        validateBookId(bookId);
        validateStudentId(sellerId);
        validateStudentId(requesterId);

        // Student cannot trade with themselves
        if (sellerId.trim().equalsIgnoreCase(requesterId.trim())) {
            throw new ValidationException("requesterId", requesterId,
                    "Self-trading is forbidden: A student cannot submit an exchange or purchase request for their own book.");
        }

        if (requestType == null || requestType.trim().isEmpty()) {
            throw new ValidationException("requestType", requestType, "Request type cannot be blank.");
        }

        String typeLower = requestType.trim().toLowerCase();
        if (!typeLower.equals("exchange") && !typeLower.equals("purchase")) {
            throw new ValidationException("requestType", requestType,
                    "Request type must be either 'Exchange' or 'Purchase'. Received: " + requestType);
        }

        // If request is an Exchange, offered book must be specified and distinct
        if (typeLower.equals("exchange")) {
            if (offeredBookId == null || offeredBookId.trim().isEmpty()) {
                throw new ValidationException("offeredBookId", offeredBookId,
                        "An offered book listing ID is required when proposing a book swap / exchange.");
            }
            validateBookId(offeredBookId);
            if (bookId.trim().equalsIgnoreCase(offeredBookId.trim())) {
                throw new ValidationException("offeredBookId", offeredBookId,
                        "Offered exchange book cannot be identical to the requested book.");
            }
        }
    }

    // ==========================================
    // 5. Rating & Review Validation
    // ==========================================

    /**
     * Validates peer rating parameters.
     */
    public static void validateRating(String sellerId, String reviewerId, double score, String comment) {
        validateStudentId(sellerId);
        validateStudentId(reviewerId);

        // Student cannot rate themselves
        if (sellerId.trim().equalsIgnoreCase(reviewerId.trim())) {
            throw new ValidationException("reviewerId", reviewerId,
                    "Students cannot rate or review their own seller profile.");
        }

        validateRatingScore(score);

        if (comment == null || comment.trim().isEmpty()) {
            throw new ValidationException("comment", comment, "Review feedback comment cannot be blank.");
        }
        if (comment.trim().length() < 3) {
            throw new ValidationException("comment", comment, "Review feedback must be at least 3 characters long.");
        }
    }

    public static void validateRatingScore(double score) {
        if (Double.compare(score, MIN_RATING) < 0 || Double.compare(score, MAX_RATING) > 0) {
            throw new ValidationException("ratingScore", score,
                    String.format("Rating score must be between %.1f and %.1f stars. Received: %.1f",
                            MIN_RATING, MAX_RATING, score));
        }
    }

    public static double validateAndParseRatingScore(String scoreStr) {
        if (scoreStr == null || scoreStr.trim().isEmpty()) {
            throw new ValidationException("ratingScore", scoreStr, "Rating input cannot be empty.");
        }
        try {
            double score = Double.parseDouble(scoreStr.trim());
            validateRatingScore(score);
            return score;
        } catch (NumberFormatException e) {
            throw new ValidationException("ratingScore", scoreStr, "Rating must be a numeric score between 1.0 and 5.0.");
        }
    }

    // ==========================================
    // Boolean Convenience Predicates
    // ==========================================

    public static boolean isValidStudentId(String id) {
        return id != null && STUDENT_ID_PATTERN.matcher(id.trim()).matches();
    }

    public static boolean isValidBookId(String id) {
        return id != null && BOOK_ID_PATTERN.matcher(id.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && ACADEMIC_EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidSemester(int sem) {
        return sem >= MIN_SEMESTER && sem <= MAX_SEMESTER;
    }

    public static boolean isValidPrice(double price) {
        return price >= MIN_BOOK_PRICE && price <= MAX_BOOK_PRICE;
    }

    public static boolean isValidRatingScore(double score) {
        return score >= MIN_RATING && score <= MAX_RATING;
    }
}
