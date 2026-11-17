package com.bookloop.model;

/**
 * Concrete class representing a Book Exchange or Purchase transaction request.
 * Demonstrates:
 * - Inheritance (extends Request)
 * - super(...) call
 * - Method overriding (@Override processStatusUpdate, displayDetails, toString)
 * - State tracking logic (trackingStep 1 to 5)
 */
public class ExchangeRequest extends Request implements Comparable<ExchangeRequest> {
    private String bookId;
    private String bookTitle;
    private double bookPrice;
    private String requestType; // "Exchange" or "Purchase"
    private String offeredBookId;
    private String offeredBookTitle;
    private int trackingStep; // 1 to 5

    public static final int STEP_REQUEST_SENT = 1;
    public static final int STEP_ACCEPTED = 2;
    public static final int STEP_CONTACT_ESTABLISHED = 3;
    public static final int STEP_COMPLETED = 4;
    public static final int STEP_REVIEW_SUBMITTED = 5;

    public ExchangeRequest() {
        super();
        this.bookId = "BK1";
        this.bookTitle = "Sample Book";
        this.bookPrice = 300.0;
        this.requestType = "Exchange";
        this.offeredBookId = null;
        this.offeredBookTitle = "";
        this.trackingStep = STEP_REQUEST_SENT;
    }

    public ExchangeRequest(String id, String bookId, String bookTitle, double bookPrice,
                           String sellerId, String sellerName, String requesterId,
                           String requesterName, String requestType, String offeredBookId,
                           String offeredBookTitle, String requestDate, RequestStatus status,
                           int trackingStep, String notes) {
        super(id, requesterId, requesterName, sellerId, sellerName, requestDate, status, notes);
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
        this.requestType = requestType != null ? requestType : "Purchase";
        this.offeredBookId = offeredBookId;
        this.offeredBookTitle = offeredBookTitle != null ? offeredBookTitle : "";
        setTrackingStep(trackingStep);
    }

    @Override
    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getOfferedBookId() {
        return offeredBookId;
    }

    public void setOfferedBookId(String offeredBookId) {
        this.offeredBookId = offeredBookId;
    }

    public String getOfferedBookTitle() {
        return offeredBookTitle;
    }

    public void setOfferedBookTitle(String offeredBookTitle) {
        this.offeredBookTitle = offeredBookTitle;
    }

    public int getTrackingStep() {
        return trackingStep;
    }

    public void setTrackingStep(int trackingStep) {
        if (trackingStep >= 1 && trackingStep <= 5) {
            this.trackingStep = trackingStep;
        } else {
            this.trackingStep = 1;
        }
    }

    public String getTrackingStepDescription() {
        switch (trackingStep) {
            case 1: return "1/5 Request Sent";
            case 2: return "2/5 Request Accepted";
            case 3: return "3/5 Contact Established";
            case 4: return "4/5 Exchange / Sale Completed";
            case 5: return "5/5 Review Submitted";
            default: return "Pending";
        }
    }

    /**
     * Advances to the next tracking step if eligible.
     *
     * @return true if successfully advanced
     */
    public boolean advanceTrackingStep() {
        if (getStatus() == RequestStatus.REJECTED || getStatus() == RequestStatus.CANCELLED) {
            return false;
        }
        if (this.trackingStep < STEP_REVIEW_SUBMITTED) {
            this.trackingStep++;
            if (this.trackingStep >= STEP_COMPLETED) {
                setStatus(RequestStatus.COMPLETED);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean processStatusUpdate(RequestStatus newStatus) {
        if (newStatus == null) return false;
        setStatus(newStatus);
        if (newStatus == RequestStatus.ACCEPTED && this.trackingStep < STEP_ACCEPTED) {
            this.trackingStep = STEP_ACCEPTED;
        } else if (newStatus == RequestStatus.COMPLETED) {
            this.trackingStep = STEP_COMPLETED;
        }
        return true;
    }

    // Natural ordering for requests (by request date descending / ID)
    @Override
    public int compareTo(ExchangeRequest other) {
        if (other == null) return 1;
        int dateCompare = other.getRequestDate().compareTo(this.getRequestDate());
        if (dateCompare != 0) return dateCompare;
        return this.getId().compareTo(other.getId());
    }

    @Override
    public void displayDetails() {
        printDivider();
        System.out.printf("TRANSACTION REQUEST: [%s] Type: %s%n", getId(), requestType);
        System.out.printf("Target Book:   [%s] \"%s\" (Price: ₹%.0f)%n", bookId, bookTitle, bookPrice);
        System.out.printf("Seller:        %s (ID: %s)%n", getSellerName(), getSellerId());
        System.out.printf("Requester:     %s (ID: %s)%n", getRequesterName(), getRequesterId());
        if ("Exchange".equalsIgnoreCase(requestType) && offeredBookTitle != null && !offeredBookTitle.isEmpty()) {
            System.out.printf("Offered Book:  [%s] \"%s\"%n",
                    offeredBookId != null ? offeredBookId : "N/A", offeredBookTitle);
        }
        System.out.printf("Status:        %-10s | Stage: %s%n", getStatus().getDisplayName(), getTrackingStepDescription());
        System.out.printf("Request Date:  %s%n", getRequestDate());
        if (getNotes() != null && !getNotes().isEmpty()) {
            System.out.printf("Message/Notes: \"%s\"%n", getNotes());
        }
        printDivider();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s Request for \"%s\" | Requester: %s -> Seller: %s | Status: %s (%s)",
                getId(), requestType, bookTitle, getRequesterName(), getSellerName(),
                getStatus().getDisplayName(), getTrackingStepDescription());
    }
}
