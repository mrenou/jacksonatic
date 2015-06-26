package test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassUpdater;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ClassProcess {

    public static AnnotatedClass process(AnnotatedClass annotatedClass, ClassMapping classMapping) {

        AnnotatedClassUpdater.setFields(annotatedClass, StreamSupport.stream(annotatedClass.fields().spliterator(), false)
                .map(annotatedField -> PropertyProcess.process(annotatedField, classMapping))
                .collect(Collectors.toList()));

        Optional<TypedParameter<?>> test = Optional.empty();
        
        test.ifPresent(t -> {
            final TypedParameter<?> t1 = t;
        });
        
        classMapping.getConstructorToUse().ifPresent(constructorToUse -> {
            final List<TypedParameter<?>> constructorToUse2 = (List<TypedParameter<?>>) constructorToUse;
            List<AnnotatedConstructor> constructors = annotatedClass.getConstructors().stream()
                    .map(annotatedConstructor -> {
                        boolean match = true;
                        if (annotatedConstructor.getParameterCount() == constructorToUse2.size()) {
                            for (int i = 0; i < annotatedConstructor.getParameterCount(); i++) {
                                if (annotatedConstructor.getParameter(i).getDeclaringClass().equals(constructorToUse2.get(i).getType())) {
                                    match = false;
                                }
                            }
                        }
                        if (match) {
                            AnnotationMap annotationMap = new AnnotationMap();
                            annotationMap.add(new JsonCreator() {
                                @Override
                                public Mode mode() {
                                    return Mode.DEFAULT;
                                }

                                @Override
                                public Class<? extends Annotation> annotationType() {
                                    return JsonCreator.class;
                                }
                            });


                            for (int i = 0; i < constructorToUse2.size(); i++) {
                                final int index = i;
                                annotatedConstructor.addOrOverrideParam(i, new JsonProperty() {
                                    @Override
                                    public String value() {
                                        return constructorToUse2.get(index).getName();
                                    }

                                    @Override
                                    public boolean required() {
                                        return true;
                                    }

                                    @Override
                                    public int index() {
                                        return index;
                                    }

                                    @Override
                                    public String defaultValue() {
                                        return "";
                                    }

                                    @Override
                                    public Class<? extends Annotation> annotationType() {
                                        return JsonProperty.class;
                                    }
                                });
                            }
                            return annotatedConstructor.withAnnotations(annotationMap);
                        }
                        return annotatedConstructor;
                    })
                    .collect(Collectors.toList());
            AnnotatedClassUpdater.setConstructors(annotatedClass, constructors);
        });

        return annotatedClass;
    }
}
