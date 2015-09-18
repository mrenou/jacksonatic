package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.annotation.Annotation;

public class JacksonaticJsonTypeInfo implements JsonTypeInfo {

    private final Id use;

    private final As include;

    private final String property;


    private final Class<?> defaultImpl;

    private final boolean visible;

    public JacksonaticJsonTypeInfo(Id use, As include, String property, Class<?> defaultImpl, boolean visible) {
        this.use = use;
        this.include = include == null ? As.PROPERTY : include;
        this.property = property == null ? "" : property;
        this.defaultImpl = defaultImpl == null ? None.class : defaultImpl;
        this.visible = visible;
    }


    @Override
    public Id use() {
        return use;
    }

    @Override
    public As include() {
        return include;
    }

    @Override
    public String property() {
        return property;
    }

    @Override
    public Class<?> defaultImpl() {
        return defaultImpl;
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonTypeInfo.class;
    }

}
