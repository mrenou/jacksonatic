package org.jacksonatic.mapping;

import org.jacksonatic.annotation.AnnotationBuilder;
import org.jacksonatic.annotation.Annotations;

import java.lang.annotation.Annotation;
import java.util.Map;

import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;

public class ParameterMapping {

    private Class<?> parameterClass;

    private Annotations annotations;

    public ParameterMapping(Class<?> parameterClass, String jsonProperty) {
        this(parameterClass, new Annotations());
        map(jsonProperty);
    }

    ParameterMapping(Class<?> parameterClass, Annotations annotations) {
        this.parameterClass = parameterClass;
        this.annotations = annotations;
    }

    public void addAnnotation(AnnotationBuilder annotationBuilder) {
        Annotation annotation = annotationBuilder.build();
        annotations.put(annotation.getClass(), annotation);
    }

    public void map(String mappedName) {
        addAnnotation(jsonProperty(mappedName));
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return annotations;
    }

    ParameterMapping copy() {
        return new ParameterMapping(parameterClass, annotations.copy());
    }
}
