package com.bookloop.model;

/**
 * Concrete class representing a College Student.
 * Demonstrates:
 * - Inheritance (extends User)
 * - super keyword
 * - Method overriding (@Override displayProfile, displayDetails, toString)
 * - Method overloading (updateRating methods)
 * - Encapsulation (validated fields: semester, rating)
 */
public class Student extends User {
    private String college;
    private String department;
    private int semester;
    private double rating;
    private int reviewCount;
    private int booksSold;
    private int booksExchanged;

    /**
     * Default constructor.
     */
    public Student() {
        super();
        this.college = "Delhi Technological University (DTU)";
        this.department = "Computer Science and Engineering";
        this.semester = 1;
        this.rating = 5.0;
        this.reviewCount = 0;
        this.booksSold = 0;
        this.booksExchanged = 0;
    }

    /**
     * Parameterized constructor without initial stats.
     */
    public Student(String id, String name, String email, String phone, String joinedDate,
                   String college, String department, int semester) {
        this(id, name, email, phone, joinedDate, college, department, semester, 5.0, 0, 0, 0);
    }

    /**
     * Full parameterized constructor.
     * Demonstrates super() call to initialize base class members.
     */
    public Student(String id, String name, String email, String phone, String joinedDate,
                   String college, String department, int semester, double rating,
                   int reviewCount, int booksSold, int booksExchanged) {
        super(id, name, email, phone, joinedDate);
        this.college = college != null ? college.trim() : "Unknown College";
        this.department = department != null ? department.trim() : "General";
        setSemester(semester);
        this.rating = Math.max(1.0, Math.min(5.0, rating));
        this.reviewCount = Math.max(0, reviewCount);
        this.booksSold = Math.max(0, booksSold);
        this.booksExchanged = Math.max(0, booksExchanged);
    }

    // Getters and Setters

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college != null ? college.trim() : "Unknown College";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null ? department.trim() : "General";
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        if (semester >= 1 && semester <= 8) {
            this.semester = semester;
        } else {
            this.semester = 1; // Default fallback
        }
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = Math.max(1.0, Math.min(5.0, rating));
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = Math.max(0, reviewCount);
    }

    public int getBooksSold() {
        return booksSold;
    }

    public void setBooksSold(int booksSold) {
        this.booksSold = booksSold;
    }

    public int getBooksExchanged() {
        return booksExchanged;
    }

    public void setBooksExchanged(int booksExchanged) {
        this.booksExchanged = booksExchanged;
    }

    public void incrementBooksSold() {
        this.booksSold++;
    }

    public void incrementBooksExchanged() {
        this.booksExchanged++;
    }

    // Method Overloading: Method 1
    /**
     * Updates seller rating with a new score.
     * Computes the cumulative moving average.
     *
     * @param newScore score between 1.0 and 5.0
     */
    public void addRating(double newScore) {
        double clampedScore = Math.max(1.0, Math.min(5.0, newScore));
        double totalScore = (this.rating * this.reviewCount) + clampedScore;
        this.reviewCount++;
        this.rating = Math.round((totalScore / this.reviewCount) * 10.0) / 10.0;
    }

    // Method Overloading: Method 2 (accepts integer stars)
    /**
     * Overloaded method accepting integer star rating.
     *
     * @param starRating rating 1 to 5
     */
    public void addRating(int starRating) {
        this.addRating((double) starRating);
    }

    @Override
    public void displayProfile() {
        printDivider();
        System.out.printf("STUDENT PROFILE: %s (ID: %s)%n", getName(), getId());
        System.out.printf("College:    %s%n", college);
        System.out.printf("Department: %s | Semester: %d%n", department, semester);
        System.out.printf("Email:      %s | Phone: %s%n", getEmail(), getPhone());
        System.out.printf("Seller Rep: %.1f ★ (%d reviews) | Sold: %d | Exchanged: %d%n",
                rating, reviewCount, booksSold, booksExchanged);
        printDivider();
    }

    @Override
    public void displayDetails() {
        displayProfile();
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s Sem %d [Rating: %.1f★]",
                getName(), getId(), department, semester, rating);
    }
}
