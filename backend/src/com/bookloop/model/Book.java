package com.bookloop.model;

import java.util.Objects;

/**
 * Concrete class representing an academic textbook in BookLoop.
 * Demonstrates:
 * - Inheritance (extends Item)
 * - super(...) constructor invocation
 * - Natural sorting via Comparable<Book>
 * - Method overriding (@Override matchesKeyword, matchesSubject, matchesSemester, displayDetails, compareTo)
 * - Method overloading (isAffordable methods)
 * - Encapsulation (price validation, semester validation, condition/listingType enums)
 */
public class Book extends Item implements Comparable<Book> {
    private String author;
    private String isbn;
    private String subject;
    private int semester;
    private String edition;
    private Condition condition;
    private double price;
    private ListingType listingType;
    private String preferredExchange;
    private String sellerId;
    private String sellerName;
    private String sellerCollege;
    private double sellerRating;
    private String status; // "Active", "Pending", "Sold", "Exchanged"
    private String dateListed;

    /**
     * Default constructor.
     */
    public Book() {
        super();
        this.author = "Unknown Author";
        this.isbn = "N/A";
        this.subject = "Computer Science";
        this.semester = 1;
        this.edition = "1st Edition";
        this.condition = Condition.GOOD;
        this.price = 0.0;
        this.listingType = ListingType.FOR_EXCHANGE;
        this.preferredExchange = "Any standard textbook";
        this.sellerId = "USR100";
        this.sellerName = "Admin";
        this.sellerCollege = "DTU";
        this.sellerRating = 5.0;
        this.status = "Active";
        this.dateListed = "2026-11-01";
    }

    /**
     * Simplified constructor for basic book listing.
     */
    public Book(String id, String title, String author, String subject, int semester, double price,
                ListingType listingType, String sellerId, String sellerName) {
        this(id, title, author, "ISBN-GEN", subject, "Computer Science", semester, "Latest Edition",
                Condition.GOOD, price, listingType, "Open to relevant academic books",
                "Standard college textbook", sellerId, sellerName, "College Campus", 5.0, "Active", "2026-11-01");
    }

    /**
     * Full parameterized constructor.
     * Demonstrates super() call to initialize Item base fields.
     */
    public Book(String id, String title, String author, String isbn, String subject,
                String category, int semester, String edition, Condition condition,
                double price, ListingType listingType, String preferredExchange,
                String description, String sellerId, String sellerName,
                String sellerCollege, double sellerRating, String status, String dateListed) {
        super(id, title, description, category);
        this.author = author != null ? author.trim() : "Unknown Author";
        this.isbn = isbn != null ? isbn.trim() : "N/A";
        this.subject = subject != null ? subject.trim() : "General";
        setSemester(semester);
        this.edition = edition != null ? edition.trim() : "Standard Edition";
        this.condition = condition != null ? condition : Condition.GOOD;
        setPrice(price);
        this.listingType = listingType != null ? listingType : ListingType.BOTH;
        this.preferredExchange = preferredExchange != null ? preferredExchange.trim() : "";
        this.sellerId = sellerId != null ? sellerId.trim() : "";
        this.sellerName = sellerName != null ? sellerName.trim() : "Fellow Student";
        this.sellerCollege = sellerCollege != null ? sellerCollege.trim() : "University";
        this.sellerRating = Math.max(1.0, Math.min(5.0, sellerRating));
        this.status = status != null ? status : "Active";
        this.dateListed = dateListed != null ? dateListed : "2026-11-01";
    }

    @Override
    public String getItemType() {
        return "Academic Textbook";
    }

    // Getters and Setters with Encapsulation

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author != null ? author.trim() : "Unknown Author";
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject != null ? subject.trim() : "General";
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        if (semester >= 1 && semester <= 8) {
            this.semester = semester;
        } else {
            this.semester = 1;
        }
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition != null ? condition : Condition.GOOD;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = Math.max(0.0, price);
    }

    public ListingType getListingType() {
        return listingType;
    }

    public void setListingType(ListingType listingType) {
        this.listingType = listingType != null ? listingType : ListingType.BOTH;
    }

    public String getPreferredExchange() {
        return preferredExchange;
    }

    public void setPreferredExchange(String preferredExchange) {
        this.preferredExchange = preferredExchange;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerCollege() {
        return sellerCollege;
    }

    public void setSellerCollege(String sellerCollege) {
        this.sellerCollege = sellerCollege;
    }

    public double getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(double sellerRating) {
        this.sellerRating = sellerRating;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateListed() {
        return dateListed;
    }

    public void setDateListed(String dateListed) {
        this.dateListed = dateListed;
    }

    public boolean isAvailable() {
        return "Active".equalsIgnoreCase(status);
    }

    // Method Overloading Example 1: Check affordability against maximum budget
    public boolean isAffordable(double maxBudget) {
        return this.price <= maxBudget;
    }

    // Method Overloading Example 2: Check affordability within price range
    public boolean isAffordable(double minPrice, double maxPrice) {
        return this.price >= minPrice && this.price <= maxPrice;
    }

    // Searchable implementation with Method Overriding

    @Override
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return true;
        String query = keyword.toLowerCase().trim();
        return super.matchesKeyword(keyword) ||
               author.toLowerCase().contains(query) ||
               isbn.toLowerCase().contains(query) ||
               subject.toLowerCase().contains(query);
    }

    @Override
    public boolean matchesSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) return true;
        return this.subject.equalsIgnoreCase(subject.trim()) ||
               getCategory().equalsIgnoreCase(subject.trim());
    }

    @Override
    public boolean matchesSemester(int semester) {
        return this.semester == semester;
    }

    // Comparable<Book> implementation for Natural Sorting (Alphabetical by title)
    @Override
    public int compareTo(Book other) {
        if (other == null) return 1;
        int titleCompare = this.getTitle().compareToIgnoreCase(other.getTitle());
        if (titleCompare != 0) {
            return titleCompare;
        }
        // Secondary tie-breaker by ID
        return this.getId().compareTo(other.getId());
    }

    @Override
    public void displayDetails() {
        printDivider();
        System.out.printf("BOOK DETAILS: [%s] %s%n", getId(), getTitle());
        System.out.printf("Author:      %s%n", author);
        System.out.printf("Subject:     %s | Category: %s | Semester: %d%n", subject, getCategory(), semester);
        System.out.printf("Edition:     %s | ISBN: %s%n", edition, isbn);
        System.out.printf("Condition:   %-12s | Status: %s%n", condition.getDisplayName(), status);
        System.out.printf("Listing:     %-12s | Price: ₹%.2f%n", listingType.getDisplayName(), price);
        if (listingType != ListingType.FOR_SALE && !preferredExchange.isEmpty()) {
            System.out.printf("Looking For: %s%n", preferredExchange);
        }
        System.out.printf("Seller:      %s (%s) [Rating: %.1f ★]%n", sellerName, sellerCollege, sellerRating);
        System.out.printf("About Book:  %s%n", getDescription());
        printDivider();
    }

    @Override
    public String toString() {
        return String.format("[%s] \"%s\" by %s | Sem %d | ₹%.0f | %s | %s [%s]",
                getId(), getTitle(), author, semester, price, listingType.getDisplayName(),
                condition.getDisplayName(), status);
    }
}
