package com.bookloop.service;

import com.bookloop.data.DataStore;
import com.bookloop.model.Rating;
import com.bookloop.model.Student;
import com.bookloop.util.ValidationUtil;
import java.time.LocalDate;
import java.util.List;

/**
 * Service managing peer ratings, trust scores, and student reviews.
 */
public class RatingService {
    private final DataStore store;
    private static int nextRatingIdNum = 406;

    public RatingService(DataStore store) {
        this.store = store;
    }

    /**
     * Submits a peer rating and review for a seller.
     */
    public Rating rateSeller(String sellerId, Student reviewer, double score, String comment) {
        if (reviewer == null) {
            throw new IllegalArgumentException("Reviewer must be a registered student.");
        }
        Student seller = store.getStudent(sellerId);
        if (seller == null) {
            throw new IllegalArgumentException("Seller not found with ID: " + sellerId);
        }

        // Validate rating rules (bounds, no self-rating, comment length)
        ValidationUtil.validateRating(sellerId, reviewer.getId(), score, comment);

        String ratingId = "REV" + (nextRatingIdNum++);
        String date = LocalDate.now().toString();

        Rating rating = new Rating(ratingId, seller.getId(), reviewer.getId(),
                reviewer.getName(), score, comment, date);

        store.addRating(rating);
        return rating;
    }

    public List<Rating> getReviewsForSeller(String sellerId) {
        return store.getRatingsForSeller(sellerId);
    }

    public List<Rating> getAllReviews() {
        return store.getAllRatings();
    }
}
