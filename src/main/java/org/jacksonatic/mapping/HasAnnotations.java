package org.jacksonatic.mapping;

import org.jacksonatic.annotation.Annotations;

import java.lang.annotation.Annotation;
import java.util.Optional;

public interface HasAnnotations {

    Annotations getAnnotations();

    default boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return getAnnotations().containsKey(annotationType);
    }

    default <T extends Annotation> Optional<T> getAnnotationOpt(Class<T> annotationType) {
        return (Optional<T>) getAnnotations().getOpt(annotationType);
    }
}
