package com.bookloop.comparator;

import com.bookloop.model.Book;
import java.util.Comparator;

/**
 * Comparator to sort Books by Price in ascending order (Low to High).
 */
public class BookPriceAscendingComparator implements Comparator<Book> {
    @Override
    public int compare(Book b1, Book b2) {
        if (b1 == null && b2 == null) return 0;
        if (b1 == null) return -1;
        if (b2 == null) return 1;
        int priceCompare = Double.compare(b1.getPrice(), b2.getPrice());
        return priceCompare != 0 ? priceCompare : b1.getTitle().compareToIgnoreCase(b2.getTitle());
    }
}
