package com.bookloop.threading;

import com.bookloop.model.ExchangeRequest;
import com.bookloop.model.RequestStatus;

/**
 * Consumer thread that processes exchange requests from the shared queue.
 * Demonstrates:
 * - Extending Thread class (alternative to Runnable)
 * - Thread.sleep() for simulating processing time
 * - Thread lifecycle management
 * - Consumer role in Producer-Consumer pattern
 * - Thread interruption and graceful shutdown
 * - synchronized block for protecting shared resource
 */
public class RequestConsumer extends Thread {

    private final RequestQueue queue;
    private final String consumerName;
    private int processedCount = 0;

    /**
     * Constructor: Sets thread name and daemon status.
     * Demonstrates:
     * - Thread naming via super(name)
     * - Thread priorities
     */
    public RequestConsumer(RequestQueue queue, String consumerName) {
        super("Consumer-" + consumerName); // Set thread name
        this.queue = queue;
        this.consumerName = consumerName;
    }

    /**
     * Overrides Thread.run() - consumes and processes requests.
     * Demonstrates:
     * - Thread.run() override
     * - Thread lifecycle: RUNNABLE -> TIMED_WAITING -> TERMINATED
     * - Graceful shutdown via queue.isShutdown()
     */
    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        System.out.printf("[%s] Consumer thread started (Thread: %s, ID: %d, Priority: %d)%n",
                consumerName, getName(), getId(), getPriority());

        try {
            while (!queue.isShutdown() || !queue.isEmpty()) {
                // Check thread interruption
                if (isInterrupted()) {
                    System.out.printf("[%s] Consumer interrupted, stopping.%n", consumerName);
                    break;
                }

                ExchangeRequest request = queue.dequeue();
                if (request == null) break; // Queue shut down and empty

                processRequest(request);
                processedCount++;
            }
        } catch (InterruptedException e) {
            System.out.printf("[%s] Consumer interrupted: %s%n", consumerName, e.getMessage());
            Thread.currentThread().interrupt();
        }

        System.out.printf("[%s] Consumer finished. Total processed: %d%n",
                consumerName, processedCount);
    }

    /**
     * Simulates processing an exchange request.
     * Demonstrates synchronized block for thread-safe status updates.
     */
    private void processRequest(ExchangeRequest request) throws InterruptedException {
        System.out.printf("  [%s] Processing: [%s] %s request for \"%s\"...%n",
                consumerName, request.getId(), request.getRequestType(), request.getBookTitle());

        // Simulate processing time
        Thread.sleep(500);

        // Synchronized block to safely update request status (shared data protection)
        synchronized (request) {
            if (request.getStatus() == RequestStatus.PENDING) {
                // Auto-validate and log the processing
                System.out.printf("  [%s] Validated: [%s] - Request is valid, notifying seller.%n",
                        consumerName, request.getId());
            }
        }

        System.out.printf("  [%s] Completed processing: [%s] (%s)%n",
                consumerName, request.getId(), request.getStatus().getDisplayName());
    }

    public int getProcessedCount() {
        return processedCount;
    }
}
