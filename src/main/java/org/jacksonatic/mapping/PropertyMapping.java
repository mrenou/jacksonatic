package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jacksonatic.annotation.JacksonaticJsonIgnore;
import org.jacksonatic.annotation.JacksonaticJsonProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

public class PropertyMapping {

    private Field field;

    private Map<Class<? extends Annotation>, Annotation> annotations;

    public PropertyMapping(Field field) {
        this(field, new HashMap<>());
    }

    PropertyMapping(Field field, Map<Class<? extends Annotation>, Annotation> annotations) {
        this.field = field;
        this.annotations = annotations;
    }

    public void ignore() {
        annotations.put(JsonIgnore.class, new JacksonaticJsonIgnore(true));
    }

    public void map() {
        annotations.put(JsonProperty.class, new JacksonaticJsonProperty(field.getName(), false, JsonProperty.INDEX_UNKNOWN, ""));
    }

    public void map(String mappedName) {
        annotations.put(JsonProperty.class, new JacksonaticJsonProperty(mappedName, false, JsonProperty.INDEX_UNKNOWN, ""));
    }

    PropertyMapping copy() {
        return new PropertyMapping(field, this.annotations.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    PropertyMapping copyWithParentMapping(PropertyMapping parentMapping) {
        return new PropertyMapping(field, this.annotations.size() == 0 ?
                parentMapping.annotations.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue)) :
                annotations.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public String getName() {
        return field.getName();
    }

    public String getMappedName() {
        return Optional.ofNullable(annotations.get(JsonProperty.class)).map(annotation -> ((JsonProperty)annotation).value()).orElse(field.getName());
    }

    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return annotations;
    }

    public boolean isMapped() {
        return annotations.containsKey(JsonProperty.class) && !annotations.containsKey(JsonIgnore.class);
    }

    public boolean isIgnored() {
        return annotations.containsKey(JsonIgnore.class);
    }
}
