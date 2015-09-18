package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.annotation.Annotation;

public class JacksonaticJsonSubTypes implements JsonSubTypes {

    private final Type[] value;

    public JacksonaticJsonSubTypes(Type[] value) {
        this.value = value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonSubTypes.class;
    }

    @Override
    public Type[] value() {
        return value;
    }
}
