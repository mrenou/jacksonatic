package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.lang.annotation.Annotation;

public class JacksonaticJsonCreator implements JsonCreator {

    private final Mode mode;

    public JacksonaticJsonCreator() {
        this(Mode.DEFAULT);
    }

    public JacksonaticJsonCreator(Mode mode) {
        this.mode = mode;
    }

    @Override
    public Mode mode() {
        return mode;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonCreator.class;
    }
}
