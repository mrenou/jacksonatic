package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.lang.annotation.Annotation;

public class JacksonaticJsonTypeName implements JsonTypeName {

    private final String value;

    public JacksonaticJsonTypeName(String value) {
        this.value = value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonTypeName.class;
    }

    @Override
    public String value() {
        return null;
    }
}
