package com.bookloop.threading;

import com.bookloop.model.ExchangeRequest;
import com.bookloop.model.RequestStatus;

/**
 * Producer thread that generates exchange requests and adds them to the queue.
 * Demonstrates:
 * - Implementing Runnable interface
 * - Thread.sleep() for simulating work
 * - Thread interruption handling
 * - Producer role in Producer-Consumer pattern
 * - Thread lifecycle (NEW -> RUNNABLE -> TIMED_WAITING -> TERMINATED)
 */
public class RequestProducer implements Runnable {

    private final RequestQueue queue;
    private final ExchangeRequest[] requests;
    private final String producerName;

    public RequestProducer(RequestQueue queue, ExchangeRequest[] requests, String producerName) {
        this.queue = queue;
        this.requests = requests;
        this.producerName = producerName;
    }

    /**
     * Implements Runnable.run() - produces requests into the shared queue.
     * Demonstrates:
     * - run() method implementation
     * - Thread.sleep() for simulating production delay
     * - InterruptedException handling
     * - Thread interruption with interrupt()
     */
    @Override
    @SuppressWarnings("deprecation")
    public void run() {
        System.out.printf("[%s] Producer thread started (Thread: %s, ID: %d)%n",
                producerName, Thread.currentThread().getName(), Thread.currentThread().getId());

        try {
            for (int i = 0; i < requests.length; i++) {
                // Check if thread was interrupted
                if (Thread.currentThread().isInterrupted()) {
                    System.out.printf("[%s] Producer interrupted, stopping.%n", producerName);
                    break;
                }

                // Simulate time to create/validate a request
                Thread.sleep(300); // Demonstrates Thread.sleep()

                queue.enqueue(requests[i]);
                System.out.printf("[%s] Produced request %d/%d%n",
                        producerName, (i + 1), requests.length);
            }
        } catch (InterruptedException e) {
            System.out.printf("[%s] Producer interrupted during operation: %s%n",
                    producerName, e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupt status
        }

        System.out.printf("[%s] Producer finished. Total produced: %d%n",
                producerName, requests.length);
    }
}
