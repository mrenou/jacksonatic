package org.jacksonatic.annotation.builder;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.jacksonatic.annotation.JacksonaticJsonProperty;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.PropertyMapping;

import java.lang.annotation.Annotation;

public class JsonPropertyBuilder implements FieldAnnotationBuilder {

    @Override
    public boolean hasToBuild(AnnotatedField annotatedField, ClassMapping classMapping, PropertyMapping propertyMapping) {
        return propertyMapping.hasMappedName();
    }

    @Override
    public Annotation build(AnnotatedField annotatedField, ClassMapping classMapping, PropertyMapping propertyMapping) {
        return new JacksonaticJsonProperty(propertyMapping.getMappedName(), false, JsonProperty.INDEX_UNKNOWN, "");
    }
}
