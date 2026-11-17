package com.bookloop.interfaces;

/**
 * Interface representing any entity capable of displaying formatted details
 * in the console UI. Demonstrates Java interfaces and default methods.
 */
public interface Displayable {

    /**
     * Abstract method to print detailed formatted information.
     */
    void displayDetails();

    /**
     * Default method providing a reusable visual divider line.
     * Demonstrates Java interface default methods.
     */
    default void printDivider() {
        System.out.println("--------------------------------------------------------------------------------");
    }

    /**
     * Default method providing a section header.
     */
    default void printHeader(String title) {
        System.out.println("\n=== " + title + " ===");
    }
}
