package org.jacksonatic.annotation;

import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import org.jacksonatic.mapping.ClassBuilderMapping;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.ParameterMapping;
import org.jacksonatic.mapping.PropertyMapping;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class ClassAnnotationDecorator {

    public static AnnotatedClass decorate(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        addFieldAnnotations(annotatedClass, classMapping);
        addConstructorAnnotations(annotatedClass, classMapping);
        return annotatedClass;
    }

    private static void addFieldAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        StreamSupport.stream(annotatedClass.fields().spliterator(), false)
                .forEach(annotatedField -> {
                    final PropertyMapping propertyMapping = classMapping.getPropertyMapping(annotatedField.getName());
                    if (!classMapping.allPropertiesAreMapped() && !propertyMapping.isMapped()) {
                        propertyMapping.ignore();
                    }
                    propertyMapping.getAnnotations().values().stream()
                            .forEach(annotation -> annotatedField.addOrOverride(annotation));
                });
    }

    private static void addConstructorAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        ((Optional<ClassBuilderMapping>) classMapping.getClassBuilderMappingOpt()).ifPresent(classBuilderMapping -> {
            if (classBuilderMapping.isStaticFactory()) {
                AnnotatedMethod staticFactoryMember = annotatedClass.getStaticMethods().stream()
                        .filter(method -> method.getMember().equals(classBuilderMapping.getStaticFactory()))
                        .findFirst()
                        .get();
                setAnnotationsOnMemberWithParams(classBuilderMapping.getAnnotations(), classBuilderMapping.getParametersMapping(), staticFactoryMember);
            } else {
                AnnotatedConstructor constructorMember = annotatedClass.getConstructors().stream()
                        .filter(constructor -> constructor.getMember().equals(classBuilderMapping.getConstructor()))
                        .findFirst()
                        .get();
                setAnnotationsOnMemberWithParams(classBuilderMapping.getAnnotations(), classBuilderMapping.getParametersMapping(), constructorMember);
            }
        });
    }

    private static void setAnnotationsOnMemberWithParams(Map<Class<? extends Annotation>, Annotation> memberAnnotation, List<ParameterMapping> parametersMapping, AnnotatedWithParams constructorMember) {
        memberAnnotation.values().stream().forEach(annotation -> constructorMember.addOrOverride(annotation));
        IntStream.range(0, parametersMapping.size())
                .forEach(index -> parametersMapping.get(index).getAnnotations().values().stream()
                        .forEach(annotation -> constructorMember.addOrOverrideParam(index, annotation)));
    }

}
