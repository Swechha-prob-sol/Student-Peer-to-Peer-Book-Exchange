package com.bookloop.comparator;

import com.bookloop.model.Book;
import java.util.Comparator;

/**
 * Comparator to sort Books by Author name alphabetically (A to Z).
 */
public class BookAuthorComparator implements Comparator<Book> {
    @Override
    public int compare(Book b1, Book b2) {
        if (b1 == null && b2 == null) return 0;
        if (b1 == null) return -1;
        if (b2 == null) return 1;
        int res = b1.getAuthor().compareToIgnoreCase(b2.getAuthor());
        return res != 0 ? res : b1.getTitle().compareToIgnoreCase(b2.getTitle());
    }
}
