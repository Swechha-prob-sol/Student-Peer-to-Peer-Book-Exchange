package com.bookloop.threading;

import com.bookloop.model.ExchangeRequest;
import com.bookloop.model.RequestStatus;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Thread-safe request queue implementing the Producer-Consumer pattern.
 * Demonstrates:
 * - synchronized methods and blocks
 * - Intrinsic locks (monitor locks)
 * - wait(), notify(), and notifyAll()
 * - Atomicity and visibility
 * - Race condition prevention
 * - Thread-safe data structure
 */
public class RequestQueue {

    private final Queue<ExchangeRequest> queue = new LinkedList<>();
    private final int maxCapacity;
    private volatile boolean shutdown = false; // volatile for visibility across threads
    private int totalProduced = 0;
    private int totalConsumed = 0;

    public RequestQueue(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Producer method: Adds a request to the queue.
     * Blocks if the queue is full (bounded buffer).
     * Demonstrates:
     * - synchronized method (intrinsic lock on 'this')
     * - wait() - releases lock and waits for space
     * - notifyAll() - wakes up all waiting consumer threads
     * - Atomicity of compound operations
     */
    public synchronized void enqueue(ExchangeRequest request) throws InterruptedException {
        // Wait while queue is at capacity (bounded buffer)
        while (queue.size() >= maxCapacity && !shutdown) {
            System.out.printf("  [PRODUCER] Queue full (%d/%d), waiting...%n", queue.size(), maxCapacity);
            wait(); // Releases intrinsic lock, waits for notify
        }

        if (shutdown) return;

        queue.add(request);
        totalProduced++;
        System.out.printf("  [PRODUCER] Enqueued: [%s] %s for \"%s\" (Queue: %d/%d)%n",
                request.getId(), request.getRequestType(),
                request.getBookTitle(), queue.size(), maxCapacity);

        notifyAll(); // Wake up consumers waiting for items
    }

    /**
     * Consumer method: Removes and returns a request from the queue.
     * Blocks if the queue is empty.
     * Demonstrates:
     * - synchronized method
     * - wait() - releases lock and waits for items
     * - notifyAll() - wakes up producer threads waiting for space
     */
    public synchronized ExchangeRequest dequeue() throws InterruptedException {
        // Wait while queue is empty
        while (queue.isEmpty() && !shutdown) {
            System.out.println("  [CONSUMER] Queue empty, waiting for requests...");
            wait(); // Releases intrinsic lock, waits for notify
        }

        if (shutdown && queue.isEmpty()) return null;

        ExchangeRequest request = queue.poll();
        if (request != null) {
            totalConsumed++;
        }

        notifyAll(); // Wake up producers waiting for space
        return request;
    }

    /**
     * Gets the current queue size.
     * Synchronized for thread-safe read.
     */
    public synchronized int size() {
        return queue.size();
    }

    /**
     * Checks if the queue is empty.
     */
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Initiates graceful shutdown.
     * Demonstrates volatile variable for cross-thread visibility.
     */
    public synchronized void shutdown() {
        this.shutdown = true;
        notifyAll(); // Wake up all waiting threads so they can exit
    }

    public boolean isShutdown() {
        return shutdown;
    }

    /**
     * Gets production/consumption statistics.
     * Demonstrates synchronized block for selective locking.
     */
    public String getStatistics() {
        // Synchronized block (instead of full method) for selective locking
        synchronized (this) {
            return String.format("Queue Stats: Produced=%d, Consumed=%d, Pending=%d, Capacity=%d",
                    totalProduced, totalConsumed, queue.size(), maxCapacity);
        }
    }
}
