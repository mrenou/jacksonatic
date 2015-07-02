package org.jacksonatic.annotation;

import com.fasterxml.jackson.databind.introspect.*;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.PropertyMapping;
import org.jacksonatic.annotation.builder.*;
import org.jacksonatic.mapping.ConstructorMapping;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class ClassAnnotationDecorator {

    private static List<FieldAnnotationBuilder> FIELD_ANNOTATION_BUILDERS = Arrays.asList(new JsonIgnoreBuilder(), new JsonPropertyBuilder());

    private static List<ConstructorAnnotationBuilder> CONSTRUCTOR_ANNOTATION_BUILDER = Arrays.asList(new JsonCreatorBuilder());

    public static AnnotatedClass decorate(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        addFieldAnnotations(annotatedClass, classMapping);
        addConstructorAnnotations(annotatedClass, classMapping);
        return annotatedClass;
    }

    private static void addFieldAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        AnnotatedClassUpdater.setFields(annotatedClass, StreamSupport.stream(annotatedClass.fields().spliterator(), false)
                .map(annotatedField -> {
                    AnnotationMap annotationMap = new AnnotationMap();
                    PropertyMapping propertyMapping = classMapping.getPropertyMapping(annotatedField.getName());
                    FIELD_ANNOTATION_BUILDERS.stream()
                            .filter(fieldAnnotationBuilder -> fieldAnnotationBuilder.hasToBuild(annotatedField, classMapping, propertyMapping))
                            .map(fieldAnnotationBuilder -> fieldAnnotationBuilder.build(annotatedField, classMapping, propertyMapping))
                            .forEach(annotation -> annotationMap.addIfNotPresent(annotation));
                    return annotatedField.withAnnotations(annotationMap);
                })
                .collect(Collectors.toList()));
    }

    private static void addConstructorAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        ((Optional<ConstructorMapping>) classMapping.getConstructorMapping()).ifPresent(constructorMapping -> {
            if (constructorMapping.isStaticFactory()) {
                List<AnnotatedMethod> methods = annotatedClass.getStaticMethods().stream()
                        .map(annotatedMethod -> {
                            AnnotationMap annotationMap = buildAnnotationMapAndAddParamAnnotations(constructorMapping, annotatedMethod);
                            return annotatedMethod.withAnnotations(annotationMap);
                        })
                        .collect(Collectors.toList());
                AnnotatedClassUpdater.setCreatorMethods(annotatedClass, methods);
            } else {
                List<AnnotatedConstructor> methods = annotatedClass.getConstructors().stream()
                        .map(annotatedMethod -> {
                            AnnotationMap annotationMap = buildAnnotationMapAndAddParamAnnotations(constructorMapping, annotatedMethod);
                            return annotatedMethod.withAnnotations(annotationMap);
                        })
                        .collect(Collectors.toList());
                AnnotatedClassUpdater.setConstructors(annotatedClass, methods);
            }
        });
    }

    private static <T extends AnnotatedWithParams> AnnotationMap buildAnnotationMapAndAddParamAnnotations(ConstructorMapping constructorMapping, T annotatedMethod) {
        AnnotationMap annotationMap = new AnnotationMap();
        CONSTRUCTOR_ANNOTATION_BUILDER.stream()
                .filter(constructorAnnotationBuilder -> constructorAnnotationBuilder.hasToBuild(annotatedMethod, constructorMapping))
                .forEach(constructorAnnotationBuilder -> {
                    final Annotation annotation = constructorAnnotationBuilder.buildMethodAnnotation(annotatedMethod, constructorMapping);
                    annotationMap.addIfNotPresent(annotation);
                    IntStream.range(0, constructorMapping.getParameters().size()).forEach(index ->
                            annotatedMethod.addOrOverrideParam(index, constructorAnnotationBuilder.buildParamAnnotation(index, constructorMapping.getParameters().get(index))));
                });
        return annotationMap;
    }




}
