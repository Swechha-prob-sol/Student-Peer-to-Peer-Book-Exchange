package com.bookloop.model;

import com.bookloop.interfaces.Displayable;
import com.bookloop.interfaces.Searchable;
import java.util.Objects;

/**
 * Abstract class representing a general catalog item.
 * Demonstrates:
 * - Abstraction (implements Displayable, Searchable)
 * - Inheritance base for specific commodities like Book
 * - Encapsulation (protected/private fields, getters/setters)
 * - Constructor chaining
 */
public abstract class Item implements Displayable, Searchable {
    private static int itemCounter = 200;

    private String id;
    private String title;
    private String description;
    private String category;

    /**
     * Default constructor.
     */
    public Item() {
        this("ITEM" + (++itemCounter), "Untitled Item", "General academic item", "General");
    }

    /**
     * Parameterized constructor.
     */
    public Item(String id, String title, String description, String category) {
        this.id = id != null ? id.trim() : "ITEM" + (++itemCounter);
        setTitle(title);
        this.description = description != null ? description.trim() : "";
        this.category = category != null ? category.trim() : "General";
    }

    // Abstract method for polymorphism
    public abstract String getItemType();

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = (title != null && !title.trim().isEmpty()) ? title.trim() : "Untitled";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public boolean matchesKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return true;
        String query = keyword.toLowerCase().trim();
        return title.toLowerCase().contains(query) ||
               description.toLowerCase().contains(query) ||
               category.toLowerCase().contains(query);
    }

    @Override
    public boolean matchesSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) return true;
        return category.equalsIgnoreCase(subject.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
