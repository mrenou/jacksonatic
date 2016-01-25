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
import org.jacksonatic.internal.JacksonOperation;
import org.jacksonatic.internal.JacksonaticInternal;
import org.jacksonatic.internal.annotations.ClassAnnotationDecorator;
import org.jacksonatic.internal.mapping.ClassMappingInternal;
import org.jacksonatic.internal.mapping.ClassesMapping;
import org.jacksonatic.internal.util.Mergeable;
import org.jacksonatic.internal.util.TypedHashMap;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static org.jacksonatic.internal.JacksonOperation.*;


/**
 * Build {@link com.fasterxml.jackson.databind.introspect.AnnotatedClass} adding annotations defined in class mapping;
 * <p>
 * Class mapping is share in three sources :
 * - standard class mapping {@link org.jacksonatic.internal.JacksonOperation#ANY}
 * - class mapping defined only for serialization process {@link org.jacksonatic.internal.JacksonOperation#SERIALIZATION}
 * - class mapping defined only for deserialization process {@link org.jacksonatic.internal.JacksonOperation#DESERIALIZATION}
 * <p>
 * Class mapping for serialization and deserialization overrides the standard class mapping.
 * <p>
 * Class mapping can be inherited from class mapping parent (expected when process is
 * {@link org.jacksonatic.internal.JacksonOperation#NO_SUPER_TYPES}). Child class mapping override parent class mapping.
 * <p>
 * When final class mapping is built from all these class mapping, it is saved into {@link #mergedClassesMappingByOperation } to
 * avoid a re-computation.
 */
public class AnnotatedClassConstructor {

    private ClassAnnotationDecorator classAnnotationDecorator = new ClassAnnotationDecorator();

    private TypedHashMap<JacksonOperation, ClassesMapping> classesMappingByOperation = new TypedHashMap<>();

    private TypedHashMap<JacksonOperation, ClassesMapping> mergedClassesMappingByOperation = new TypedHashMap<>();

    public AnnotatedClassConstructor(JacksonaticInternal mappingConfigurer) {
        this.classesMappingByOperation = mappingConfigurer.getClassesMappingByOperation().copy();
        this.mergedClassesMappingByOperation.put(JacksonOperation.SERIALIZATION, new ClassesMapping());
        this.mergedClassesMappingByOperation.put(JacksonOperation.DESERIALIZATION, new ClassesMapping());
        this.mergedClassesMappingByOperation.put(JacksonOperation.NO_SUPER_TYPES, new ClassesMapping());
    }

    public AnnotatedClass constructForSerialization(Class<?> cls, AnnotationIntrospector annotationIntrospector, ClassIntrospector.MixInResolver mir) {
        AnnotatedClass annotatedClass = AnnotatedClass.construct(cls, annotationIntrospector, mir);
        return processAnnotatedClass(JacksonOperation.SERIALIZATION, annotatedClass);
    }

    public AnnotatedClass constructForDeserialization(Class<?> cls, AnnotationIntrospector annotationIntrospector, ClassIntrospector.MixInResolver mir) {
        AnnotatedClass annotatedClass = AnnotatedClass.construct(cls, annotationIntrospector, mir);
        return processAnnotatedClass(JacksonOperation.DESERIALIZATION, annotatedClass);
    }

    public AnnotatedClass constructWithoutSuperTypes(Class<?> cls, AnnotationIntrospector annotationIntrospector, ClassIntrospector.MixInResolver mir) {
        AnnotatedClass annotatedClass = AnnotatedClass.construct(cls, annotationIntrospector, mir);
        return processAnnotatedClass(JacksonOperation.NO_SUPER_TYPES, annotatedClass);
    }

    @SuppressWarnings("unchecked")
    private AnnotatedClass processAnnotatedClass(JacksonOperation processType, AnnotatedClass ac) {
        if (ac.getAnnotated().getName().startsWith("java.")) {
            return ac;
        }
        Class<Object> annotated = (Class<Object>) ac.getAnnotated();
        ClassesMapping serOrDeserClassesMapping = getSerOrDeserClassMapping(processType);
        ClassesMapping mergedClassesMapping = this.mergedClassesMappingByOperation.getTyped(processType);

        return Optional.ofNullable(mergedClassesMapping.getOpt(annotated)
                .orElseGet(() -> mergeAndPutInMergedClassesMapping(mergedClassesMapping, annotated,
                        serOrDeserClassesMapping.getOpt(annotated),
                        classesMappingByOperation.get(ANY).getOpt(annotated),
                        getClassMappingFromSuperTypes(annotated, serOrDeserClassesMapping, mergedClassesMapping))))
                .map(classMapping -> classAnnotationDecorator.decorate(ac, classMapping))
                .orElse(ac);
    }

    @SuppressWarnings("unchecked")
    private Optional<ClassMappingInternal<Object>> getClassMappingFromSuperTypes(Class<?> type, ClassesMapping serOrDeserClassesMapping, ClassesMapping mergedClassesMapping) {
        return Stream.concat(Stream.of(Object.class), ClassUtil.findSuperTypes(type, Object.class).stream().sorted(Collections.reverseOrder()))
                .map(superType -> (Class<Object>) superType)
                .map(superType -> Optional.ofNullable(
                                mergedClassesMapping.getOpt(superType)
                                        .orElseGet(() -> mergeAndPutInMergedClassesMapping(mergedClassesMapping, superType,
                                                serOrDeserClassesMapping.getOpt(superType),
                                                classesMappingByOperation.get(ANY).getOpt(superType))))
                )
                .reduce(Optional.empty(), Mergeable::merge);
    }

    @SafeVarargs
    private final ClassMappingInternal<Object> mergeAndPutInMergedClassesMapping(ClassesMapping mergedClassesMapping, Class<Object> superType, Optional<ClassMappingInternal<Object>>... classMappings) {
        Optional<ClassMappingInternal<Object>> classMappingOpt = Mergeable.merge(classMappings)
                .map(classMapping -> classMapping.getType() != superType ? new ClassMappingInternal<>(superType).mergeWith(classMapping) : classMapping);
        classMappingOpt.ifPresent(classMapping -> mergedClassesMapping.put(superType, classMapping));
        return classMappingOpt.orElse(null);
    }

    private ClassesMapping getSerOrDeserClassMapping(JacksonOperation processType) {
        if (processType == JacksonOperation.SERIALIZATION || processType == JacksonOperation.NO_SUPER_TYPES) {
            return classesMappingByOperation.get(SERIALIZATION);
        } else {
            return classesMappingByOperation.get(DESERIALIZATION);
        }
    }
}
