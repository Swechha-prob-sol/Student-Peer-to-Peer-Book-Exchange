package com.bookloop.model;

import com.bookloop.interfaces.Displayable;
import java.util.Objects;

/**
 * Abstract class representing a user interaction/transaction request.
 * Demonstrates:
 * - Abstraction
 * - Encapsulation
 * - Polymorphic method signatures
 */
public abstract class Request implements Displayable {
    private static int requestCounter = 300;

    private String id;
    private String requesterId;
    private String requesterName;
    private String sellerId;
    private String sellerName;
    private String requestDate;
    private RequestStatus status;
    private String notes;

    public Request() {
        this("REQ" + (++requestCounter), "USR1", "Requester", "USR2", "Seller",
                "2026-11-01", RequestStatus.PENDING, "Standard request");
    }

    public Request(String id, String requesterId, String requesterName,
                   String sellerId, String sellerName, String requestDate,
                   RequestStatus status, String notes) {
        this.id = id != null ? id.trim() : "REQ" + (++requestCounter);
        this.requesterId = requesterId;
        this.requesterName = requesterName != null ? requesterName : "Student";
        this.sellerId = sellerId;
        this.sellerName = sellerName != null ? sellerName : "Seller";
        this.requestDate = requestDate != null ? requestDate : "2026-11-01";
        this.status = status != null ? status : RequestStatus.PENDING;
        this.notes = notes != null ? notes : "";
    }

    // Abstract method to be implemented by concrete requests
    public abstract String getRequestType();
    public abstract boolean processStatusUpdate(RequestStatus newStatus);

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(id, request.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
