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

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class MapUtil {

    public static <H extends Map<K, V>, K, V> H merge(H map1, H map2,
                                                Function<V, V> copyFunction,
                                                BiFunction<V, V, V> mergeFunction,
                                                Supplier<H> mapSupplier) {
        Stream<Map.Entry<K, V>> streamFromMap1WithMergedValues = map1.entrySet().stream()
                .map((entry1) -> {
                    V newValue;
                    if (map2.get(entry1.getKey()) != null) {
                        newValue = mergeFunction.apply(entry1.getValue(), map2.get(entry1.getKey()));
                    } else {
                        newValue = copyFunction.apply(entry1.getValue());
                    }
                    return new AbstractMap.SimpleEntry<>(entry1.getKey(), newValue);
                });

        Stream<Map.Entry<K, V>> streamFromMap2WithoutMergedValues = map2.entrySet().stream()
                .filter(entry2 -> !map1.containsKey(entry2.getKey()));

        return Stream.concat(streamFromMap1WithMergedValues, streamFromMap2WithoutMergedValues).collect(toMap(e -> e.getKey(), e -> e.getValue(), (v1, v2) -> v1, mapSupplier));
    }
}