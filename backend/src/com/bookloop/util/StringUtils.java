package com.bookloop.util;

/**
 * Utility class for string processing, validation, and formatting in BookLoop.
 * Demonstrates:
 * - String immutability and String Pool behavior
 * - String comparison (==, equals, equalsIgnoreCase, compareTo)
 * - String searching (indexOf, contains, startsWith, endsWith)
 * - String manipulation (substring, replace, trim, toUpperCase, toLowerCase, split, join)
 * - String formatting (String.format, printf-style)
 * - StringBuilder (mutable, non-synchronized, for single-threaded string building)
 * - StringBuffer (mutable, synchronized, for thread-safe string building)
 * - Wrapper classes (Integer, Double, Boolean) and auto-boxing/unboxing
 * - Parsing with wrapper classes (Integer.parseInt, Double.parseDouble, etc.)
 * - Multidimensional arrays for tabular data formatting
 */
public class StringUtils {

    // ==========================================
    // String Pool & Immutability Demonstration
    // ==========================================

    /**
     * Demonstrates String Pool and immutability concepts.
     * Prints educational output showing how String Pool works.
     */
    public static void demonstrateStringPoolAndImmutability() {
        System.out.println("\n--- STRING POOL & IMMUTABILITY DEMONSTRATION ---");

        // String Pool: Both reference the same object in the String Pool
        String s1 = "BookLoop";
        String s2 = "BookLoop";

        // new String() creates a NEW object on the heap, outside the String Pool
        String s3 = new String("BookLoop");

        // == compares REFERENCES (memory addresses)
        System.out.println("s1 == s2 (both from String Pool):      " + (s1 == s2));      // true
        System.out.println("s1 == s3 (s3 is new String on heap):   " + (s1 == s3));      // false

        // .equals() compares CONTENT (the actual character sequence)
        System.out.println("s1.equals(s3) (content comparison):    " + s1.equals(s3));    // true

        // intern() returns the String Pool reference
        String s4 = s3.intern();
        System.out.println("s1 == s3.intern() (interned to Pool):  " + (s1 == s4));      // true

        // String Immutability: operations return NEW strings, original unchanged
        String original = "Student Exchange";
        String upper = original.toUpperCase();
        String replaced = original.replace("Student", "Peer");
        System.out.println("\nOriginal string (unchanged):   \"" + original + "\"");
        System.out.println("After toUpperCase() (new obj): \"" + upper + "\"");
        System.out.println("After replace() (new obj):     \"" + replaced + "\"");
        System.out.println("Original still same?           " + "Student Exchange".equals(original)); // true
    }

    // ==========================================
    // String Comparison Methods
    // ==========================================

    /**
     * Compares two strings using multiple comparison methods.
     * Demonstrates equals, equalsIgnoreCase, compareTo, ==.
     */
    public static void demonstrateStringComparison(String a, String b) {
        System.out.println("\n--- STRING COMPARISON: \"" + a + "\" vs \"" + b + "\" ---");
        System.out.println("  equals():           " + a.equals(b));
        System.out.println("  equalsIgnoreCase(): " + a.equalsIgnoreCase(b));
        System.out.println("  compareTo():        " + a.compareTo(b) + " (0=equal, <0=before, >0=after)");
        System.out.println("  == (reference):     " + (a == b));
    }

    // ==========================================
    // String Searching
    // ==========================================

    /**
     * Demonstrates string searching methods.
     */
    public static boolean containsIgnoreCase(String source, String target) {
        if (source == null || target == null) return false;
        return source.toLowerCase().contains(target.toLowerCase());
    }

    /**
     * Finds the position of a keyword in text.
     * Demonstrates indexOf, lastIndexOf.
     */
    public static int findKeyword(String text, String keyword) {
        if (text == null || keyword == null) return -1;
        return text.toLowerCase().indexOf(keyword.toLowerCase());
    }

    // ==========================================
    // String Manipulation & Formatting
    // ==========================================

    /**
     * Formats a book listing for display.
     * Demonstrates String.format and manipulation methods.
     */
    public static String formatBookListing(String id, String title, String author,
                                           int semester, double price, String status) {
        // Demonstrate substring, trim, toUpperCase
        String shortTitle = title.length() > 30 ? title.substring(0, 27) + "..." : title;
        String shortAuthor = author.length() > 18 ? author.substring(0, 15) + "..." : author;

        return String.format("%-7s | %-30s | %-18s | Sem %-2d | Rs.%-7.0f | %s",
                id, shortTitle.trim(), shortAuthor.trim(), semester, price, status.toUpperCase());
    }

    /**
     * Formats a student profile summary.
     * Demonstrates String.format, concatenation, and manipulation.
     */
    public static String formatStudentSummary(String name, String college, String dept,
                                               int semester, double rating) {
        // Demonstrate split to extract first name
        String[] nameParts = name.split(" ");
        String firstName = nameParts[0];

        // Demonstrate String.join
        String fullDept = String.join(" - ", dept, "Semester " + semester);

        return String.format("%s (%s) | %s | Rating: %.1f★", firstName, college, fullDept, rating);
    }

    /**
     * Validates and formats ISBN.
     * Demonstrates String manipulation: replace, matches (regex), length.
     */
    public static String formatISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty() || "N/A".equalsIgnoreCase(isbn.trim())) {
            return "N/A";
        }
        // Remove hyphens and spaces for validation
        String cleaned = isbn.replace("-", "").replace(" ", "").trim();
        // Check if it's a valid ISBN-10 or ISBN-13
        if (cleaned.matches("\\d{10}") || cleaned.matches("\\d{13}")) {
            return isbn.trim();
        }
        return isbn.trim(); // Return as-is if format is non-standard
    }

    /**
     * Validates email format.
     * Demonstrates String methods: contains, endsWith, indexOf, split.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        email = email.trim().toLowerCase();

        // Must contain exactly one @
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex != email.lastIndexOf('@')) return false;

        // Must have domain part with dot
        String domain = email.substring(atIndex + 1);
        return domain.contains(".") && !domain.startsWith(".") && !domain.endsWith(".");
    }

    /**
     * Validates phone number format.
     * Demonstrates String methods: startsWith, replace, matches.
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        String cleaned = phone.replace(" ", "").replace("-", "").replace("+", "").trim();
        // Indian phone: should be 10-12 digits
        return cleaned.matches("\\d{10,12}");
    }

    // ==========================================
    // StringBuilder Usage (non-synchronized, single-threaded)
    // ==========================================

    /**
     * Builds a detailed book report using StringBuilder.
     * Demonstrates append, insert, delete, reverse, toString, length, capacity.
     */
    public static String buildBookReport(String title, String author, String subject,
                                          int semester, double price, String condition) {
        StringBuilder sb = new StringBuilder(256); // Initial capacity hint
        sb.append("╔══════════════════════════════════════════════╗\n");
        sb.append("║           BOOK DETAIL REPORT                ║\n");
        sb.append("╠══════════════════════════════════════════════╣\n");
        sb.append("║ Title:     ").append(title).append("\n");
        sb.append("║ Author:    ").append(author).append("\n");
        sb.append("║ Subject:   ").append(subject).append("\n");
        sb.append("║ Semester:  ").append(semester).append("\n");
        sb.append("║ Price:     Rs.").append(String.format("%.2f", price)).append("\n");
        sb.append("║ Condition: ").append(condition).append("\n");
        sb.append("╚══════════════════════════════════════════════╝");

        return sb.toString();
    }

    /**
     * Builds a CSV line from book data using StringBuilder.
     */
    public static String buildCSVLine(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(",");
            // Escape commas and quotes in fields
            String field = fields[i] != null ? fields[i] : "";
            if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
                sb.append("\"").append(field.replace("\"", "\"\"")).append("\"");
            } else {
                sb.append(field);
            }
        }
        return sb.toString();
    }

    // ==========================================
    // StringBuffer Usage (synchronized, thread-safe)
    // ==========================================

    /**
     * Builds an activity log entry using StringBuffer (thread-safe).
     * Demonstrates StringBuffer for concurrent/shared string building.
     */
    public static String buildActivityLogEntry(String timestamp, String action,
                                                String userId, String details) {
        StringBuffer sbuf = new StringBuffer(128);
        sbuf.append("[").append(timestamp).append("] ");
        sbuf.append(action.toUpperCase()).append(" | ");
        sbuf.append("User: ").append(userId).append(" | ");
        sbuf.append(details);
        return sbuf.toString();
    }

    /**
     * Thread-safe search history builder using StringBuffer.
     */
    public static StringBuffer sharedSearchHistoryBuffer = new StringBuffer();

    public static synchronized void appendSearchToHistory(String keyword) {
        sharedSearchHistoryBuffer.append("[").append(System.currentTimeMillis())
                .append("] Searched: ").append(keyword).append("\n");
    }

    // ==========================================
    // Wrapper Classes & Parsing Demonstration
    // ==========================================

    /**
     * Demonstrates wrapper classes and parsing.
     * Shows Integer, Double, Boolean wrapper usage, auto-boxing, unboxing, parsing.
     */
    public static void demonstrateWrapperClasses() {
        System.out.println("\n--- WRAPPER CLASSES & PARSING DEMONSTRATION ---");

        // Auto-boxing: primitive -> Wrapper
        int primitiveInt = 42;
        Integer wrappedInt = primitiveInt; // Auto-boxing
        System.out.println("Auto-boxing int -> Integer: " + wrappedInt);

        // Auto-unboxing: Wrapper -> primitive
        double unboxedDouble = Double.valueOf(99.99); // Auto-unboxing
        System.out.println("Auto-unboxing Double -> double: " + unboxedDouble);

        // Parsing strings to numbers (common in user input validation)
        String priceStr = "450";
        String semesterStr = "5";
        String ratingStr = "4.8";
        String availableStr = "true";

        // Integer.parseInt - unchecked NumberFormatException if invalid
        Integer parsedPrice = Integer.parseInt(priceStr);
        System.out.println("Integer.parseInt(\"" + priceStr + "\"): " + parsedPrice);

        // Integer.valueOf - returns Integer object
        Integer semesterObj = Integer.valueOf(semesterStr);
        System.out.println("Integer.valueOf(\"" + semesterStr + "\"): " + semesterObj);

        // Double.parseDouble
        Double parsedRating = Double.parseDouble(ratingStr);
        System.out.println("Double.parseDouble(\"" + ratingStr + "\"): " + parsedRating);

        // Boolean.parseBoolean
        Boolean isAvailable = Boolean.parseBoolean(availableStr);
        System.out.println("Boolean.parseBoolean(\"" + availableStr + "\"): " + isAvailable);

        // Wrapper utility methods
        System.out.println("Integer.MAX_VALUE: " + Integer.MAX_VALUE);
        System.out.println("Double.MIN_VALUE:  " + Double.MIN_VALUE);
        System.out.println("Integer.toBinaryString(42): " + Integer.toBinaryString(42));
        System.out.println("Integer.toHexString(255):   " + Integer.toHexString(255));

        // Comparison using wrapper methods
        System.out.println("Integer.compare(10, 20): " + Integer.compare(10, 20));
        System.out.println("Double.compare(4.5, 4.5): " + Double.compare(4.5, 4.5));
    }

    /**
     * Safe parsing with wrapper classes - returns default value on parse failure.
     * Demonstrates exception handling with wrapper class parsing.
     */
    public static int safeParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    public static double safeParseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException | NullPointerException e) {
            return defaultValue;
        }
    }

    public static boolean safeParseBoolean(String value) {
        return Boolean.parseBoolean(value != null ? value.trim() : "false");
    }

    // ==========================================
    // Multidimensional Array Usage
    // ==========================================

    /**
     * Builds a semester-subject matrix using a 2D array.
     * Demonstrates multidimensional arrays for organizing book data.
     *
     * @return 2D String array [semester][subject columns] with book counts
     */
    public static String[][] buildSemesterSubjectMatrix(java.util.List<com.bookloop.model.Book> books) {
        // 8 semesters x 6 categories: CS, Electronics, Math, Data Science, Mechanical, Other
        String[] categories = {"Computer Science", "Electronics", "Mathematics",
                               "Data Science", "Mechanical", "Other"};
        // Row 0 = header, Rows 1-8 = semesters
        String[][] matrix = new String[9][categories.length + 1];

        // Header row
        matrix[0][0] = "Semester";
        for (int c = 0; c < categories.length; c++) {
            matrix[0][c + 1] = categories[c];
        }

        // Initialize data rows
        int[][] counts = new int[8][categories.length];
        for (com.bookloop.model.Book book : books) {
            int semIdx = book.getSemester() - 1; // 0-based
            if (semIdx < 0 || semIdx >= 8) continue;

            String cat = book.getCategory();
            int catIdx = categories.length - 1; // Default to "Other"
            for (int c = 0; c < categories.length - 1; c++) {
                if (categories[c].equalsIgnoreCase(cat)) {
                    catIdx = c;
                    break;
                }
            }
            counts[semIdx][catIdx]++;
        }

        // Fill matrix
        for (int s = 0; s < 8; s++) {
            matrix[s + 1][0] = "Sem " + (s + 1);
            for (int c = 0; c < categories.length; c++) {
                matrix[s + 1][c + 1] = String.valueOf(counts[s][c]);
            }
        }

        return matrix;
    }

    /**
     * Prints a 2D array as a formatted table.
     * Demonstrates multidimensional array traversal.
     */
    public static void printMatrix(String[][] matrix) {
        if (matrix == null || matrix.length == 0) return;

        // Calculate column widths
        int cols = matrix[0].length;
        int[] widths = new int[cols];
        for (String[] row : matrix) {
            for (int c = 0; c < Math.min(row.length, cols); c++) {
                if (row[c] != null && row[c].length() > widths[c]) {
                    widths[c] = row[c].length();
                }
            }
        }

        // Print with formatting
        StringBuilder divider = new StringBuilder("+");
        for (int w : widths) {
            for (int i = 0; i < w + 2; i++) divider.append("-");
            divider.append("+");
        }

        System.out.println(divider);
        for (int r = 0; r < matrix.length; r++) {
            StringBuilder line = new StringBuilder("|");
            for (int c = 0; c < cols; c++) {
                String cell = (c < matrix[r].length && matrix[r][c] != null) ? matrix[r][c] : "";
                line.append(String.format(" %-" + widths[c] + "s |", cell));
            }
            System.out.println(line);
            if (r == 0) System.out.println(divider); // Header separator
        }
        System.out.println(divider);
    }

    /**
     * Escapes a field for CSV output.
     */
    public static String escapeCSV(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Parses a CSV line respecting quoted fields.
     * Demonstrates detailed character-level String processing.
     */
    public static String[] parseCSVLine(String line) {
        if (line == null || line.isEmpty()) return new String[0];
        java.util.List<String> fields = new java.util.ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++; // skip next quote
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }
}
