package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.lang.annotation.Annotation;

public class JacksonaticJsonSubTypesType implements JsonSubTypes.Type {

    private final String name;

    private final Class<?> value;

    public JacksonaticJsonSubTypesType(String name, Class<?> value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?> value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonSubTypes.Type.class;
    }

}
