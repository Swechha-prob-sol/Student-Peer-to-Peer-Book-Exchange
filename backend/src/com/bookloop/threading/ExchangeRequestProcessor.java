package com.bookloop.threading;

import com.bookloop.data.DataStore;
import com.bookloop.model.ExchangeRequest;
import com.bookloop.model.RequestStatus;

import java.util.List;

/**
 * Orchestrator for the multithreaded exchange-request processing system.
 * Demonstrates:
 * - Thread creation using both Thread and Runnable
 * - start(), join(), sleep(), interrupt()
 * - Thread lifecycle management
 * - Daemon threads
 * - Thread priorities
 * - Producer-Consumer pattern coordination
 * - Race condition awareness and synchronization
 * - wait()/notify()/notifyAll() (via RequestQueue)
 * - Intrinsic locks and monitors
 */
public class ExchangeRequestProcessor {

    private final DataStore dataStore;
    private final RequestQueue requestQueue;

    public ExchangeRequestProcessor(DataStore dataStore) {
        this.dataStore = dataStore;
        this.requestQueue = new RequestQueue(5); // Bounded buffer of size 5
    }

    /**
     * Demonstrates multithreading with the exchange request processing system.
     * Shows Thread vs Runnable, daemon threads, priorities, join, etc.
     */
    @SuppressWarnings("deprecation")
    public void demonstrateMultithreading() {
        System.out.println("\n================================================================================");
        System.out.println("   MULTITHREADING DEMONSTRATION: Exchange Request Processing System");
        System.out.println("================================================================================");

        // Get pending requests for processing
        List<ExchangeRequest> allRequests = dataStore.getAllRequests();

        // Create sample requests for the demo
        ExchangeRequest[] demoRequests = allRequests.stream()
                .limit(4)
                .toArray(ExchangeRequest[]::new);

        if (demoRequests.length == 0) {
            System.out.println("[INFO] No requests available for multithreading demo.");
            return;
        }

        System.out.println("\n--- Thread Lifecycle & Creation ---");
        System.out.println("Main Thread: " + Thread.currentThread().getName() +
                " (ID: " + Thread.currentThread().getId() + ")");

        // ==========================================
        // 1. Create Producer (using Runnable interface)
        // ==========================================
        RequestProducer producer = new RequestProducer(requestQueue, demoRequests, "BookLoop-Producer");
        Thread producerThread = new Thread(producer, "Producer-Thread");
        // Thread state: NEW
        System.out.println("\n[1] Producer thread created (State: " + producerThread.getState() + ")");

        // ==========================================
        // 2. Create Consumer (extending Thread class)
        // ==========================================
        RequestConsumer consumer = new RequestConsumer(requestQueue, "BookLoop-Consumer");
        // Set thread priority (demonstrates thread priorities)
        consumer.setPriority(Thread.MAX_PRIORITY); // High priority for consumer
        producerThread.setPriority(Thread.NORM_PRIORITY); // Normal priority for producer
        System.out.println("[2] Consumer thread created (State: " + consumer.getState() + ")");
        System.out.println("    Producer Priority: " + producerThread.getPriority());
        System.out.println("    Consumer Priority: " + consumer.getPriority());

        // ==========================================
        // 3. Create Daemon thread for monitoring
        // ==========================================
        Thread monitorDaemon = new Thread(() -> {
            System.out.println("  [DAEMON] Monitor daemon started (isDaemon: " +
                    Thread.currentThread().isDaemon() + ")");
            int ticks = 0;
            try {
                while (!Thread.currentThread().isInterrupted() && ticks < 10) {
                    Thread.sleep(800);
                    ticks++;
                    System.out.printf("  [DAEMON] Tick %d - Queue status: %s%n",
                            ticks, requestQueue.getStatistics());
                }
            } catch (InterruptedException e) {
                System.out.println("  [DAEMON] Monitor daemon interrupted.");
            }
            System.out.println("  [DAEMON] Monitor daemon exiting.");
        }, "Monitor-Daemon");
        monitorDaemon.setDaemon(true); // Daemon thread - won't prevent JVM shutdown
        System.out.println("[3] Monitor daemon created (isDaemon: " + monitorDaemon.isDaemon() + ")");

        // ==========================================
        // 4. Start threads (demonstrates start() and state transitions)
        // ==========================================
        System.out.println("\n--- Starting Threads ---");
        monitorDaemon.start(); // Daemon starts first to monitor
        System.out.println("[4] Monitor daemon started (State: " + monitorDaemon.getState() + ")");

        producerThread.start(); // start() causes run() to execute in new thread
        System.out.println("[5] Producer started (State: " + producerThread.getState() + ")");

        consumer.start();
        System.out.println("[6] Consumer started (State: " + consumer.getState() + ")");

        // ==========================================
        // 5. Wait for threads to finish (demonstrates join())
        // ==========================================
        try {
            System.out.println("\n--- Waiting for Producer to finish (join) ---");
            producerThread.join(); // Main thread waits for producer to finish
            System.out.println("[7] Producer finished (State: " + producerThread.getState() + ")");

            // Give consumer time to finish processing
            Thread.sleep(1000);

            // Signal shutdown to the queue
            requestQueue.shutdown();

            System.out.println("\n--- Waiting for Consumer to finish (join with timeout) ---");
            consumer.join(5000); // join with timeout: wait at most 5 seconds
            System.out.println("[8] Consumer finished (State: " + consumer.getState() + ")");

            // Interrupt daemon if still running
            if (monitorDaemon.isAlive()) {
                monitorDaemon.interrupt(); // Demonstrates interrupt()
                System.out.println("[9] Monitor daemon interrupted (State: " + monitorDaemon.getState() + ")");
            }

        } catch (InterruptedException e) {
            System.out.println("[ERROR] Main thread interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // ==========================================
        // 6. Summary
        // ==========================================
        System.out.println("\n--- Multithreading Summary ---");
        System.out.println(requestQueue.getStatistics());
        System.out.println("Consumer processed: " + consumer.getProcessedCount() + " requests");
        System.out.println("Producer thread final state: " + producerThread.getState());
        System.out.println("Consumer thread final state: " + consumer.getState());
        System.out.println("Monitor daemon final state: " + monitorDaemon.getState());

        System.out.println("\n--- Race Condition Prevention ---");
        System.out.println("All shared data (RequestQueue) was protected using:");
        System.out.println("  - synchronized methods (intrinsic locks)");
        System.out.println("  - wait()/notifyAll() for producer-consumer coordination");
        System.out.println("  - volatile keyword for shutdown flag visibility");
        System.out.println("  - synchronized blocks for request status updates");
        System.out.println("================================================================================");
    }
}
