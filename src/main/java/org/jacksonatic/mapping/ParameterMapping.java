package org.jacksonatic.mapping;

import java.util.Map;
import java.util.PriorityQueue;

public class ParameterMapping {

    private Class<?> parameterClass;

    private String jsonProperty;

    ParameterMapping(Class<?> parameterClass, String jsonProperty) {
        this.parameterClass = parameterClass;
        this.jsonProperty = jsonProperty;
    }

    ParameterMapping(ParameterMatcher parameterMatcher, Map<Class<?>, PriorityQueue<String>> propertiesByClass, Map<String, Class<?>> classByProperty) {
        this.parameterClass = loadParameterClass(parameterMatcher, classByProperty);
        this.jsonProperty = loadJsonProperty(parameterMatcher, propertiesByClass);
    }

    private Class<?> loadParameterClass(ParameterMatcher parameterMatcher, Map<String, Class<?>> classByProperty) {
        Class<?> parameterClass = parameterMatcher.getParameterClass();
        if (parameterClass == null) {
            parameterClass = classByProperty.get(parameterMatcher.getFieldProperty());
        }
        if (parameterClass == null) {
            throw new RuntimeException("Cannot find class for parameter matcher " + parameterMatcher);
        }
        return parameterClass;
    }

    private String loadJsonProperty(ParameterMatcher parameterMatcher, Map<Class<?>, PriorityQueue<String>> propertiesByClass) {
        String jsonProperty = parameterMatcher.getJsonProperty();
        if (jsonProperty == null) {
            final PriorityQueue<String> properties = propertiesByClass.get(parameterMatcher.getParameterClass());
            if (properties != null) {
                jsonProperty = properties.poll();
            }
        }
        if (jsonProperty == null) {
            throw new RuntimeException("Cannot find class for parameter matcher " + parameterMatcher);
        }
        return jsonProperty;
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    public String getJsonProperty() {
        return jsonProperty;
    }

    ParameterMapping copy() {
        return new ParameterMapping(parameterClass, jsonProperty);
    }
}
