package com.bookloop.service;

import com.bookloop.data.DataStore;
import com.bookloop.model.*;
import com.bookloop.util.ValidationUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service managing peer-to-peer book exchange and purchase transactions,
 * lifecycle state machines, and visual progress tracking.
 */
public class ExchangeService {
    private final DataStore store;
    private static int nextRequestIdNum = 307;

    public ExchangeService(DataStore store) {
        this.store = store;
    }

    /**
     * Sends an exchange or purchase request from a student to a book seller.
     */
    public ExchangeRequest createRequest(String bookId, Student requester, String requestType,
                                         String offeredBookId, String notes) {
        if (requester == null) {
            throw new IllegalArgumentException("Requester must be a valid registered student.");
        }
        Book book = store.getBook(bookId);
        if (book == null) {
            throw new IllegalArgumentException("The requested book does not exist: " + bookId);
        }
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is currently unavailable for requests: " + book.getStatus());
        }

        // Validate request parameters and ensure no self-trading
        ValidationUtil.validateExchangeRequest(bookId, book.getSellerId(), requester.getId(), requestType, offeredBookId);

        String offeredTitle = "";
        if ("Exchange".equalsIgnoreCase(requestType) && offeredBookId != null && !offeredBookId.trim().isEmpty()) {
            Book offered = store.getBook(offeredBookId.trim());
            if (offered == null) {
                throw new IllegalArgumentException("Offered exchange book does not exist: " + offeredBookId);
            }
            if (!offered.getSellerId().equalsIgnoreCase(requester.getId())) {
                throw new SecurityException("You can only offer books that belong to your personal listings.");
            }
            offeredTitle = offered.getTitle();
        }

        String requestId = "REQ" + (nextRequestIdNum++);
        String date = LocalDate.now().toString();

        ExchangeRequest request = new ExchangeRequest(
                requestId, book.getId(), book.getTitle(), book.getPrice(),
                book.getSellerId(), book.getSellerName(), requester.getId(),
                requester.getName(), requestType, offeredBookId, offeredTitle,
                date, RequestStatus.PENDING, ExchangeRequest.STEP_REQUEST_SENT, notes
        );

        store.addRequest(request);
        return request;
    }

    /**
     * Accepts a received request.
     */
    public boolean acceptRequest(String requestId, String sellerId) {
        ExchangeRequest request = store.getRequest(requestId);
        if (request == null) return false;
        if (!request.getSellerId().equalsIgnoreCase(sellerId)) {
            throw new SecurityException("Unauthorized: Only the seller can accept this request.");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be accepted.");
        }

        request.processStatusUpdate(RequestStatus.ACCEPTED);
        request.setTrackingStep(ExchangeRequest.STEP_ACCEPTED);

        // Mark book as Pending/Under Exchange
        Book book = store.getBook(request.getBookId());
        if (book != null) {
            book.setStatus("Pending");
        }

        store.logActivity("Request [" + requestId + "] accepted by seller " + request.getSellerName());
        return true;
    }

    /**
     * Rejects a received request.
     */
    public boolean rejectRequest(String requestId, String sellerId) {
        ExchangeRequest request = store.getRequest(requestId);
        if (request == null) return false;
        if (!request.getSellerId().equalsIgnoreCase(sellerId)) {
            throw new SecurityException("Unauthorized: Only the seller can reject this request.");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Only pending requests can be rejected.");
        }

        request.processStatusUpdate(RequestStatus.REJECTED);
        store.logActivity("Request [" + requestId + "] rejected by seller.");
        return true;
    }

    /**
     * Cancels an outgoing request.
     */
    public boolean cancelRequest(String requestId, String requesterId) {
        ExchangeRequest request = store.getRequest(requestId);
        if (request == null) return false;
        if (!request.getRequesterId().equalsIgnoreCase(requesterId)) {
            throw new SecurityException("Unauthorized: You can only cancel requests you created.");
        }
        if (request.getStatus() == RequestStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed transaction.");
        }

        request.processStatusUpdate(RequestStatus.CANCELLED);
        store.logActivity("Request [" + requestId + "] cancelled by requester.");
        return true;
    }

    /**
     * Advances the tracking stage (1 to 5).
     */
    public boolean advanceStage(String requestId) {
        ExchangeRequest request = store.getRequest(requestId);
        if (request == null) return false;

        boolean advanced = request.advanceTrackingStep();
        if (advanced && request.getTrackingStep() == ExchangeRequest.STEP_COMPLETED) {
            // Update book status and student stats
            Book book = store.getBook(request.getBookId());
            if (book != null) {
                book.setStatus("Exchange".equalsIgnoreCase(request.getRequestType()) ? "Exchanged" : "Sold");
            }
            Student seller = store.getStudent(request.getSellerId());
            if (seller != null) {
                if ("Exchange".equalsIgnoreCase(request.getRequestType())) {
                    seller.incrementBooksExchanged();
                } else {
                    seller.incrementBooksSold();
                }
            }
        }
        return advanced;
    }

    public List<ExchangeRequest> getReceivedRequests(String sellerId) {
        return store.getRequestsForSeller(sellerId);
    }

    public List<ExchangeRequest> getSentRequests(String requesterId) {
        return store.getRequestsByRequester(requesterId);
    }

    public List<ExchangeRequest> getAllRequests() {
        return store.getAllRequests();
    }

    public ExchangeRequest getRequestById(String id) {
        return store.getRequest(id);
    }
}
