package com.bookloop.model;

/**
 * Enum representing the current status of an exchange or purchase request.
 */
public enum RequestStatus {
    PENDING("Pending"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static RequestStatus fromString(String text) {
        if (text == null) return PENDING;
        for (RequestStatus status : RequestStatus.values()) {
            if (status.name().equalsIgnoreCase(text.replace(" ", "_")) ||
                status.displayName.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return PENDING;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
