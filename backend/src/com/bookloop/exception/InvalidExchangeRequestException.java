package com.bookloop.exception;

/**
 * Custom checked exception thrown when an exchange request is invalid.
 * Demonstrates:
 * - Custom checked exception (extends Exception)
 * - Domain-specific error information
 * - Exception message formatting with StringBuilder
 */
public class InvalidExchangeRequestException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String requestId;
    private final String reason;

    public InvalidExchangeRequestException(String reason) {
        super(reason);
        this.requestId = null;
        this.reason = reason;
    }

    public InvalidExchangeRequestException(String requestId, String reason) {
        super(formatMessage(requestId, reason));
        this.requestId = requestId;
        this.reason = reason;
    }

    public InvalidExchangeRequestException(String requestId, String reason, Throwable cause) {
        super(formatMessage(requestId, reason), cause);
        this.requestId = requestId;
        this.reason = reason;
    }

    /**
     * Uses StringBuilder to format the error message.
     * Demonstrates StringBuilder usage in exception handling.
     */
    private static String formatMessage(String requestId, String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid exchange request");
        if (requestId != null && !requestId.isEmpty()) {
            sb.append(" [").append(requestId).append("]");
        }
        sb.append(": ").append(reason);
        return sb.toString();
    }

    public String getRequestId() {
        return requestId;
    }

    public String getReason() {
        return reason;
    }
}
