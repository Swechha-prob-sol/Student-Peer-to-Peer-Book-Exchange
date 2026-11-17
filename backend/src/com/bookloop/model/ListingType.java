package com.bookloop.model;

/**
 * Enum representing book listing modalities.
 */
public enum ListingType {
    FOR_SALE("For Sale"),
    FOR_EXCHANGE("For Exchange"),
    BOTH("Both (Sale / Exchange)");

    private final String displayName;

    ListingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ListingType fromString(String text) {
        if (text == null) return BOTH;
        for (ListingType type : ListingType.values()) {
            if (type.name().equalsIgnoreCase(text.replace(" ", "_")) ||
                type.displayName.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return BOTH;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
