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
import org.jacksonatic.mapping.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.jacksonatic.mapping.ClassBuilderFinder.findClassBuilderMapping;

/**
 * Add annotations defined in {@link org.jacksonatic.mapping.ClassMapping} to {@link com.fasterxml.jackson.databind.introspect.AnnotatedClass}
 */
public class ClassAnnotationDecorator {

    public static AnnotatedClass decorate(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        annotatedClass = addClassAnnotations(annotatedClass, classMapping);
        addFieldAnnotations(annotatedClass, classMapping);
        addMethodAnnotations(annotatedClass, classMapping);
        addConstructorAnnotations(annotatedClass, classMapping);

        return annotatedClass;
    }

    private static AnnotatedClass addClassAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        AnnotationMap annotationMap = new AnnotationMap();
        StreamSupport.stream(annotatedClass.annotations().spliterator(), false).forEach(annotation -> annotationMap.add(annotation));
        classMapping.getAnnotations().values().stream().forEach(annotation -> annotationMap.add(annotation));
        return annotatedClass.withAnnotations(annotationMap);
    }

    private static void addFieldAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        StreamSupport.stream(annotatedClass.fields().spliterator(), false)
                .forEach(annotatedField -> {
                    PropertyMapping propertyMapping = classMapping.getOrCreatePropertyMapping(annotatedField.getName());
                    if (classMapping.allPropertiesAreMapped() && !propertyMapping.isMapped() && !propertyMapping.isIgnored()) {
                        propertyMapping.map();
                    }
                    if (!classMapping.allPropertiesAreMapped() && !propertyMapping.isMapped() && !propertyMapping.isIgnored()) {
                        propertyMapping.ignore();
                    }
                    propertyMapping.getAnnotations().values().stream()
                            .forEach(annotation -> annotatedField.addOrOverride(annotation));
                });
    }

    private static void addMethodAnnotations(AnnotatedClass annotatedClass, ClassMapping classMapping) {
        StreamSupport.stream(annotatedClass.memberMethods().spliterator(), false)
                .forEach(annotatedMethod -> ((Optional<MethodMapping>) classMapping.getMethodMapping(new MethodSignature(annotatedMethod.getName(), annotatedMethod.getRawParameterTypes())))
                        .ifPresent(methodMapping -> methodMapping.getAnnotations().values().stream()
                                .forEach(annotation -> annotatedMethod.addOrOverride(annotation))));
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
