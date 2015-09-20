package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.lang.annotation.Annotation;

public class JacksonaticJsonCreator implements JsonCreator {

    private Mode mode = Mode.DEFAULT;

    private JacksonaticJsonCreator() {
    }

    @Override
    public Mode mode() {
        return mode;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonCreator.class;
    }

    public static Builder jsonCreator() {
        return new Builder();
    }

    public static Builder jsonCreator(Mode mode) {
        return new Builder().mode(mode);
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonCreator jsonCreator = new JacksonaticJsonCreator();

        public Builder mode(Mode mode) {
            jsonCreator.mode = mode;
            return this;
        }

        @Override
        public JsonCreator build() {
            return jsonCreator;
        }
    }
}
