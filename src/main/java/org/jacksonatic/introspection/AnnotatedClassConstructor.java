package org.jacksonatic.introspection;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.fasterxml.jackson.databind.util.ClassUtil;
import org.jacksonatic.ClassMappingConfigurer;
import org.jacksonatic.MappingConfigurer;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.ClassesMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.jacksonatic.annotation.ClassAnnotationDecorator.decorate;

/**
 * Build {@link com.fasterxml.jackson.databind.introspect.AnnotatedClass} adding annotations defined in class mapping;
 *
 * Class mapping is share in three sources :
 * - standard class mapping  {@link #classesMapping
 * - class mapping defined only for serialization process {@link #serializationOnlyClassesMapping
 * - class mapping defined only for deserialization process {@link #deserializationOnlyClassesMapping
 *
 * Class mapping for serialization and deserialization overrides the standard class mapping.
 *
 * Class mapping can be inherited from class mapping parent (expected when process is NO_SUPER_TYPES). Child class
 * mapping override parent class mapping.
 *
 * When final class mapping is built from all these class mapping, it is saved into {@link #mergedClassesMapping} to
 * avoid a re-computation.
 */
public class AnnotatedClassConstructor {

    private enum ProcessType {SERIALIZATION, DESERIALIZATION, NO_SUPER_TYPES}

    private ClassesMapping classesMapping;

    private ClassesMapping serializationOnlyClassesMapping;

    private ClassesMapping deserializationOnlyClassesMapping;

    private Map<ProcessType, ClassesMapping> mergedClassesMapping = new HashMap<>();

    public AnnotatedClassConstructor(MappingConfigurer mappingConfigurer) {
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
        Optional<ClassMapping<Object>> parentClassMappingOpt = Optional.empty();

        for (int i = superTypes.size() - 1; i >= 0; i--) {
            Class<?> superType = superTypes.get(i);
            Optional<ClassMapping<Object>> mergedClassMapping = Optional.ofNullable(mergedClassesMapping.get(processType).get(superType));
            if (!mergedClassMapping.isPresent()) {
                mergedClassMapping = mergeClassMappings(parentClassMappingOpt,
                        Optional.ofNullable(classesMapping.get(superType)),
                        getExtraClassMappingOpt(processType, superType));
                mergedClassMapping.ifPresent(classMapping -> mergedClassesMapping.get(processType).put((Class<Object>) superType, classMapping));
            }
            if (mergedClassMapping.isPresent()) {
                parentClassMappingOpt = mergedClassMapping;
            }

        }

        Optional<ClassMapping<Object>> finalClassMappingOpt = mergeClassMappings(parentClassMappingOpt,
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

    private Optional<ClassMapping<Object>> mergeClassMappings(Optional<ClassMapping<Object>>... classMappings) {
        if (classMappings.length == 0) {
            return Optional.empty();
        }
        Optional<ClassMapping<Object>> finalClassMapping = classMappings[0];
        for (int i = 1; i < classMappings.length; i++) {
            finalClassMapping = copyWithParentMapping(classMappings[i], finalClassMapping);
        }
        return finalClassMapping;
    }

    private Optional<ClassMapping<Object>> copyWithParentMapping(Optional<ClassMapping<Object>> classMappingOpt, Optional<ClassMapping<Object>> parentClassMappingOpt) {
        return classMappingOpt
                .map(classMapping -> parentClassMappingOpt
                        .map(parentClassMapping -> Optional.of(classMapping.copyWithParentMapping(parentClassMapping)))
                        .orElse(Optional.of(classMapping)))
                .orElse(parentClassMappingOpt
                        .map(parentClassMapping -> Optional.of(parentClassMapping))
                        .orElse(Optional.empty()));
    }

    private Optional<ClassMapping<Object>> getExtraClassMappingOpt(ProcessType processType, Class<?> clazz) {
        Optional<ClassMapping<Object>> extraClassMapping;
        if (processType == ProcessType.SERIALIZATION || processType == ProcessType.NO_SUPER_TYPES) {
            extraClassMapping = Optional.ofNullable(serializationOnlyClassesMapping.get(clazz));
        } else {
            extraClassMapping = Optional.ofNullable(deserializationOnlyClassesMapping.get(clazz));
        }
        return extraClassMapping;
    }
}
