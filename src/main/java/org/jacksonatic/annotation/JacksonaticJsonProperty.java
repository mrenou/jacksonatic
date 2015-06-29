package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.annotation.Annotation;

public class JacksonaticJsonProperty implements JsonProperty {

    private String value;

    private boolean required;

    private int index;

    private String defaultValue;

    public JacksonaticJsonProperty(String value, boolean required, int index, String defaultValue) {
        this.value = value;
        this.required = required;
        this.index = index;
        this.defaultValue = defaultValue;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public boolean required() {
        return required;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public String defaultValue() {
        return defaultValue;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonProperty.class;
    }
}
