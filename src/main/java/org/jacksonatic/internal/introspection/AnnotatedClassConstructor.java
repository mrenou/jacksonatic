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
package org.jacksonatic.internal.introspection;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.util.ClassUtil;
import org.jacksonatic.internal.JacksonaticInternal;
import org.jacksonatic.internal.mapping.ClassMappingInternal;
import org.jacksonatic.internal.mapping.ClassesMapping;
import org.jacksonatic.internal.util.Mergeable;
import org.jacksonatic.internal.util.TypedHashMap;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.jacksonatic.internal.annotations.ClassAnnotationDecorator.decorate;

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

    private AnnotatedClass processAnnotedClass(ProcessType processType, AnnotatedClass ac) {
        if (ac.getAnnotated().getName().startsWith("java.")) {
            return ac;
        }
        ClassesMapping mergedClassesMapping = this.mergedClassesMapping.getTyped(processType);
        ClassesMapping childrenClassesMapping = getChildenClassMapping(processType);

        return Optional.ofNullable(mergedClassesMapping.getOpt((Class<Object>) ac.getAnnotated())
                .orElseGet(() -> mergeAndPutInMergedClassesMapping(mergedClassesMapping, ac.getAnnotated(),
                        childrenClassesMapping.getOpt((Class<Object>) ac.getAnnotated()),
                        classesMapping.getOpt((Class<Object>) ac.getAnnotated()),
                        getClassMappingFromSuperTypes(ac.getAnnotated(), childrenClassesMapping, mergedClassesMapping))))
                .map(classMapping -> decorate(ac, classMapping))
                .orElse(ac);
    }

    private Optional<ClassMappingInternal<Object>> getClassMappingFromSuperTypes(Class<?> type, ClassesMapping childrenClassesMapping, ClassesMapping mergedClassesMapping) {
        return Stream.concat(Stream.of(Object.class), ClassUtil.findSuperTypes(type, Object.class).stream().sorted(Collections.reverseOrder()))
                .map(superType -> Optional.ofNullable(
                        mergedClassesMapping.getOpt((Class<Object>) superType)
                                .orElseGet(() -> mergeAndPutInMergedClassesMapping(mergedClassesMapping, superType,
                                        childrenClassesMapping.getOpt((Class<Object>) superType),
                                        classesMapping.getOpt((Class<Object>) superType))))
                )
                .reduce(Optional.empty(), Mergeable::merge);
    }

    private ClassMappingInternal<Object> mergeAndPutInMergedClassesMapping(ClassesMapping mergedClassesMapping, Class<?> superType, Optional<ClassMappingInternal<Object>>... classMappings) {
        Optional<ClassMappingInternal<Object>> classMappingOpt = Mergeable.merge(classMappings)
                .map(classMapping -> classMapping.getType() != superType ? new ClassMappingInternal<>((Class<Object>) superType).mergeWith(classMapping) : classMapping);
        classMappingOpt.ifPresent(classMapping -> mergedClassesMapping.put((Class<Object>) superType, classMapping));
        return classMappingOpt.orElse(null);
    }

    private ClassesMapping getChildenClassMapping(ProcessType processType) {
        if (processType == ProcessType.SERIALIZATION || processType == ProcessType.NO_SUPER_TYPES) {
            return serializationOnlyClassesMapping;
        } else {
            return deserializationOnlyClassesMapping;
        }
    }
}
