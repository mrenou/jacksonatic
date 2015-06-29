package org.jacksonatic;

import com.fasterxml.jackson.databind.introspect.*;
import org.jacksonatic.annotation.JacksonaticJsonCreator;
import org.jacksonatic.annotation.JacksonaticJsonProperty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class ClassProcess {

    static class Pouet<T> {
        private Optional<Integer> opt;


        public Optional<Integer> getOpt() {
            return opt;
        }
    }


    public static AnnotatedClass process(AnnotatedClass annotatedClass, ClassMapping classMapping) {

        AnnotatedClassUpdater.setFields(annotatedClass, StreamSupport.stream(annotatedClass.fields().spliterator(), false)
                .map(annotatedField -> PropertyProcess.process(annotatedField, classMapping))
                .collect(Collectors.toList()));

        ((Optional<ConstructorMapping>) classMapping.getConstructorMapping()).ifPresent(constructorMapping -> {
            if (constructorMapping.isStaticFactory()) {
                List<AnnotatedMethod> methods = annotatedClass.getStaticMethods().stream()
                        .map(annotatedMethod -> {
                            boolean match = matchConstructor(constructorMapping, annotatedMethod);
                            if (match) {
                                AnnotationMap annotationMap = new AnnotationMap();
                                annotationMap.add(new JacksonaticJsonCreator());
                                IntStream.range(0, constructorMapping.getParameters().size()).forEach(index -> {
                                    JacksonaticJsonProperty jsonProperty = new JacksonaticJsonProperty(constructorMapping.getParameters().get(index).getName(), true, index, "");
                                    annotatedMethod.addOrOverrideParam(index, jsonProperty);
                                });
                                return annotatedMethod.withAnnotations(annotationMap);
                            }
                            return annotatedMethod;
                        })
                        .collect(Collectors.toList());
                AnnotatedClassUpdater.setCreatorMethods(annotatedClass, methods);
            } else {
                List<AnnotatedConstructor> constructors = annotatedClass.getConstructors().stream()
                        .map(annotatedConstructor -> {
                            boolean match = matchConstructor(constructorMapping, annotatedConstructor);
                            if (match) {
                                AnnotationMap annotationMap = new AnnotationMap();
                                annotationMap.add(new JacksonaticJsonCreator());
                                IntStream.range(0, constructorMapping.getParameters().size()).forEach(index -> {
                                    JacksonaticJsonProperty jsonProperty = new JacksonaticJsonProperty(constructorMapping.getParameters().get(index).getName(), true, index, "");
                                    annotatedConstructor.addOrOverrideParam(index, jsonProperty);
                                });
                                return annotatedConstructor.withAnnotations(annotationMap);
                            }
                            return annotatedConstructor;
                        })
                        .collect(Collectors.toList());
                AnnotatedClassUpdater.setConstructors(annotatedClass, constructors);
            }
        });

        return annotatedClass;
    }

    private static boolean matchConstructor(ConstructorMapping constructorMapping, AnnotatedWithParams annotatedWithParams) {
        boolean match = false;
        if ((constructorMapping.getMethodName() == null || constructorMapping.getMethodName().equals(annotatedWithParams.getName()))
                && annotatedWithParams.getParameterCount() == constructorMapping.getParameters().size()) {
            match = true;
            for (int i = 0; i < annotatedWithParams.getParameterCount(); i++) {
                if (annotatedWithParams.getParameter(i).getDeclaringClass().equals(constructorMapping.getParameters().get(i).getType())) {
                    match = false;
                }
            }
        }
        return match;
    }

}
