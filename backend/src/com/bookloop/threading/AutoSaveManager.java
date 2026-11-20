package com.bookloop.threading;

import com.bookloop.data.DataStore;
import com.bookloop.persistence.FileStorageManager;

import java.io.IOException;

/**
 * Daemon thread that periodically auto-saves data to files.
 * Demonstrates:
 * - Daemon thread usage (auto-terminates when JVM exits)
 * - Thread.sleep() for periodic execution
 * - Background persistence without blocking the main application
 * - Exception handling in threads
 */
public class AutoSaveManager extends Thread {

    private final DataStore dataStore;
    private final long intervalMs;
    private volatile boolean running = true;

    /**
     * Creates an auto-save daemon thread.
     *
     * @param dataStore    data to save
     * @param intervalMs   save interval in milliseconds
     */
    @SuppressWarnings("this-escape")
    public AutoSaveManager(DataStore dataStore, long intervalMs) {
        super("AutoSave-Daemon");
        this.dataStore = dataStore;
        this.intervalMs = intervalMs;
        setDaemon(true); // Daemon thread: won't prevent JVM shutdown
        setPriority(Thread.MIN_PRIORITY); // Low priority: shouldn't interfere with main operations
    }

    @Override
    public void run() {
        System.out.println("[AUTO-SAVE] Background auto-save daemon started (interval: " +
                (intervalMs / 1000) + "s)");

        while (running && !isInterrupted()) {
            try {
                Thread.sleep(intervalMs);

                if (!running) break;

                // Perform auto-save
                synchronized (dataStore) {
                    FileStorageManager.saveStudents(dataStore.getAllStudents());
                    FileStorageManager.saveBooks(dataStore.getAllBooks());
                    FileStorageManager.saveRequests(dataStore.getAllRequests());
                    FileStorageManager.saveRatings(new java.util.ArrayList<>(dataStore.getAllRatings()));
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (IOException e) {
                System.err.println("[AUTO-SAVE] Error during auto-save: " + e.getMessage());
            }
        }

        System.out.println("[AUTO-SAVE] Background auto-save daemon stopped.");
    }

    /**
     * Signals the daemon to stop.
     */
    public void shutdown() {
        running = false;
        interrupt();
    }
}
