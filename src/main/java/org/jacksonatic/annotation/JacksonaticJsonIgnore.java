package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.annotation.Annotation;

public class JacksonaticJsonIgnore implements JsonIgnore {

    private boolean value = true;

    private JacksonaticJsonIgnore() {

    }

    @Override
    public boolean value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonIgnore.class;
    }

    public static Builder jsonIgnore() {
        return new Builder();
    }

    public static Builder jsonIgnore(boolean value) {
        return new Builder().value(value);
    }

    static class Builder implements AnnotationBuilder {

        private JacksonaticJsonIgnore jsonIgnore = new JacksonaticJsonIgnore();

        public  Builder value(boolean value) {
            jsonIgnore.value = value;
            return this;
        }

        @Override
        public JsonIgnore build() {
            return jsonIgnore;
        }
    }
}
