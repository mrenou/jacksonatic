package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jacksonatic.annotation.AnnotationBuilder;
import org.jacksonatic.annotation.Annotations;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

import static org.jacksonatic.annotation.JacksonaticJsonIgnore.jsonIgnore;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;

public class PropertyMapping {

    private String fieldName;

    private Annotations annotations;


    public static PropertyMapping property(String fieldName) {
        return new PropertyMapping(fieldName);
    }

    private PropertyMapping(String fieldName) {
        this(fieldName, new Annotations());
    }

    private PropertyMapping(String fieldName, Annotations annotations) {
        this.fieldName = fieldName;
        this.annotations = annotations;
    }

    public PropertyMapping add(AnnotationBuilder annotationBuilder) {
        annotations.add(annotationBuilder);
        return this;
    }

    public PropertyMapping ignore() {
        add(jsonIgnore());
        return this;
    }

    public PropertyMapping map() {
        mapTo(fieldName);
        return this;
    }

    public PropertyMapping mapTo(String mappedName) {
        add(jsonProperty(mappedName));
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMappedName() {
        return Optional.ofNullable(annotations.get(JsonProperty.class)).map(annotation -> ((JsonProperty)annotation).value()).orElse(fieldName);
    }

    public boolean isMapped() {
        return annotations.containsKey(JsonProperty.class) && !annotations.containsKey(JsonIgnore.class);
    }

    public boolean isIgnored() {
        return annotations.containsKey(JsonIgnore.class);
    }

    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return annotations;
    }

    PropertyMapping copy() {
        return new PropertyMapping(fieldName, this.annotations.copy());
    }

    PropertyMapping copyWithParentMapping(PropertyMapping parentMapping) {
        return new PropertyMapping(fieldName, this.annotations.size() == 0 ?
                parentMapping.annotations.copy():
                annotations.copy());
    }
}
