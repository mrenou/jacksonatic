/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic.internal.util;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toMap;

public class CopyableMergeableHashMap<K, V extends Mergeable<V> & Copyable<V>> extends HashMap<K, V> {

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

    public CopyableMergeableHashMap<K, V> copy() {
        return  this.entrySet().stream().collect(toMap((Entry<K, V> e) -> {
            K key = e.getKey();
            return key;
        }, (Entry<K, V> e) -> {
            V copy = e.getValue();
            return copy;
        }, (V v1, V v2) -> {
            throw new UnsupportedOperationException();
        }, () -> new CopyableMergeableHashMap<>()));
    }

    public V mergeKeyWith(K key,V otherValue) {
        return put(key, Mergeable.merge(get(key), otherValue));
    }

    public CopyableMergeableHashMap<K, V> mergeWith(CopyableMergeableHashMap<K, V> map) {
        return MapUtil.merge(this, map);
    }

}
