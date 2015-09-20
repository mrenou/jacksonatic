package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.lang.annotation.Annotation;

public class JacksonaticJsonTypeName implements JsonTypeName {

    private String value = "";

    private JacksonaticJsonTypeName() {
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonTypeName.class;
    }

    @Override
    public String value() {
        return value;
    }

    public static Builder jsonTypeName() {
        return new Builder();
    }

    public static Builder jsonTypeName(String value) {
        return new Builder().value(value);
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonTypeName jsonTypeName = new JacksonaticJsonTypeName();

        public Builder value(String value) {
            jsonTypeName.value = value;
            return this;
        }

        @Override
        public JsonTypeName build() {
            return jsonTypeName;
        }
    }
}
