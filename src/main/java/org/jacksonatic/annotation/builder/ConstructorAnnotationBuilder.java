package org.jacksonatic.annotation.builder;

import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import org.jacksonatic.mapping.ConstructorMapping;
import org.jacksonatic.mapping.ParameterMapping;

import java.lang.annotation.Annotation;

public interface ConstructorAnnotationBuilder {

    boolean hasToBuild(AnnotatedWithParams annotatedWithParams, ConstructorMapping constructorMapping);

    Annotation buildMethodAnnotation(AnnotatedWithParams annotatedWithParams, ConstructorMapping constructorMapping);

    Annotation buildParamAnnotation(int index, ParameterMapping parameterMapping);
}
