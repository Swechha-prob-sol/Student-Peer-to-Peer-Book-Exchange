package com.bookloop.persistence;

import com.bookloop.data.DataStore;
import com.bookloop.model.*;
import com.bookloop.service.ProjectReportService;
import com.bookloop.util.StringUtils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Dedicated CSV Export Service for BookLoop.
 * Generates clean, user-facing CSV datasets and analytics reports.
 *
 * Strict Architecture Rules:
 * - Completely decoupled from internal application persistence files in data/
 * - Never overwrites active persistence files (enforced via path validation guards)
 * - Writes all output into the dedicated exports/ directory
 * - Demonstrates Java syllabus I/O concepts:
 *   - Character streams: FileWriter & BufferedWriter
 *   - Byte streams: FileOutputStream for raw binary/text export
 *   - Exception handling: try-with-resources, IOException, defensive file validation
 *   - Robust CSV escaping (handles quotes, commas, newlines)
 */
public class CsvExportService {

    public static final String DEFAULT_EXPORT_DIR = "exports";
    private static final String PERSISTENCE_DIR = "data";

    private final String exportDirectory;

    public CsvExportService() {
        this(DEFAULT_EXPORT_DIR);
    }

    public CsvExportService(String exportDirectory) {
        this.exportDirectory = exportDirectory;
        ensureExportDirectory();
    }

    /**
     * Value object representing the outcome of an export operation.
     */
    public static class ExportResult {
        private final String fileName;
        private final String fullPath;
        private final int recordCount;
        private final long fileSizeBytes;
        private final boolean success;
        private final String message;

        public ExportResult(String fileName, String fullPath, int recordCount,
                            long fileSizeBytes, boolean success, String message) {
            this.fileName = fileName;
            this.fullPath = fullPath;
            this.recordCount = recordCount;
            this.fileSizeBytes = fileSizeBytes;
            this.success = success;
            this.message = message;
        }

        public String getFileName() { return fileName; }
        public String getFullPath() { return fullPath; }
        public int getRecordCount() { return recordCount; }
        public long getFileSizeBytes() { return fileSizeBytes; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }

        @Override
        public String toString() {
            return String.format("[EXPORT] %-28s -> %s (%d records, %d bytes)",
                    fileName, (success ? "SUCCESS" : "FAILED"), recordCount, fileSizeBytes);
        }
    }

    /**
     * Ensures export directory exists and is distinct from persistence directory.
     */
    private void ensureExportDirectory() {
        File dir = new File(exportDirectory);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created && !dir.exists()) {
                System.err.println("[WARN] Could not create export directory: " + exportDirectory);
            }
        }
    }

    /**
     * Safety guard ensuring an export target never points to active persistence files.
     */
    private void assertNotPersistencePath(String targetPath) {
        File target = new File(targetPath);
        File parent = target.getParentFile();
        if (parent != null && parent.getName().equalsIgnoreCase(PERSISTENCE_DIR)) {
            throw new SecurityException("Safety Violation: Export target path '" + targetPath +
                    "' collides with persistence directory '" + PERSISTENCE_DIR + "'. Export aborted.");
        }
    }

    // ==========================================
    // Master Export All Operation
    // ==========================================

    /**
     * Exports all five core entities and reports in one comprehensive operation.
     * Generates:
     * 1. students_export.csv
     * 2. books_export.csv
     * 3. requests_export.csv
     * 4. ratings_export.csv
     * 5. analytics_summary_export.csv
     */
    public List<ExportResult> exportAll(DataStore store, ProjectReportService reportService) {
        List<ExportResult> results = new ArrayList<>();
        ensureExportDirectory();

        try {
            results.add(exportStudents(store.getAllStudents(), exportDirectory + File.separator + "students_export.csv"));
            results.add(exportBooks(store.getAllBooks(), exportDirectory + File.separator + "books_export.csv"));
            results.add(exportRequests(store.getAllRequests(), exportDirectory + File.separator + "requests_export.csv"));
            results.add(exportRatings(store.getAllRatings(), exportDirectory + File.separator + "ratings_export.csv"));
            if (reportService != null) {
                results.add(exportAnalyticsReport(reportService, exportDirectory + File.separator + "analytics_summary_export.csv"));
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Batch export interrupted: " + e.getMessage());
        }
        return results;
    }

    // ==========================================
    // 1. Export Students
    // ==========================================

    /**
     * Exports student records to CSV using FileWriter + BufferedWriter.
     */
    public ExportResult exportStudents(List<Student> students, String targetPath) throws IOException {
        assertNotPersistencePath(targetPath);
        ensureExportDirectory();
        File file = new File(targetPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write CSV Header
            writer.write("Student_ID,Full_Name,College_Email,Phone,Joined_Date,College,Department,Semester,Rating,Review_Count,Books_Sold,Books_Exchanged");
            writer.newLine();

            for (Student s : students) {
                StringBuilder line = new StringBuilder();
                line.append(escape(s.getId())).append(",");
                line.append(escape(s.getName())).append(",");
                line.append(escape(s.getEmail())).append(",");
                line.append(escape(s.getPhone())).append(",");
                line.append(escape(s.getJoinedDate())).append(",");
                line.append(escape(s.getCollege())).append(",");
                line.append(escape(s.getDepartment())).append(",");
                line.append(s.getSemester()).append(",");
                line.append(String.format(Locale.US, "%.2f", s.getRating())).append(",");
                line.append(s.getReviewCount()).append(",");
                line.append(s.getBooksSold()).append(",");
                line.append(s.getBooksExchanged());
                writer.write(line.toString());
                writer.newLine();
            }
            writer.flush();
        }

        return new ExportResult(file.getName(), file.getAbsolutePath(), students.size(), file.length(), true, "OK");
    }

    // ==========================================
    // 2. Export Books
    // ==========================================

    /**
     * Exports all listed textbooks to CSV.
     */
    public ExportResult exportBooks(List<Book> books, String targetPath) throws IOException {
        assertNotPersistencePath(targetPath);
        ensureExportDirectory();
        File file = new File(targetPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Book_ID,Title,Author,ISBN,Subject,Category,Semester,Edition,Condition,Price_INR,Listing_Type,Preferred_Exchange,Seller_ID,Seller_Name,Seller_College,Seller_Rating,Status,Date_Listed");
            writer.newLine();

            for (Book b : books) {
                StringBuilder line = new StringBuilder();
                line.append(escape(b.getId())).append(",");
                line.append(escape(b.getTitle())).append(",");
                line.append(escape(b.getAuthor())).append(",");
                line.append(escape(b.getIsbn())).append(",");
                line.append(escape(b.getSubject())).append(",");
                line.append(escape(b.getCategory())).append(",");
                line.append(b.getSemester()).append(",");
                line.append(escape(b.getEdition())).append(",");
                line.append(escape(b.getCondition().getDisplayName())).append(",");
                line.append(String.format(Locale.US, "%.2f", b.getPrice())).append(",");
                line.append(escape(b.getListingType().getDisplayName())).append(",");
                line.append(escape(b.getPreferredExchange())).append(",");
                line.append(escape(b.getSellerId())).append(",");
                line.append(escape(b.getSellerName())).append(",");
                line.append(escape(b.getSellerCollege())).append(",");
                line.append(String.format(Locale.US, "%.2f", b.getSellerRating())).append(",");
                line.append(escape(b.getStatus())).append(",");
                line.append(escape(b.getDateListed()));
                writer.write(line.toString());
                writer.newLine();
            }
            writer.flush();
        }

        return new ExportResult(file.getName(), file.getAbsolutePath(), books.size(), file.length(), true, "OK");
    }

    // ==========================================
    // 3. Export Exchange Requests History
    // ==========================================

    /**
     * Exports full transaction and exchange request history to CSV.
     */
    public ExportResult exportRequests(List<ExchangeRequest> requests, String targetPath) throws IOException {
        assertNotPersistencePath(targetPath);
        ensureExportDirectory();
        File file = new File(targetPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Request_ID,Book_ID,Book_Title,Book_Price_INR,Seller_ID,Seller_Name,Requester_ID,Requester_Name,Request_Type,Offered_Book_ID,Offered_Book_Title,Request_Date,Status,Tracking_Step,Step_Description,Notes");
            writer.newLine();

            for (ExchangeRequest r : requests) {
                StringBuilder line = new StringBuilder();
                line.append(escape(r.getId())).append(",");
                line.append(escape(r.getBookId())).append(",");
                line.append(escape(r.getBookTitle())).append(",");
                line.append(String.format(Locale.US, "%.2f", r.getBookPrice())).append(",");
                line.append(escape(r.getSellerId())).append(",");
                line.append(escape(r.getSellerName())).append(",");
                line.append(escape(r.getRequesterId())).append(",");
                line.append(escape(r.getRequesterName())).append(",");
                line.append(escape(r.getRequestType())).append(",");
                line.append(escape(r.getOfferedBookId() != null ? r.getOfferedBookId() : "")).append(",");
                line.append(escape(r.getOfferedBookTitle())).append(",");
                line.append(escape(r.getRequestDate())).append(",");
                line.append(escape(r.getStatus().name())).append(",");
                line.append(r.getTrackingStep()).append(",");
                line.append(escape(r.getTrackingStepDescription())).append(",");
                line.append(escape(r.getNotes()));
                writer.write(line.toString());
                writer.newLine();
            }
            writer.flush();
        }

        return new ExportResult(file.getName(), file.getAbsolutePath(), requests.size(), file.length(), true, "OK");
    }

    // ==========================================
    // 4. Export Seller Ratings & Reviews
    // ==========================================

    /**
     * Exports peer reviews and ratings to CSV.
     */
    public ExportResult exportRatings(List<Rating> ratings, String targetPath) throws IOException {
        assertNotPersistencePath(targetPath);
        ensureExportDirectory();
        File file = new File(targetPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Rating_ID,Seller_ID,Reviewer_ID,Reviewer_Name,Rating_Score,Review_Comment,Review_Date");
            writer.newLine();

            for (Rating r : ratings) {
                StringBuilder line = new StringBuilder();
                line.append(escape(r.getId())).append(",");
                line.append(escape(r.getSellerId())).append(",");
                line.append(escape(r.getReviewerId())).append(",");
                line.append(escape(r.getReviewerName())).append(",");
                line.append(String.format(Locale.US, "%.1f", r.getRating())).append(",");
                line.append(escape(r.getComment())).append(",");
                line.append(escape(r.getDate()));
                writer.write(line.toString());
                writer.newLine();
            }
            writer.flush();
        }

        return new ExportResult(file.getName(), file.getAbsolutePath(), ratings.size(), file.length(), true, "OK");
    }

    // ==========================================
    // 5. Export Analytics & Summary Report
    // ==========================================

    /**
     * Exports comprehensive project analytics report to a structured multi-table CSV file.
     */
    public ExportResult exportAnalyticsReport(ProjectReportService reportService, String targetPath) throws IOException {
        assertNotPersistencePath(targetPath);
        ensureExportDirectory();
        File file = new File(targetPath);

        int recordCount = 0;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Header comments
            writer.write("# BOOKLOOP PLATFORM ANALYTICS & EXECUTIVE SUMMARY REPORT");
            writer.newLine();
            writer.write("# Generated At: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.newLine();
            writer.newLine();

            // Table 1: Core Platform Key Performance Indicators
            writer.write("SECTION 1: CORE PLATFORM KEY PERFORMANCE INDICATORS");
            writer.newLine();
            writer.write("Metric,Value");
            writer.newLine();
            writer.write("Total Registered Students," + reportService.getTotalRegisteredStudents());
            writer.newLine();
            writer.write("Total Textbooks Listed (All-Time)," + reportService.getTotalListedBooks());
            writer.newLine();
            writer.write("Active Textbooks Available," + reportService.getAvailableBooksCount());
            writer.newLine();
            writer.write("Books Available for Sale," + reportService.getBooksAvailableForSaleCount());
            writer.newLine();
            writer.write("Books Available for Exchange," + reportService.getBooksAvailableForExchangeCount());
            writer.newLine();
            writer.write("Dual Mode Books (Sale and Swap)," + reportService.getBooksAvailableBothCount());
            writer.newLine();
            writer.write("Total Catalog Valuation (INR)," + String.format(Locale.US, "%.2f", reportService.getTotalCatalogValuation()));
            writer.newLine();
            writer.write("Average Seller Reputation Score," + String.format(Locale.US, "%.2f", reportService.getAverageSellerRating()));
            writer.newLine();
            writer.write("Pending Requests," + reportService.getTotalPendingRequests());
            writer.newLine();
            writer.write("Accepted Requests," + reportService.getTotalAcceptedRequests());
            writer.newLine();
            writer.write("Completed Transactions," + reportService.getTotalCompletedRequests());
            writer.newLine();
            writer.write("Rejected Requests," + reportService.getTotalRejectedRequests());
            writer.newLine();
            recordCount += 12;

            writer.newLine();

            // Table 2: Semester Distribution (Native Array)
            writer.write("SECTION 2: TEXTBOOK DISTRIBUTION ACROSS SEMESTERS (1D ARRAY)");
            writer.newLine();
            writer.write("Semester,Total_Books");
            writer.newLine();
            int[] semDist = reportService.getSemesterDistribution();
            for (int s = 1; s <= 8; s++) {
                writer.write("Semester " + s + "," + semDist[s]);
                writer.newLine();
                recordCount++;
            }

            writer.newLine();

            // Table 3: Academic Subject Frequency Ranking
            writer.write("SECTION 3: MOST POPULAR ACADEMIC SUBJECTS (FREQUENCY RANKING)");
            writer.newLine();
            writer.write("Rank,Subject,Book_Count");
            writer.newLine();
            Map<String, Long> popularSubjects = reportService.getSubjectPopularityMap();
            int subjectRank = 1;
            for (Map.Entry<String, Long> e : popularSubjects.entrySet()) {
                writer.write(String.format("%d,%s,%d", subjectRank++, escape(e.getKey()), e.getValue()));
                writer.newLine();
                recordCount++;
            }

            writer.newLine();

            // Table 4: Active Sellers Leaderboard
            writer.write("SECTION 4: TOP ACTIVE SELLERS LEADERBOARD");
            writer.newLine();
            writer.write("Rank,Student_ID,Student_Name,College,Rating,Books_Listed,Books_Sold,Books_Exchanged,Activity_Score");
            writer.newLine();
            List<ProjectReportService.SellerActivity> topSellers = reportService.getMostActiveSellers(10);
            int sellerRank = 1;
            for (ProjectReportService.SellerActivity sa : topSellers) {
                writer.write(String.format(Locale.US, "%d,%s,%s,%s,%.1f,%d,%d,%d,%d",
                        sellerRank++,
                        escape(sa.getStudent().getId()),
                        escape(sa.getStudent().getName()),
                        escape(sa.getStudent().getCollege()),
                        sa.getRating(),
                        sa.getBooksListed(),
                        sa.getBooksSold(),
                        sa.getBooksExchanged(),
                        sa.getTotalActivityScore()));
                writer.newLine();
                recordCount++;
            }

            writer.flush();
        }

        return new ExportResult(file.getName(), file.getAbsolutePath(), recordCount, file.length(), true, "OK");
    }

    // ==========================================
    // CSV Escaping Helper
    // ==========================================

    /**
     * Standard RFC-4180 CSV escaping.
     * Escapes quotes by doubling them, and wraps the field in quotes if it contains commas, quotes, or newlines.
     */
    private static String escape(String value) {
        if (value == null) return "";
        String s = value;
        if (s.contains("\"")) {
            s = s.replace("\"", "\"\"");
        }
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s + "\"";
        }
        return s;
    }
}
