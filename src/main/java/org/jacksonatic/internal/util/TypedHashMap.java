/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.stream.Collectors.toMap;

public class TypedHashMap<K, V> extends HashMap<K, V> {

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

    public TypedHashMap<K, V> copy(Function<V, V> copyFunction) {
        return copy (copyFunction, () -> new TypedHashMap<>());
    }

    public <H extends TypedHashMap<K, V>> H copy(Function<V, V> copyFunction, Supplier<H> hashMapSupplier) {
        return this.entrySet().stream().collect(toMap(e -> e.getKey(), e -> copyFunction.apply(e.getValue()), (v1, V2) -> {
            throw new UnsupportedOperationException();
        }, hashMapSupplier));
    }

    public TypedHashMap<K, V> mergeWith(TypedHashMap<K, V> map,
                                        Function<V, V> copyFunction,
                                        BiFunction<V, V, V> mergeFunction) {
        return MapUtil.merge(this, map, copyFunction, mergeFunction, () -> new TypedHashMap<>());
    }
}
