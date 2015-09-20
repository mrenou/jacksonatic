package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.lang.annotation.Annotation;

public class JacksonaticJsonSubTypes implements JsonSubTypes {

    private Type[] value;

    public JacksonaticJsonSubTypes(Type[] value) {
        this.value = value;
    }

    private JacksonaticJsonSubTypes() {
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonSubTypes.class;
    }

    @Override
    public Type[] value() {
        return value;
    }

    public static Builder jsonSubTypes() {
        return new Builder();
    }

    public static Builder jsonSubTypes(Type[] value) {
        return new Builder().value(value);
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonSubTypes jsonSubTypes = new JacksonaticJsonSubTypes();

        public Builder value(Type[] value) {
            jsonSubTypes.value = value;
            return this;
        }

        @Override
        public JsonSubTypes build() {
            return jsonSubTypes;
        }
    }
}
