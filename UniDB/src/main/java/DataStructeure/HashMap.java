package DataStructeure;

import Shared.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class HashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Bucket<K, V>[] table;
    private int size;
    private int capacity;

    public HashMap() {
        this(DEFAULT_CAPACITY);
    }

    public HashMap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.table = new Bucket[capacity];
        this.size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode() % capacity);
    }

    public void put(K key, V value) {
        int index = hash(key);

        if (table[index] == null) {
            table[index] = new Bucket<>(key, value);
            size++;
            if ((double) size / capacity > LOAD_FACTOR) {
                rehash();
            }
        } else {
            int i = index;
            while (table[i] != null && !table[i].key.equals(key)) {
                i = (i + 1) % capacity;
                if (i == index) {
                    rehash();
                    return;
                }
            }
            table[i] = new Bucket<>(key, value);
        }
    }

    public SearchResult get(K key) {
        int index = hash(key);
        int count = 0 ;
        int scanned = 0;
        int i = index;
        while (table[i] != null) {
            scanned ++;
            if (table[i].key.equals(key)) {
                count++;
            }
            i = (i + 1) % capacity;
            if (i == index) {
                return new SearchResult(count,0.0,scanned);
            }
        }

        return new SearchResult(count, 0.0,scanned);
    }
    public V getValue(K key) {
        int index = hash(key);
        int i = index;
        while (table[i] != null) {
            if (table[i].key.equals(key)) {
                return table[i].value;
            }
            i = (i + 1) % capacity;
            if (i == index) {
                return null;
            }
        }

        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public void remove(K key) {
        int index = hash(key);

        int i = index;
        while (table[i] != null) {
            if (table[i].key.equals(key)) {
                table[i] = null;
                size--;
                rehash();
                return;
            }
            i = (i + 1) % capacity;
            if (i == index) {
                return; // Key not found
            }
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void rehash() {
        int newCapacity = capacity * 2;
        Bucket<K, V>[] oldTable = table;
        capacity = newCapacity;
        table = new Bucket[capacity];
        size = 0; // Reset size

        for (Bucket<K, V> bucket : oldTable) {
            if (bucket != null) {
                put(bucket.key, bucket.value);
            }
        }
    }

    private static class Bucket<K, V> {
        K key;
        V value;

        Bucket(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
