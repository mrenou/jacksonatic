package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.annotation.Annotation;

public class JacksonaticJsonProperty implements JsonProperty {

    private String value = USE_DEFAULT_NAME;

    private boolean required = false;

    private int index = INDEX_UNKNOWN;

    private String defaultValue = "";

    private JacksonaticJsonProperty() {
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

    public static Builder jsonProperty() {
        return new Builder();
    }

    public static Builder jsonProperty(String value) {
        return new Builder().value(value);
    }

    static class Builder implements AnnotationBuilder {

        private JacksonaticJsonProperty jsonProperty = new JacksonaticJsonProperty();

        public  Builder value(String value) {
            jsonProperty.value = value;
            return this;
        }

        public  Builder required(boolean required) {
            jsonProperty.required = required;
            return this;
        }

        public  Builder index(int index) {
            jsonProperty.index = index;
            return this;
        }

        public  Builder defaultValue(String defaultValue) {
            jsonProperty.defaultValue = defaultValue;
            return this;
        }

        @Override
        public Annotation build() {
            return jsonProperty;
        }
    }
}

