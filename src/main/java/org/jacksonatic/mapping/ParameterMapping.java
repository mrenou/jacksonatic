package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jacksonatic.annotation.JacksonaticJsonProperty;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class ParameterMapping {

    private Class<?> parameterClass;

    private Map<Class<? extends Annotation>, Annotation> annotations;

    public ParameterMapping(Class<?> parameterClass, String jsonProperty) {
        this(parameterClass, new HashMap<>());
        map(jsonProperty);
    }

    ParameterMapping(Class<?> parameterClass,  Map<Class<? extends Annotation>, Annotation> annotations) {
        this.parameterClass = parameterClass;
        this.annotations = annotations;
    }

    public void map(String mappedName) {
        annotations.put(JsonProperty.class, new JacksonaticJsonProperty(mappedName, false, JsonProperty.INDEX_UNKNOWN, ""));
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return annotations;
    }

    ParameterMapping copy() {
        return new ParameterMapping(parameterClass, annotations.entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
}
