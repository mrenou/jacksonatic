package org.jacksonatic.mapping;

import java.util.HashMap;

import static java.util.stream.Collectors.toMap;

public class ClassesMapping extends HashMap<Class<Object>, ClassMapping<Object>> {

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
