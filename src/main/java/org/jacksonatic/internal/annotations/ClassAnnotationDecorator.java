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
import org.jacksonatic.internal.mapping.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.jacksonatic.internal.mapping.ClassBuilderFinder.findClassBuilderMapping;
import static org.jacksonatic.internal.mapping.MethodSignature.methodSignature;
import static org.jacksonatic.internal.mapping.MethodSignature.methodSignatureIgnoringParameters;
import static org.jacksonatic.internal.util.StreamUtil.stream;

/**
 * Add annotations defined in {@link ClassMappingInternal} to {@link com.fasterxml.jackson.databind.introspect.AnnotatedClass}
 */
public class ClassAnnotationDecorator {

    public static AnnotatedClass decorate(AnnotatedClass annotatedClass, ClassMappingInternal classMapping) {
        annotatedClass = addClassAnnotations(annotatedClass, classMapping);
        addFieldAnnotations(annotatedClass, classMapping);
        addMethodAnnotations(annotatedClass, classMapping);
        addConstructorAnnotations(annotatedClass, classMapping);
        AnnotatedClassLogger.log(annotatedClass);
        return annotatedClass;
    }

    private static AnnotatedClass addClassAnnotations(AnnotatedClass annotatedClass, ClassMappingInternal classMapping) {
        AnnotationMap annotationMap = new AnnotationMap();
        stream(annotatedClass.annotations()).forEach(annotation -> annotationMap.add(annotation));
        classMapping.getAnnotations().values().stream().forEach(annotation -> annotationMap.add(annotation));
        return annotatedClass.withAnnotations(annotationMap);
    }

    private static void addFieldAnnotations(AnnotatedClass annotatedClass, ClassMappingInternal classMapping) {
        stream(annotatedClass.fields())
                .forEach(annotatedField -> {
                    FieldMappingInternal fieldMapping = classMapping.getOrCreateFieldMappingInternal(annotatedField.getName());
                    if (classMapping.allFieldsAreMapped() && !fieldMapping.isMapped() && !fieldMapping.isIgnored()) {
                        fieldMapping.map();
                    }
                    if (!classMapping.allFieldsAreMapped() && !fieldMapping.isMapped() && !fieldMapping.isIgnored()) {
                        fieldMapping.ignore();
                    }
                    fieldMapping.getAnnotations().values().stream()
                            .forEach(annotation -> annotatedField.addOrOverride(annotation));
                });
    }

    private static void addMethodAnnotations(AnnotatedClass annotatedClass, ClassMappingInternal classMapping) {
        stream(annotatedClass.memberMethods())
                .forEach(annotatedMethod -> {
                    Optional<MethodMappingInternal> methodMappingOpt = classMapping.getMethodMappingInternal(methodSignature(annotatedMethod.getName(), annotatedMethod.getRawParameterTypes()));
                    methodMappingOpt = Optional.ofNullable(methodMappingOpt
                            .orElse(((Optional<MethodMappingInternal>) classMapping.getMethodMappingInternal(methodSignatureIgnoringParameters(annotatedMethod.getName())))
                                    .orElse(null)));
                    methodMappingOpt.ifPresent(methodMapping -> methodMapping.getAnnotations().values().stream()
                            .forEach(annotation -> annotatedMethod.addOrOverride(annotation)));

                });
    }

    private static void addConstructorAnnotations(AnnotatedClass annotatedClass, ClassMappingInternal classMapping) {
        ((Optional<ClassBuilderCriteria>) classMapping.getClassBuilderCriteriaOpt())
                .ifPresent(classBuilderCriteria -> ((Optional<ClassBuilderMapping>) findClassBuilderMapping(classMapping, classBuilderCriteria))
                        .ifPresent(classBuilderMapping -> {
                            if (classBuilderMapping.isStaticFactory()) {
                                Optional<AnnotatedMethod> first = annotatedClass.getStaticMethods().stream()
                                        .filter(method -> method.getMember().equals(classBuilderMapping.getStaticFactory()))
                                        .findFirst();
                                AnnotatedMethod staticFactoryMember = first
                                        .get();
                                setAnnotationsOnMemberWithParams(classBuilderMapping.getAnnotations(), classBuilderMapping.getParametersMapping(), staticFactoryMember);
                            } else {
                                AnnotatedConstructor constructorMember = Stream.concat(
                                        annotatedClass.getConstructors().stream(),
                                        Optional.ofNullable(annotatedClass.getDefaultConstructor()).map(constructor -> Stream.of(constructor)).orElse(Stream.empty())
                                )
                                        .filter(constructor -> constructor.getMember().equals(classBuilderMapping.getConstructor()))
                                        .findFirst()
                                        .get();
                                setAnnotationsOnMemberWithParams(classBuilderMapping.getAnnotations(), classBuilderMapping.getParametersMapping(), constructorMember);
                            }
                        }));
    }

    private static void setAnnotationsOnMemberWithParams(Map<Class<? extends Annotation>, Annotation> memberAnnotation, List<ParameterMapping> parametersMapping, AnnotatedWithParams constructorMember) {
        memberAnnotation.values().stream().forEach(annotation -> constructorMember.addOrOverride(annotation));
        IntStream.range(0, parametersMapping.size())
                .forEach(index -> parametersMapping.get(index).getAnnotations().values().stream()
                        .forEach(annotation -> constructorMember.addOrOverrideParam(index, annotation)));
    }

}
