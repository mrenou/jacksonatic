package com.fasterxml.jackson.databind.introspect;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.jacksonatic.mapping.ClassBuilderMapping;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AnnotatedClassUpdater {

    public static void setConstructors(AnnotatedClass annotatedClass, List<AnnotatedConstructor> annotatedConstructors) {
        annotatedClass._constructors = annotatedConstructors;
    }

    public static void setCreatorMethods(AnnotatedClass annotatedClass, List<AnnotatedMethod> annotatedConstructors) {
        annotatedClass._creatorMethods = annotatedConstructors;
    }

    public static void setCreatorMethods(AnnotatedClass annotatedClass, ClassBuilderMapping classBuilderMapping) {
        AnnotatedMethod staticFactoryMethod = annotatedClass.getStaticMethods().stream()
                .filter(method -> method.getMember() == classBuilderMapping.getStaticFactory())
                .findFirst()
                .get();
        classBuilderMapping.getAnnotations().values().stream().forEach(annotation -> staticFactoryMethod.addOrOverride(annotation));
        IntStream.range(0, classBuilderMapping.getParametersMapping().size())
                .forEach(index -> classBuilderMapping.getParametersMapping().get(index).getAnnotations().values().stream()
                        .forEach(annotation -> staticFactoryMethod.addOrOverrideParam(index, annotation)));
    }

    public static void setFields(AnnotatedClass annotatedClass, List<AnnotatedField> annotatedFields) {
        annotatedClass._fields = annotatedFields;
    }
}
