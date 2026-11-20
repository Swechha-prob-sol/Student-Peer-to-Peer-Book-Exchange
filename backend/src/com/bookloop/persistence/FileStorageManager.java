package com.bookloop.persistence;

import com.bookloop.exception.DataPersistenceException;
import com.bookloop.model.*;
import com.bookloop.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based persistence layer for BookLoop.
 * Demonstrates:
 * - FileInputStream and FileOutputStream (byte streams)
 * - FileReader and FileWriter (character streams)
 * - BufferedReader and BufferedWriter for efficiency
 * - StreamTokenizer for parsing
 * - Reading and writing CSV files
 * - Exception-safe resource management (try-with-resources, finally)
 * - Importing and exporting records
 * - Checked exceptions with throws clause
 */
public class FileStorageManager {

    private static final String DATA_DIR = "data";
    private static final String STUDENTS_FILE = DATA_DIR + File.separator + "students.csv";
    private static final String BOOKS_FILE = DATA_DIR + File.separator + "books.csv";
    private static final String REQUESTS_FILE = DATA_DIR + File.separator + "requests.csv";
    private static final String RATINGS_FILE = DATA_DIR + File.separator + "ratings.csv";
    private static final String METADATA_FILE = DATA_DIR + File.separator + "metadata.dat";

    /**
     * Ensures the data directory exists.
     */
    private static void ensureDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new DataPersistenceException("Failed to create data directory: " + DATA_DIR);
            }
        }
    }

    /**
     * Checks if saved data files exist.
     * Used to determine whether to load from file or use dummy data.
     */
    public static boolean savedDataExists() {
        File studentsFile = new File(STUDENTS_FILE);
        File booksFile = new File(BOOKS_FILE);
        return studentsFile.exists() && studentsFile.length() > 0 &&
               booksFile.exists() && booksFile.length() > 0;
    }

    // ==========================================
    // SAVE OPERATIONS (Writing to files)
    // ==========================================

    /**
     * Saves all students to CSV using FileWriter + BufferedWriter (character stream).
     * Demonstrates: FileWriter, BufferedWriter, try-with-resources, throws.
     */
    public static void saveStudents(List<Student> students) throws IOException {
        ensureDataDirectory();
        // FileWriter is a character stream (writes chars)
        // BufferedWriter wraps it for efficiency (buffers output)
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            // Write CSV header
            writer.write("id,name,email,phone,joinedDate,college,department,semester,rating,reviewCount,booksSold,booksExchanged");
            writer.newLine();

            for (Student s : students) {
                StringBuilder line = new StringBuilder();
                line.append(StringUtils.escapeCSV(s.getId())).append(",");
                line.append(StringUtils.escapeCSV(s.getName())).append(",");
                line.append(StringUtils.escapeCSV(s.getEmail())).append(",");
                line.append(StringUtils.escapeCSV(s.getPhone())).append(",");
                line.append(StringUtils.escapeCSV(s.getJoinedDate())).append(",");
                line.append(StringUtils.escapeCSV(s.getCollege())).append(",");
                line.append(StringUtils.escapeCSV(s.getDepartment())).append(",");
                line.append(s.getSemester()).append(",");
                line.append(s.getRating()).append(",");
                line.append(s.getReviewCount()).append(",");
                line.append(s.getBooksSold()).append(",");
                line.append(s.getBooksExchanged());
                writer.write(line.toString());
                writer.newLine();
            }
            writer.flush();
        }
        // try-with-resources automatically closes writer (finally equivalent)
    }

    /**
     * Saves all books to CSV using FileWriter.
     */
    public static void saveBooks(List<Book> books) throws IOException {
        ensureDataDirectory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKS_FILE))) {
            writer.write("id,title,author,isbn,subject,category,semester,edition,condition,price,listingType,preferredExchange,description,sellerId,sellerName,sellerCollege,sellerRating,status,dateListed");
            writer.newLine();

            for (Book b : books) {
                StringBuilder line = new StringBuilder();
                line.append(StringUtils.escapeCSV(b.getId())).append(",");
                line.append(StringUtils.escapeCSV(b.getTitle())).append(",");
                line.append(StringUtils.escapeCSV(b.getAuthor())).append(",");
                line.append(StringUtils.escapeCSV(b.getIsbn())).append(",");
                line.append(StringUtils.escapeCSV(b.getSubject())).append(",");
                line.append(StringUtils.escapeCSV(b.getCategory())).append(",");
                line.append(b.getSemester()).append(",");
                line.append(StringUtils.escapeCSV(b.getEdition())).append(",");
                line.append(StringUtils.escapeCSV(b.getCondition().name())).append(",");
                line.append(b.getPrice()).append(",");
                line.append(StringUtils.escapeCSV(b.getListingType().name())).append(",");
                line.append(StringUtils.escapeCSV(b.getPreferredExchange())).append(",");
                line.append(StringUtils.escapeCSV(b.getDescription())).append(",");
                line.append(StringUtils.escapeCSV(b.getSellerId())).append(",");
                line.append(StringUtils.escapeCSV(b.getSellerName())).append(",");
                line.append(StringUtils.escapeCSV(b.getSellerCollege())).append(",");
                line.append(b.getSellerRating()).append(",");
                line.append(StringUtils.escapeCSV(b.getStatus())).append(",");
                line.append(StringUtils.escapeCSV(b.getDateListed()));
                writer.write(line.toString());
                writer.newLine();
            }
            writer.flush();
        }
    }

    /**
     * Saves all exchange requests to CSV.
     */
    public static void saveRequests(List<ExchangeRequest> requests) throws IOException {
        ensureDataDirectory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(REQUESTS_FILE))) {
            writer.write("id,bookId,bookTitle,bookPrice,sellerId,sellerName,requesterId,requesterName,requestType,offeredBookId,offeredBookTitle,requestDate,status,trackingStep,notes");
            writer.newLine();

            for (ExchangeRequest r : requests) {
                StringBuilder line = new StringBuilder();
                line.append(StringUtils.escapeCSV(r.getId())).append(",");
                line.append(StringUtils.escapeCSV(r.getBookId())).append(",");
                line.append(StringUtils.escapeCSV(r.getBookTitle())).append(",");
                line.append(r.getBookPrice()).append(",");
                line.append(StringUtils.escapeCSV(r.getSellerId())).append(",");
                line.append(StringUtils.escapeCSV(r.getSellerName())).append(",");
                line.append(StringUtils.escapeCSV(r.getRequesterId())).append(",");
                line.append(StringUtils.escapeCSV(r.getRequesterName())).append(",");
                line.append(StringUtils.escapeCSV(r.getRequestType())).append(",");
                line.append(StringUtils.escapeCSV(r.getOfferedBookId() != null ? r.getOfferedBookId() : "")).append(",");
                line.append(StringUtils.escapeCSV(r.getOfferedBookTitle())).append(",");
                line.append(StringUtils.escapeCSV(r.getRequestDate())).append(",");
                line.append(StringUtils.escapeCSV(r.getStatus().name())).append(",");
                line.append(r.getTrackingStep()).append(",");
                line.append(StringUtils.escapeCSV(r.getNotes()));
                writer.write(line.toString());
                writer.newLine();
            }
            writer.flush();
        }
    }

    /**
     * Saves all ratings to CSV.
     */
    public static void saveRatings(List<Rating> ratings) throws IOException {
        ensureDataDirectory();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RATINGS_FILE))) {
            writer.write("id,sellerId,reviewerId,reviewerName,rating,comment,date");
            writer.newLine();

            for (Rating r : ratings) {
                StringBuilder line = new StringBuilder();
                line.append(StringUtils.escapeCSV(r.getId())).append(",");
                line.append(StringUtils.escapeCSV(r.getSellerId())).append(",");
                line.append(StringUtils.escapeCSV(r.getReviewerId())).append(",");
                line.append(StringUtils.escapeCSV(r.getReviewerName())).append(",");
                line.append(r.getRating()).append(",");
                line.append(StringUtils.escapeCSV(r.getComment())).append(",");
                line.append(StringUtils.escapeCSV(r.getDate()));
                writer.write(line.toString());
                writer.newLine();
            }
            writer.flush();
        }
    }

    /**
     * Saves metadata using FileOutputStream (byte stream).
     * Demonstrates: FileOutputStream, byte stream writing.
     */
    public static void saveMetadata(int nextStudentId, int nextBookId,
                                     int nextRequestId, int nextRatingId) throws IOException {
        ensureDataDirectory();
        // FileOutputStream writes raw bytes
        try (FileOutputStream fos = new FileOutputStream(METADATA_FILE);
             DataOutputStream dos = new DataOutputStream(fos)) {
            dos.writeInt(nextStudentId);
            dos.writeInt(nextBookId);
            dos.writeInt(nextRequestId);
            dos.writeInt(nextRatingId);
            dos.flush();
        }
    }

    // ==========================================
    // LOAD OPERATIONS (Reading from files)
    // ==========================================

    /**
     * Loads students from CSV using FileReader + BufferedReader (character stream).
     * Demonstrates: FileReader, BufferedReader, readLine(), parsing.
     */
    public static List<Student> loadStudents() throws IOException {
        List<Student> students = new ArrayList<>();
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) return students;

        // FileReader is a character stream (reads chars)
        // BufferedReader wraps it for efficiency and readLine()
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine(); // Skip CSV header
            if (header == null) return students;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    String[] fields = StringUtils.parseCSVLine(line);
                    if (fields.length >= 12) {
                        // Wrapper class parsing: Integer.parseInt, Double.parseDouble
                        Student s = new Student(
                            fields[0].trim(),                           // id
                            fields[1].trim(),                           // name
                            fields[2].trim(),                           // email
                            fields[3].trim(),                           // phone
                            fields[4].trim(),                           // joinedDate
                            fields[5].trim(),                           // college
                            fields[6].trim(),                           // department
                            Integer.parseInt(fields[7].trim()),         // semester (Wrapper parsing)
                            Double.parseDouble(fields[8].trim()),       // rating (Wrapper parsing)
                            Integer.parseInt(fields[9].trim()),         // reviewCount
                            Integer.parseInt(fields[10].trim()),        // booksSold
                            Integer.parseInt(fields[11].trim())         // booksExchanged
                        );
                        students.add(s);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("[WARN] Skipping malformed student record: " + e.getMessage());
                }
            }
        }
        return students;
    }

    /**
     * Loads books from CSV using FileReader + BufferedReader.
     */
    public static List<Book> loadBooks() throws IOException {
        List<Book> books = new ArrayList<>();
        File file = new File(BOOKS_FILE);
        if (!file.exists()) return books;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) return books;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    String[] f = StringUtils.parseCSVLine(line);
                    if (f.length >= 19) {
                        Book b = new Book(
                            f[0].trim(),                             // id
                            f[1].trim(),                             // title
                            f[2].trim(),                             // author
                            f[3].trim(),                             // isbn
                            f[4].trim(),                             // subject
                            f[5].trim(),                             // category
                            Integer.parseInt(f[6].trim()),           // semester
                            f[7].trim(),                             // edition
                            Condition.fromString(f[8].trim()),       // condition
                            Double.parseDouble(f[9].trim()),         // price
                            ListingType.fromString(f[10].trim()),    // listingType
                            f[11].trim(),                            // preferredExchange
                            f[12].trim(),                            // description
                            f[13].trim(),                            // sellerId
                            f[14].trim(),                            // sellerName
                            f[15].trim(),                            // sellerCollege
                            Double.parseDouble(f[16].trim()),        // sellerRating
                            f[17].trim(),                            // status
                            f[18].trim()                             // dateListed
                        );
                        books.add(b);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("[WARN] Skipping malformed book record: " + e.getMessage());
                }
            }
        }
        return books;
    }

    /**
     * Loads exchange requests from CSV.
     */
    public static List<ExchangeRequest> loadRequests() throws IOException {
        List<ExchangeRequest> requests = new ArrayList<>();
        File file = new File(REQUESTS_FILE);
        if (!file.exists()) return requests;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) return requests;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    String[] f = StringUtils.parseCSVLine(line);
                    if (f.length >= 15) {
                        ExchangeRequest r = new ExchangeRequest(
                            f[0].trim(),                             // id
                            f[1].trim(),                             // bookId
                            f[2].trim(),                             // bookTitle
                            Double.parseDouble(f[3].trim()),         // bookPrice
                            f[4].trim(),                             // sellerId
                            f[5].trim(),                             // sellerName
                            f[6].trim(),                             // requesterId
                            f[7].trim(),                             // requesterName
                            f[8].trim(),                             // requestType
                            f[9].trim().isEmpty() ? null : f[9].trim(), // offeredBookId
                            f[10].trim(),                            // offeredBookTitle
                            f[11].trim(),                            // requestDate
                            RequestStatus.fromString(f[12].trim()),  // status
                            Integer.parseInt(f[13].trim()),          // trackingStep
                            f[14].trim()                             // notes
                        );
                        requests.add(r);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("[WARN] Skipping malformed request record: " + e.getMessage());
                }
            }
        }
        return requests;
    }

    /**
     * Loads ratings from CSV.
     */
    public static List<Rating> loadRatings() throws IOException {
        List<Rating> ratings = new ArrayList<>();
        File file = new File(RATINGS_FILE);
        if (!file.exists()) return ratings;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) return ratings;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                try {
                    String[] f = StringUtils.parseCSVLine(line);
                    if (f.length >= 7) {
                        Rating r = new Rating(
                            f[0].trim(),                             // id
                            f[1].trim(),                             // sellerId
                            f[2].trim(),                             // reviewerId
                            f[3].trim(),                             // reviewerName
                            Double.parseDouble(f[4].trim()),         // rating
                            f[5].trim(),                             // comment
                            f[6].trim()                              // date
                        );
                        ratings.add(r);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("[WARN] Skipping malformed rating record: " + e.getMessage());
                }
            }
        }
        return ratings;
    }

    /**
     * Loads metadata using FileInputStream (byte stream).
     * Demonstrates: FileInputStream, byte stream reading.
     *
     * @return int array: [nextStudentId, nextBookId, nextRequestId, nextRatingId]
     */
    public static int[] loadMetadata() throws IOException {
        File file = new File(METADATA_FILE);
        if (!file.exists()) return null;

        // FileInputStream reads raw bytes
        try (FileInputStream fis = new FileInputStream(file);
             DataInputStream dis = new DataInputStream(fis)) {
            int[] meta = new int[4];
            meta[0] = dis.readInt();
            meta[1] = dis.readInt();
            meta[2] = dis.readInt();
            meta[3] = dis.readInt();
            return meta;
        }
    }

    // ==========================================
    // StreamTokenizer Demonstration
    // ==========================================

    /**
     * Reads and tokenizes a CSV file using StreamTokenizer.
     * Demonstrates: StreamTokenizer, token types (TT_WORD, TT_NUMBER, TT_EOF, TT_EOL).
     */
    public static void demonstrateStreamTokenizer() throws IOException {
        File file = new File(STUDENTS_FILE);
        if (!file.exists()) {
            System.out.println("[INFO] No students file found for StreamTokenizer demo.");
            return;
        }

        System.out.println("\n--- STREAMTOKENIZER DEMONSTRATION ---");
        // StreamTokenizer parses a stream into tokens
        try (FileReader fileReader = new FileReader(file)) {
            StreamTokenizer tokenizer = new StreamTokenizer(fileReader);

            // Configure tokenizer
            tokenizer.eolIsSignificant(true);  // Treat end-of-line as a token
            tokenizer.ordinaryChar(',');        // Treat comma as a delimiter
            tokenizer.wordChars('@', '@');      // Include @ in words (for emails)
            tokenizer.wordChars('.', '.');      // Include . in words
            tokenizer.wordChars('+', '+');      // Include + in words (for phone)
            tokenizer.wordChars('-', '-');      // Include - in words
            tokenizer.wordChars('_', '_');      // Include _ in words

            int tokenCount = 0;
            int wordCount = 0;
            int numberCount = 0;

            System.out.println("First 20 tokens from students.csv:");
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF && tokenCount < 20) {
                tokenCount++;
                switch (tokenizer.ttype) {
                    case StreamTokenizer.TT_WORD:
                        System.out.printf("  Token %2d: WORD   = \"%s\"%n", tokenCount, tokenizer.sval);
                        wordCount++;
                        break;
                    case StreamTokenizer.TT_NUMBER:
                        System.out.printf("  Token %2d: NUMBER = %.1f%n", tokenCount, tokenizer.nval);
                        numberCount++;
                        break;
                    case StreamTokenizer.TT_EOL:
                        System.out.printf("  Token %2d: END OF LINE%n", tokenCount);
                        break;
                    default:
                        System.out.printf("  Token %2d: CHAR   = '%c'%n", tokenCount, (char) tokenizer.ttype);
                        break;
                }
            }
            System.out.printf("Summary: %d words, %d numbers found in first %d tokens.%n",
                    wordCount, numberCount, tokenCount);
        }
    }

    // ==========================================
    // Export & Import (Byte Stream for binary backup)
    // ==========================================

    /**
     * Exports all data to a text report using FileOutputStream (byte stream).
     * Demonstrates writing character data as bytes.
     */
    public static void exportDataReport(List<Student> students, List<Book> books,
                                         List<ExchangeRequest> requests, List<Rating> ratings)
                                         throws IOException {
        ensureDataDirectory();
        String reportFile = DATA_DIR + File.separator + "bookloop_report.txt";

        // Using FileOutputStream to write bytes
        try (FileOutputStream fos = new FileOutputStream(reportFile)) {
            StringBuilder report = new StringBuilder();
            report.append("====================================================\n");
            report.append("   BOOKLOOP DATA EXPORT REPORT\n");
            report.append("   Generated: ").append(java.time.LocalDateTime.now()).append("\n");
            report.append("====================================================\n\n");

            report.append("STUDENTS (").append(students.size()).append(" records)\n");
            report.append("----------------------------------------------------\n");
            for (Student s : students) {
                report.append(String.format("  [%s] %s | %s | Sem %d | Rating: %.1f★%n",
                        s.getId(), s.getName(), s.getCollege(), s.getSemester(), s.getRating()));
            }

            report.append("\nBOOKS (").append(books.size()).append(" records)\n");
            report.append("----------------------------------------------------\n");
            for (Book b : books) {
                report.append(String.format("  [%s] \"%s\" by %s | Rs.%.0f | %s%n",
                        b.getId(), b.getTitle(), b.getAuthor(), b.getPrice(), b.getStatus()));
            }

            report.append("\nEXCHANGE REQUESTS (").append(requests.size()).append(" records)\n");
            report.append("----------------------------------------------------\n");
            for (ExchangeRequest r : requests) {
                report.append(String.format("  [%s] %s for \"%s\" | %s | %s%n",
                        r.getId(), r.getRequestType(), r.getBookTitle(),
                        r.getStatus().getDisplayName(), r.getTrackingStepDescription()));
            }

            report.append("\nRATINGS (").append(ratings.size()).append(" records)\n");
            report.append("----------------------------------------------------\n");
            for (Rating r : ratings) {
                report.append(String.format("  [%s] %.1f★ by %s for seller %s%n",
                        r.getId(), r.getRating(), r.getReviewerName(), r.getSellerId()));
            }

            // Write string as bytes
            byte[] reportBytes = report.toString().getBytes("UTF-8");
            fos.write(reportBytes);
            fos.flush();

            System.out.println("[SUCCESS] Data report exported to: " + reportFile);
            System.out.printf("  Report size: %d bytes (%d characters)%n",
                    reportBytes.length, report.length());
        }
    }

    /**
     * Deletes all saved data files (for reset functionality).
     */
    public static void deleteAllDataFiles() {
        String[] files = {STUDENTS_FILE, BOOKS_FILE, REQUESTS_FILE, RATINGS_FILE, METADATA_FILE};
        for (String filePath : files) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
