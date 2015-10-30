package org.jacksonatic.util;

import java.util.HashMap;
import java.util.Optional;

public class MyHashMap<K, V> extends HashMap<K, V> {

    public V getTyped(K key) {
        return super.get(key);
    }

    public boolean containsKeyTyped(K key) {
        return super.containsKey(key);
    }

    public V removeTyped(K key) {
        return super.remove(key);
    }

    public boolean containsValueTyped(V value) {
        return super.containsValue(value);
    }

    public V getTypedOrDefault(K key, V defaultValue) {
        return super.getOrDefault(key, defaultValue);
    }

    public boolean removeTyped(K key, V value) {
        return super.remove(key, value);
    }

    public Optional<V> getOpt(K key) {
        return Optional.ofNullable(get(key));
    }
}
