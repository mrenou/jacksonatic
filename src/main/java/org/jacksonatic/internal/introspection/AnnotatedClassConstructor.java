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
package org.jacksonatic.internal.introspection;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.util.ClassUtil;
import org.jacksonatic.internal.JacksonaticInternal;
import org.jacksonatic.internal.annotations.ClassAnnotationDecorator;
import org.jacksonatic.internal.mapping.ClassMappingInternal;
import org.jacksonatic.internal.mapping.ClassesMapping;
import org.jacksonatic.internal.util.Mergeable;
import org.jacksonatic.internal.util.TypedHashMap;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * Build {@link com.fasterxml.jackson.databind.introspect.AnnotatedClass} adding annotations defined in class mapping;
 * <p>
 * Class mapping is share in three sources :
 * - standard class mapping  {@link #classesMapping
 * - class mapping defined only for serialization process {@link #serializationOnlyClassesMapping
 * - class mapping defined only for deserialization process {@link #deserializationOnlyClassesMapping
 * <p>
 * Class mapping for serialization and deserialization overrides the standard class mapping.
 * <p>
 * Class mapping can be inherited from class mapping parent (expected when process is NO_SUPER_TYPES). Child class
 * mapping override parent class mapping.
 * <p>
 * When final class mapping is built from all these class mapping, it is saved into {@link #mergedClassesMapping} to
 * avoid a re-computation.
 */
public class AnnotatedClassConstructor {

    private enum ProcessType {SERIALIZATION, DESERIALIZATION, NO_SUPER_TYPES}

    private ClassAnnotationDecorator classAnnotationDecorator = new ClassAnnotationDecorator();

    private ClassesMapping classesMapping;

    private ClassesMapping serializationOnlyClassesMapping;

    private ClassesMapping deserializationOnlyClassesMapping;

    private TypedHashMap<ProcessType, ClassesMapping> mergedClassesMapping = new TypedHashMap<>();

    public AnnotatedClassConstructor(JacksonaticInternal mappingConfigurer) {
        this.classesMapping = mappingConfigurer.getClassesMapping().copy();
        this.serializationOnlyClassesMapping = mappingConfigurer.getSerializationOnlyClassesMapping().copy();
        this.deserializationOnlyClassesMapping = mappingConfigurer.getDeserializationOnlyClassesMapping().copy();
        this.mergedClassesMapping.put(ProcessType.SERIALIZATION, new ClassesMapping());
        this.mergedClassesMapping.put(ProcessType.DESERIALIZATION, new ClassesMapping());
        this.mergedClassesMapping.put(ProcessType.NO_SUPER_TYPES, new ClassesMapping());
    }

    public AnnotatedClass constructForSerialization(Class<?> cls, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir) {
        AnnotatedClass annotatedClass = AnnotatedClass.construct(cls, aintr, mir);
        return processAnnotedClass(ProcessType.SERIALIZATION, annotatedClass);
    }

    public AnnotatedClass constructForDeserialization(Class<?> cls, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir) {
        AnnotatedClass annotatedClass = AnnotatedClass.construct(cls, aintr, mir);
        return processAnnotedClass(ProcessType.DESERIALIZATION, annotatedClass);
    }

    public AnnotatedClass constructWithoutSuperTypes(Class<?> cls, AnnotationIntrospector aintr, ClassIntrospector.MixInResolver mir) {
        AnnotatedClass annotatedClass = AnnotatedClass.construct(cls, aintr, mir);
        return processAnnotedClass(ProcessType.NO_SUPER_TYPES, annotatedClass);
    }

    @SuppressWarnings("unchecked")
    private AnnotatedClass processAnnotedClass(ProcessType processType, AnnotatedClass ac) {
        if (ac.getAnnotated().getName().startsWith("java.")) {
            return ac;
        }
        Class<Object> annotated = (Class<Object>) ac.getAnnotated();
        ClassesMapping childrenClassesMapping = getChildrenClassMapping(processType);
        ClassesMapping mergedClassesMapping = this.mergedClassesMapping.getTyped(processType);

        return Optional.ofNullable(mergedClassesMapping.getOpt(annotated)
                .orElseGet(() -> mergeAndPutInMergedClassesMapping(mergedClassesMapping, annotated,
                        childrenClassesMapping.getOpt(annotated),
                        classesMapping.getOpt(annotated),
                        getClassMappingFromSuperTypes(annotated, childrenClassesMapping, mergedClassesMapping))))
                .map(classMapping -> classAnnotationDecorator.decorate(ac, classMapping))
                .orElse(ac);
    }

    @SuppressWarnings("unchecked")
    private Optional<ClassMappingInternal<Object>> getClassMappingFromSuperTypes(Class<?> type, ClassesMapping childrenClassesMapping, ClassesMapping mergedClassesMapping) {
        return Stream.concat(Stream.of(Object.class), ClassUtil.findSuperTypes(type, Object.class).stream().sorted(Collections.reverseOrder()))
                .map(superType -> {
                            Class<Object> objectSuperType = (Class<Object>) superType;
                            return Optional.ofNullable(
                                    mergedClassesMapping.getOpt(objectSuperType)
                                            .orElseGet(() -> mergeAndPutInMergedClassesMapping(mergedClassesMapping, objectSuperType,
                                                    childrenClassesMapping.getOpt(objectSuperType),
                                                    classesMapping.getOpt(objectSuperType))));
                        }
                )
                .reduce(Optional.empty(), Mergeable::merge);
    }


    private ClassMappingInternal<Object> mergeAndPutInMergedClassesMapping(ClassesMapping mergedClassesMapping, Class<Object> superType, Optional<ClassMappingInternal<Object>>... classMappings) {
        Optional<ClassMappingInternal<Object>> classMappingOpt = Mergeable.merge(classMappings)
                .map(classMapping -> classMapping.getType() != superType ? new ClassMappingInternal<>(superType).mergeWith(classMapping) : classMapping);
        classMappingOpt.ifPresent(classMapping -> mergedClassesMapping.put(superType, classMapping));
        return classMappingOpt.orElse(null);
    }

    private ClassesMapping getChildrenClassMapping(ProcessType processType) {
        if (processType == ProcessType.SERIALIZATION || processType == ProcessType.NO_SUPER_TYPES) {
            return serializationOnlyClassesMapping;
        } else {
            return deserializationOnlyClassesMapping;
        }
    }
}
