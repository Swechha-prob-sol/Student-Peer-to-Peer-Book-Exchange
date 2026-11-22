package com.bookloop.exception;

/**
 * Custom exception thrown when data validation fails in the BookLoop platform.
 * Demonstrates:
 * - Custom domain exception extending IllegalArgumentException (unchecked)
 * - Encapsulation of validation failure metadata (field name, invalid value, reason)
 * - Constructor overloading for flexible error reporting
 * - StringBuilder for formatted diagnostic messages
 */
public class ValidationException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private final String fieldName;
    private final Object invalidValue;
    private final String reason;

    public ValidationException(String reason) {
        super(reason);
        this.fieldName = null;
        this.invalidValue = null;
        this.reason = reason;
    }

    public ValidationException(String fieldName, String reason) {
        super(formatMessage(fieldName, null, reason));
        this.fieldName = fieldName;
        this.invalidValue = null;
        this.reason = reason;
    }

    public ValidationException(String fieldName, Object invalidValue, String reason) {
        super(formatMessage(fieldName, invalidValue, reason));
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.reason = reason;
    }

    public ValidationException(String fieldName, Object invalidValue, String reason, Throwable cause) {
        super(formatMessage(fieldName, invalidValue, reason), cause);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
        this.reason = reason;
    }

    /**
     * Formats diagnostic exception message using StringBuilder.
     */
    private static String formatMessage(String fieldName, Object invalidValue, String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append("Validation failed");
        if (fieldName != null && !fieldName.trim().isEmpty()) {
            sb.append(" on field '").append(fieldName).append("'");
        }
        if (invalidValue != null) {
            sb.append(" [value: \"").append(invalidValue).append("\"]");
        }
        sb.append(": ").append(reason);
        return sb.toString();
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }

    public String getReason() {
        return reason;
    }

    /**
     * Returns a structured multi-line diagnostic summary.
     */
    public String getDiagnosticSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Validation Error Report ===\n");
        sb.append("  Field:  ").append(fieldName != null ? fieldName : "N/A").append("\n");
        sb.append("  Value:  ").append(invalidValue != null ? invalidValue.toString() : "null").append("\n");
        sb.append("  Reason: ").append(reason != null ? reason : "Unknown validation failure").append("\n");
        return sb.toString();
    }
}
