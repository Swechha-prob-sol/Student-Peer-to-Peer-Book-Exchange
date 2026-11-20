package com.bookloop.util;

/**
 * Generic Pair class to hold two related values.
 * Demonstrates:
 * - Generic class with multiple type parameters <K, V>
 * - Type safety through generics
 * - Useful for returning paired results (e.g., Book + Seller, ID + Name)
 *
 * @param <K> the type of the first element (key)
 * @param <V> the type of the second element (value)
 */
public class GenericPair<K, V> {
    private final K key;
    private final V value;

    public GenericPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericPair<?, ?> pair = (GenericPair<?, ?>) o;
        return java.util.Objects.equals(key, pair.key) &&
               java.util.Objects.equals(value, pair.value);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(key, value);
    }
}
