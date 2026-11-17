package com.bookloop.interfaces;

/**
 * Interface for catalog items that support text-based and attribute-based searches.
 * Demonstrates abstraction and polymorphism.
 */
public interface Searchable {

    /**
     * Checks if the entity matches a free-form keyword across title, author, or description.
     *
     * @param keyword search keyword
     * @return true if match found, false otherwise
     */
    boolean matchesKeyword(String keyword);

    /**
     * Checks if the entity belongs to a specific academic subject or topic.
     *
     * @param subject academic subject name
     * @return true if subject matches
     */
    boolean matchesSubject(String subject);

    /**
     * Checks if the entity is designated for a specific college semester.
     *
     * @param semester academic semester (1-8)
     * @return true if semester matches
     */
    boolean matchesSemester(int semester);
}
