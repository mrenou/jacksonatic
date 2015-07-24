package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.annotation.Annotation;

public class JacksonaticJsonIgnore implements JsonIgnore {

    private final boolean value;

    public JacksonaticJsonIgnore(boolean value) {
        this.value = value;
    }

    @Override
    public boolean value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonIgnore.class;
    }
}
