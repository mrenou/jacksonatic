package org.jacksonatic.mapping;

import java.util.HashMap;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

public class ClassesMapping extends HashMap<Class<Object>, ClassMapping<Object>> {

    public ClassesMapping copyWithParentMapping(ClassesMapping parentMapping) {

        ClassesMapping newClassesMapping = this.entrySet().stream().collect(toMap(
                        e -> e.getKey(),
                        e -> e.getValue().copy(),
                        (v1, v2) -> {
                            throw new UnsupportedOperationException();
                        },
                        () -> new ClassesMapping())
        );
        parentMapping.values().stream()
                .map(classParentMapping -> Optional.ofNullable(newClassesMapping.get(classParentMapping.getType()))
                        .map(classMapping -> classMapping.copyWithParentMapping(classParentMapping))
                        .orElseGet(() -> classParentMapping.copy()))
                .forEach(classMapping -> newClassesMapping.put(classMapping.getType(), classMapping));
        return newClassesMapping;
    }
}
