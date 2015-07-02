package org.jacksonatic.annotation.builder;

import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import org.jacksonatic.mapping.ConstructorMapping;
import org.jacksonatic.mapping.ParameterMapping;
import org.jacksonatic.annotation.JacksonaticJsonCreator;
import org.jacksonatic.annotation.JacksonaticJsonProperty;

import java.lang.annotation.Annotation;

public class JsonCreatorBuilder implements ConstructorAnnotationBuilder {


    @Override
    public boolean hasToBuild(AnnotatedWithParams annotatedWithParams, ConstructorMapping constructorMapping) {
        boolean match = false;
        if ((constructorMapping.getMethodName() == null || constructorMapping.getMethodName().equals(annotatedWithParams.getName()))
                && annotatedWithParams.getParameterCount() == constructorMapping.getParameters().size()) {
            match = true;
            for (int i = 0; i < annotatedWithParams.getParameterCount(); i++) {
                if (!annotatedWithParams.getParameter(i).getParameterType().equals(constructorMapping.getParameters().get(i).getParameterClass())) {
                    match = false;
                }
            }
        }
        return match;
    }

    @Override
    public Annotation buildMethodAnnotation(AnnotatedWithParams annotatedWithParams, ConstructorMapping constructorMapping) {
        return new JacksonaticJsonCreator();
    }

    @Override
    public Annotation buildParamAnnotation(int index, ParameterMapping parameterMapping) {
        return new JacksonaticJsonProperty(parameterMapping.getJsonProperty(), true, index, "");
    }
}
