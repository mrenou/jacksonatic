package org.jacksonatic.annotation.builder;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.PropertyMapping;

import java.lang.annotation.Annotation;

public interface FieldAnnotationBuilder {

    boolean hasToBuild(AnnotatedField annotatedField, ClassMapping classMapping, PropertyMapping propertyMapping);

    Annotation build(AnnotatedField annotatedField, ClassMapping classMapping, PropertyMapping propertyMapping);

}
