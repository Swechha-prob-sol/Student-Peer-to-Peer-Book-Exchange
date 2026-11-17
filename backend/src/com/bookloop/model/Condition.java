package com.bookloop.model;

/**
 * Enum representing the physical condition of a textbook.
 */
public enum Condition {
    LIKE_NEW("Like New"),
    VERY_GOOD("Very Good"),
    GOOD("Good"),
    FAIR("Fair");

    private final String displayName;

    Condition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Condition fromString(String text) {
        if (text == null) return GOOD;
        for (Condition cond : Condition.values()) {
            if (cond.name().equalsIgnoreCase(text.replace(" ", "_")) ||
                cond.displayName.equalsIgnoreCase(text)) {
                return cond;
            }
        }
        return GOOD;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
