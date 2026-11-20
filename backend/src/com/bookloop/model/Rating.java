package com.bookloop.model;

import com.bookloop.interfaces.Displayable;
import java.util.Objects;

/**
 * Class representing a peer review and star rating given to a seller.
 * Demonstrates:
 * - Encapsulation (rating bounds checking between 1.0 and 5.0)
 * - Interface implementation (Displayable)
 * - Overridden toString, equals, hashCode
 */
public class Rating implements Displayable {
    private static int ratingCounter = 400;

    private String id;
    private String sellerId;
    private String reviewerId;
    private String reviewerName;
    private double rating; // 1.0 to 5.0
    private String comment;
    private String date;

    public Rating() {
        this("REV" + (++ratingCounter), "USR1", "USR2", "Peer Student", 5.0, "Great peer transaction!", "2026-11-01");
    }

    public Rating(String id, String sellerId, String reviewerId, String reviewerName,
                  double rating, String comment, String date) {
        this.id = id != null ? id.trim() : "REV" + (++ratingCounter);
        this.sellerId = sellerId;
        this.reviewerId = reviewerId;
        this.reviewerName = reviewerName != null ? reviewerName : "Verified Student";
        setRating(rating);
        this.comment = comment != null ? comment.trim() : "";
        this.date = date != null ? date : "2026-11-01";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public double getRating() {
        return rating;
    }

    public final void setRating(double rating) {
        // Enforce rating bounds between 1.0 and 5.0
        this.rating = Math.max(1.0, Math.min(5.0, rating));
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public void displayDetails() {
        System.out.printf("  ★ %.1f / 5.0 | By: %s on %s%n", rating, reviewerName, date);
        System.out.printf("    \"%s\"%n", comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating r = (Rating) o;
        return Objects.equals(id, r.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%.1f★] %s: \"%s\" (%s)", rating, reviewerName, comment, date);
    }
}
