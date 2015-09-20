package org.jacksonatic.annotation;

import java.lang.annotation.Annotation;

/**
 * Build annotation implementation
 */
public interface AnnotationBuilder {

    Annotation build();
}
