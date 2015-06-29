package org.jacksonatic;

import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassUpdater;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
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

        (( Optional<ConstructorMapping>)classMapping.getConstructorMapping()).ifPresent(constructorMapping -> {
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
        });

        return annotatedClass;
    }

    private static boolean matchConstructor(ConstructorMapping constructorMapping, AnnotatedConstructor annotatedConstructor) {
        boolean match = true;
        if (annotatedConstructor.getParameterCount() == constructorMapping.getParameters().size()) {
            for (int i = 0; i < annotatedConstructor.getParameterCount(); i++) {
                if (annotatedConstructor.getParameter(i).getDeclaringClass().equals(constructorMapping.getParameters().get(i).getType())) {
                    match = false;
                }
            }
        }
        return match;
    }

}
