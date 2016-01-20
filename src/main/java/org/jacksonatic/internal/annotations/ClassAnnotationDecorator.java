/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic.internal.annotations;

import com.fasterxml.jackson.databind.introspect.*;
import org.jacksonatic.internal.AnnotatedClassLogger;
import org.jacksonatic.internal.mapping.ClassMappingInternal;
import org.jacksonatic.internal.mapping.builder.ClassBuilderFinder;
import org.jacksonatic.internal.mapping.builder.ClassBuilderMapping;
import org.jacksonatic.internal.mapping.builder.parameter.ParameterMapping;
import org.jacksonatic.internal.mapping.field.FieldMappingInternal;
import org.jacksonatic.internal.mapping.method.MethodMappingInternal;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.jacksonatic.internal.mapping.method.MethodSignature.methodSignature;
import static org.jacksonatic.internal.mapping.method.MethodSignature.methodSignatureIgnoringParameters;
import static org.jacksonatic.internal.util.StreamUtil.getFirstPresent;
import static org.jacksonatic.internal.util.StreamUtil.stream;

/**
 * Add annotations defined in {@link ClassMappingInternal} to {@link com.fasterxml.jackson.databind.introspect.AnnotatedClass}
 */
public class ClassAnnotationDecorator {

    private ClassBuilderFinder classBuilderFinder = new ClassBuilderFinder();

    public AnnotatedClass decorate(AnnotatedClass annotatedClass, ClassMappingInternal<Object> classMapping) {
        annotatedClass = addClassAnnotations(annotatedClass, classMapping);
        addFieldAnnotations(annotatedClass, classMapping);
        addMethodAnnotations(annotatedClass, classMapping);
        addBuilderAnnotations(annotatedClass, classMapping);
        AnnotatedClassLogger.log(annotatedClass);
        return annotatedClass;
    }

    private AnnotatedClass addClassAnnotations(AnnotatedClass annotatedClass, ClassMappingInternal<Object> classMapping) {
        AnnotationMap annotationMap = new AnnotationMap();
        stream(annotatedClass.annotations()).forEach(annotationMap::add);
        classMapping.getAnnotations().values().stream().forEach(annotationMap::add);
        return annotatedClass.withAnnotations(annotationMap);
    }

    private void addFieldAnnotations(AnnotatedClass annotatedClass, ClassMappingInternal<Object> classMapping) {
        stream(annotatedClass.fields()).forEach(annotatedField -> {
            FieldMappingInternal fieldMapping = classMapping.getOrCreateFieldMappingInternal(annotatedField.getName());
            mapByDefaultIfAllFieldsAreMapped(classMapping, fieldMapping);
            ignoreByDefaultIfAllFieldsAreNotMapped(classMapping, fieldMapping);
            fieldMapping.getAnnotations().values().stream().forEach(annotatedField::addOrOverride);
        });
    }

    private void mapByDefaultIfAllFieldsAreMapped(ClassMappingInternal<Object> classMapping, FieldMappingInternal fieldMapping) {
        if (classMapping.allFieldsAreMapped() && !fieldMapping.isMapped() && !fieldMapping.isIgnored()) {
            fieldMapping.map();
        }
    }

    private void ignoreByDefaultIfAllFieldsAreNotMapped(ClassMappingInternal<Object> classMapping, FieldMappingInternal fieldMapping) {
        if (!classMapping.allFieldsAreMapped() && !fieldMapping.isMapped() && !fieldMapping.isIgnored()) {
            fieldMapping.ignore();
        }
    }

    private static void addMethodAnnotations(AnnotatedClass annotatedClass, ClassMappingInternal<Object> classMapping) {
        stream(annotatedClass.memberMethods()).forEach(annotatedMethod -> getFirstPresent(
                () -> classMapping.<MethodMappingInternal>getMethodMappingInternal(methodSignature(annotatedMethod.getName(), annotatedMethod.getRawParameterTypes())),
                () -> classMapping.<MethodMappingInternal>getMethodMappingInternal(methodSignatureIgnoringParameters(annotatedMethod.getName()))).ifPresent(methodMapping -> methodMapping.getAnnotations().values().stream()
                .forEach(annotatedMethod::addOrOverride)));
    }

    private void addBuilderAnnotations(AnnotatedClass annotatedClass, ClassMappingInternal<Object> classMapping) {
        classMapping.getClassBuilderCriteriaOpt()
                .ifPresent(classBuilderCriteria -> classBuilderFinder.find(classMapping, classBuilderCriteria)
                        .ifPresent(classBuilderMapping -> {
                            if (classBuilderMapping.isStaticFactory()) {
                                addStaticFactoryAnnotations(annotatedClass, classBuilderMapping);
                            } else {
                                addConstructorAnnotations(annotatedClass, classBuilderMapping);
                            }
                        }));
    }

    private void addStaticFactoryAnnotations(AnnotatedClass annotatedClass, ClassBuilderMapping classBuilderMapping) {
        AnnotatedMethod staticFactoryMember = annotatedClass.getStaticMethods().stream()
                .filter(method -> method.getMember().equals(classBuilderMapping.getStaticFactory()))
                .findFirst()
                .get();
        setAnnotationsOnMemberWithParams(classBuilderMapping.getAnnotations(), classBuilderMapping.getParametersMapping(), staticFactoryMember);
    }

    private void addConstructorAnnotations(AnnotatedClass annotatedClass, ClassBuilderMapping classBuilderMapping) {
        AnnotatedConstructor constructorMember = Stream.concat(
                annotatedClass.getConstructors().stream(),
                Optional.ofNullable(annotatedClass.getDefaultConstructor()).map(Stream::of).orElse(Stream.empty())
        )
                .filter(constructor -> constructor.getMember().equals(classBuilderMapping.getConstructor()))
                .findFirst()
                .get();
        setAnnotationsOnMemberWithParams(classBuilderMapping.getAnnotations(), classBuilderMapping.getParametersMapping(), constructorMember);
    }

    private void setAnnotationsOnMemberWithParams(Map<Class<? extends Annotation>, Annotation> memberAnnotation, List<ParameterMapping> parametersMapping, AnnotatedWithParams constructorMember) {
        memberAnnotation.values().stream().forEach(constructorMember::addOrOverride);
        IntStream.range(0, parametersMapping.size())
                .forEach(index -> parametersMapping.get(index).getAnnotations().values().stream()
                        .forEach(annotation -> constructorMember.addOrOverrideParam(index, annotation)));
    }

}
