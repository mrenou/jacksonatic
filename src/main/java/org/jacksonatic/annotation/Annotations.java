package org.jacksonatic.annotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class Annotations extends HashMap<Class<? extends Annotation>, Annotation> {

    public void add(AnnotationBuilder annotationBuilder) {
        Annotation annotation = annotationBuilder.build();
        put(annotation.annotationType(), annotation);
    }

    public Annotations copy() {
        return entrySet().stream().collect(toMap(Map.Entry::getKey,
                Map.Entry::getValue,
                (v1, v2) -> {
                    throw new UnsupportedOperationException();
                },
                () -> new Annotations()
        ));
    }
}
