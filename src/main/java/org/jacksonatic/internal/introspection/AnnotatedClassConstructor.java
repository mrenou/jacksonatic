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
import org.jacksonatic.internal.mapping.ClassMappingInternal;
import org.jacksonatic.internal.mapping.ClassesMapping;
import org.jacksonatic.internal.util.MyHashMap;

import java.util.List;
import java.util.Optional;

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

    private MyHashMap<ProcessType, ClassesMapping> mergedClassesMapping = new MyHashMap<>();

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

        List<Class<?>> superTypes = ClassUtil.findSuperTypes(ac.getAnnotated(), Object.class);
        superTypes.add(Object.class);
        Optional<ClassMappingInternal<Object>> parentClassMappingOpt = Optional.empty();

        for (int i = superTypes.size() - 1; i >= 0; i--) {
            Class<?> superType = superTypes.get(i);
            Optional<ClassMappingInternal<Object>> mergedClassMapping = Optional.ofNullable(mergedClassesMapping.getTyped(processType).get(superType));
            if (!mergedClassMapping.isPresent()) {
                mergedClassMapping = mergeClassMappings(parentClassMappingOpt,
                        Optional.ofNullable(classesMapping.get(superType)),
                        getExtraClassMappingOpt(processType, superType));
                mergedClassMapping.ifPresent(classMapping -> mergedClassesMapping.getTyped(processType).put((Class<Object>) superType, classMapping));
            }
            if (mergedClassMapping.isPresent()) {
                parentClassMappingOpt = mergedClassMapping;
            }

        }

        Optional<ClassMappingInternal<Object>> finalClassMappingOpt = mergeClassMappings(parentClassMappingOpt,
                Optional.ofNullable(classesMapping.get(ac.getAnnotated())),
                getExtraClassMappingOpt(processType, ac.getAnnotated()))
                .map(finalClassMapping -> {
                    if (finalClassMapping.getType() != ac.getAnnotated()) {
                        return finalClassMapping.createChildMapping((Class<Object>) ac.getAnnotated());
                    }
                    return finalClassMapping;
                });

        return finalClassMappingOpt
                .map(finalClassMapping -> decorate(ac, finalClassMapping))
                .orElse(ac);
    }

    private Optional<ClassMappingInternal<Object>> mergeClassMappings(Optional<ClassMappingInternal<Object>>... classMappings) {
        if (classMappings.length == 0) {
            return Optional.empty();
        }
        Optional<ClassMappingInternal<Object>> finalClassMapping = classMappings[0];
        for (int i = 1; i < classMappings.length; i++) {
            finalClassMapping = copyWithParentMapping(classMappings[i], finalClassMapping);
        }
        return finalClassMapping;
    }

    private Optional<ClassMappingInternal<Object>> copyWithParentMapping(Optional<ClassMappingInternal<Object>> classMappingOpt, Optional<ClassMappingInternal<Object>> parentClassMappingOpt) {
        return classMappingOpt
                .map(classMapping -> parentClassMappingOpt
                        .map(parentClassMapping -> Optional.of(classMapping.mergeWith(parentClassMapping)))
                        .orElse(Optional.of(classMapping)))
                .orElse(parentClassMappingOpt
                        .map(parentClassMapping -> Optional.of(parentClassMapping))
                        .orElse(Optional.empty()));
    }

    private Optional<ClassMappingInternal<Object>> getExtraClassMappingOpt(ProcessType processType, Class<?> clazz) {
        Optional<ClassMappingInternal<Object>> extraClassMapping;
        if (processType == ProcessType.SERIALIZATION || processType == ProcessType.NO_SUPER_TYPES) {
            extraClassMapping = Optional.ofNullable(serializationOnlyClassesMapping.get(clazz));
        } else {
            extraClassMapping = Optional.ofNullable(deserializationOnlyClassesMapping.get(clazz));
        }
        return extraClassMapping;
    }
}
