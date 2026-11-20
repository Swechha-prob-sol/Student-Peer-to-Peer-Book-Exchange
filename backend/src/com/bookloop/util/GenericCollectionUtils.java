package com.bookloop.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generic utility class for reusable collection operations.
 * Demonstrates:
 * - Generic class with type parameter <T>
 * - Generic methods with bounded type parameters
 * - Diamond operator (<>)
 * - Bounded types (<T extends Comparable<T>>)
 * - Wildcard usage (? extends T, ? super T)
 * - Type safety through generics
 * - Integration with functional interfaces and lambdas
 */
public class GenericCollectionUtils<T> {

    private final List<T> items;

    /**
     * Constructor using Diamond Operator.
     */
    public GenericCollectionUtils() {
        this.items = new ArrayList<>(); // Diamond operator
    }

    /**
     * Constructor with initial items.
     */
    public GenericCollectionUtils(List<T> initialItems) {
        this.items = new ArrayList<>(initialItems); // Diamond operator
    }

    /**
     * Adds an item with type safety.
     */
    public void add(T item) {
        if (item != null) {
            items.add(item);
        }
    }

    /**
     * Gets all items as unmodifiable list.
     */
    public List<T> getAll() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Gets item at index with bounds checking.
     */
    public T get(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + items.size());
        }
        return items.get(index);
    }

    public int size() {
        return items.size();
    }

    // ==========================================
    // Generic Methods
    // ==========================================

    /**
     * Generic method: Filters items matching a predicate.
     * Demonstrates generic method with Predicate<T> functional interface.
     */
    public List<T> filter(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Generic method: Transforms items using a mapping function.
     * Demonstrates generic method with two type parameters <T, R>.
     *
     * @param mapper function to transform T to R
     * @param <R>    the result type
     * @return list of transformed items
     */
    public <R> List<R> map(Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : items) {
            result.add(mapper.apply(item));
        }
        return result;
    }

    /**
     * Static generic method: Finds the maximum element in a list.
     * Demonstrates bounded type parameter <T extends Comparable<T>>.
     *
     * @param list list of comparable elements
     * @param <T>  type that must implement Comparable
     * @return the maximum element, or null if list is empty
     */
    public static <T extends Comparable<T>> T findMax(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        T max = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).compareTo(max) > 0) {
                max = list.get(i);
            }
        }
        return max;
    }

    /**
     * Static generic method: Finds the minimum element in a list.
     * Demonstrates bounded type parameter.
     */
    public static <T extends Comparable<T>> T findMin(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        T min = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).compareTo(min) < 0) {
                min = list.get(i);
            }
        }
        return min;
    }

    /**
     * Static generic method: Counts items matching a predicate.
     * Demonstrates wildcard with upper bound (? extends T).
     */
    public static <T> long countMatching(List<? extends T> list, Predicate<T> predicate) {
        long count = 0;
        for (T item : list) {
            if (predicate.test(item)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Static generic method: Copies elements from source to destination.
     * Demonstrates wildcard with lower bound (? super T) and upper bound (? extends T).
     */
    public static <T> void copyAll(List<? extends T> source, List<? super T> destination) {
        for (T item : source) {
            destination.add(item);
        }
    }

    /**
     * Static generic method: Creates a formatted summary string.
     * Demonstrates generic method with bounded type and String operations.
     */
    public static <T> String formatList(List<T> list, Function<T, String> formatter, String separator) {
        if (list == null || list.isEmpty()) return "(empty)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(separator);
            sb.append(formatter.apply(list.get(i)));
        }
        return sb.toString();
    }

    /**
     * Displays the contents of the collection using a custom formatter.
     */
    public void display(Function<T, String> formatter) {
        for (int i = 0; i < items.size(); i++) {
            System.out.printf("  %d. %s%n", (i + 1), formatter.apply(items.get(i)));
        }
    }
}
