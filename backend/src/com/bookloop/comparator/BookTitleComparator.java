package com.bookloop.comparator;

import com.bookloop.model.Book;
import java.util.Comparator;

/**
 * Comparator to sort Books by Title alphabetically (A to Z).
 * Demonstrates Comparator interface implementation.
 */
public class BookTitleComparator implements Comparator<Book> {
    @Override
    public int compare(Book b1, Book b2) {
        if (b1 == null && b2 == null) return 0;
        if (b1 == null) return -1;
        if (b2 == null) return 1;
        int res = b1.getTitle().compareToIgnoreCase(b2.getTitle());
        return res != 0 ? res : b1.getId().compareTo(b2.getId());
    }
}
