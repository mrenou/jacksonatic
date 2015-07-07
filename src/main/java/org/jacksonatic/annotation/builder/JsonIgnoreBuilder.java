package org.jacksonatic.annotation.builder;

import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.jacksonatic.annotation.JacksonaticJsonIgnore;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.PropertyMapping;

import java.lang.annotation.Annotation;

public class JsonIgnoreBuilder implements FieldAnnotationBuilder {

    @Override
    public boolean hasToBuild(AnnotatedField annotatedField, ClassMapping classMapping, PropertyMapping propertyMapping) {
        return ((!classMapping.allPropertiesAreMapped() && !propertyMapping.isMapped())
                || propertyMapping.isIgnored());
    }

    @Override
    public Annotation build(AnnotatedField annotatedField, ClassMapping classMapping, PropertyMapping propertyMapping) {
        return new JacksonaticJsonIgnore(true);
    }
}
