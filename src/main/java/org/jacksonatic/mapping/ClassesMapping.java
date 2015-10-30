package org.jacksonatic.mapping;

import org.jacksonatic.util.MyHashMap;

import static java.util.stream.Collectors.toMap;

/**
 * Class mapping map
 */
public class ClassesMapping extends MyHashMap<Class<Object>, ClassMapping<Object>> {

    public ClassesMapping copy() {
        return this.entrySet().stream().collect(toMap(
                        e -> e.getKey(),
                        e -> e.getValue().copy(),
                        (v1, v2) -> {
                            throw new UnsupportedOperationException();
                        },
                        () -> new ClassesMapping())
        );
    }

}
