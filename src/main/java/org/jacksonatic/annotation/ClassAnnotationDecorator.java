/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic.annotation;

import com.fasterxml.jackson.databind.introspect.*;
import org.jacksonatic.AnnotatedClassLogger;
import org.jacksonatic.mapping.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.jacksonatic.mapping.ClassBuilderFinder.findClassBuilderMapping;
import static org.jacksonatic.mapping.MethodSignature.methodSignature;
import static org.jacksonatic.mapping.MethodSignature.methodSignatureIgnoringParameters;
import static org.jacksonatic.util.StreamUtil.stream;

/**
 * Add annotations defined in {@link org.jacksonatic.mapping.ClassMapping} to {@link com.fasterxml.jackson.databind.introspect.AnnotatedClass}
 */
public class ClassAnnotationDecorator {

    public static AnnotatedClass decorate(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        annotatedClass = addClassAnnotations(annotatedClass, classMapping);
        addFieldAnnotations(annotatedClass, classMapping);
        addMethodAnnotations(annotatedClass, classMapping);
        addConstructorAnnotations(annotatedClass, classMapping);
        AnnotatedClassLogger.log(annotatedClass);
        return annotatedClass;
    }

    private static AnnotatedClass addClassAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        AnnotationMap annotationMap = new AnnotationMap();
        stream(annotatedClass.annotations()).forEach(annotation -> annotationMap.add(annotation));
        classMapping.getAnnotations().values().stream().forEach(annotation -> annotationMap.add(annotation));
        return annotatedClass.withAnnotations(annotationMap);
    }

    private static void addFieldAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        stream(annotatedClass.fields())
                .forEach(annotatedField -> {
                    FieldMapping fieldMapping = classMapping.getOrCreateFieldMapping(annotatedField.getName());
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

    private static void addMethodAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        stream(annotatedClass.memberMethods())
                .forEach(annotatedMethod -> {
                    Optional<MethodMapping> methodMappingOpt = classMapping.getMethodMapping(methodSignature(annotatedMethod.getName(), annotatedMethod.getRawParameterTypes()));
                    methodMappingOpt = Optional.ofNullable(methodMappingOpt
                            .orElse(((Optional<MethodMapping>) classMapping.getMethodMapping(methodSignatureIgnoringParameters(annotatedMethod.getName())))
                                    .orElse(null)));
                    methodMappingOpt.ifPresent(methodMapping -> methodMapping.getAnnotations().values().stream()
                            .forEach(annotation -> annotatedMethod.addOrOverride(annotation)));

                });
    }

    private static void addConstructorAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
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
