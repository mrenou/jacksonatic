package org.jacksonatic;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.ClassesMapping;

import java.util.Optional;
import java.util.function.Function;

import static org.jacksonatic.annotation.ClassAnnotationDecorator.decorate;

class JacksonaticClassIntrospector extends BasicClassIntrospector {

    private Function<Class<Object>, ClassMappingConfigurer<Object>> baseClassMappingProducer;

    private ClassesMapping classesMapping;

    private ClassesMapping serializationOnlyClassesMapping;

    private ClassesMapping deserializationOnlyClassesMapping;

    public void register(Function<Class<Object>, ClassMappingConfigurer<Object>> baseClassMappingProducer, MappingConfigurer mappingConfigurer) {
        this.baseClassMappingProducer = baseClassMappingProducer;
        this.classesMapping = mappingConfigurer.classesMapping.copy();
        this.serializationOnlyClassesMapping = mappingConfigurer.serializationOnlyClassesMapping.copy();
        this.deserializationOnlyClassesMapping = mappingConfigurer.deserializationOnlyClassesMapping.copy();
    }

    @Override
    protected POJOPropertiesCollector collectProperties(MapperConfig<?> config,
                                                        JavaType type, MixInResolver r, boolean forSerialization,
                                                        String mutatorPrefix) {
        boolean useAnnotations = config.isAnnotationProcessingEnabled();
        AnnotatedClass ac = AnnotatedClass.construct(type.getRawClass(),
                (useAnnotations ? config.getAnnotationIntrospector() : null), r);
        if (!ac.getAnnotated().getName().startsWith("java.")) {
            ac = processAnnotecClass(forSerialization, ac);
        }

        return constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix).collect();
    }

    private AnnotatedClass processAnnotecClass(boolean forSerialization, AnnotatedClass ac) {
        Optional<ClassMapping<Object>> baseClassMappingOpt = getBaseClassMappingOpt(baseClassMappingProducer.apply((Class<Object>) ac.getAnnotated()), forSerialization);
        Optional<ClassMapping<Object>> classMappingOpt = Optional.ofNullable(classesMapping.get(ac.getAnnotated()));
        Optional<ClassMapping<Object>> extraClassMappingOpt = getExtraClassMappingOpt(forSerialization, ac);

        return extraClassMappingOpt
                .map(childClassMapping -> Optional.of(classMappingOpt
                        .map(classMapping -> childClassMapping.mergeWithParentMapping(classMapping))
                        .orElse(childClassMapping)))
                .orElse(classMappingOpt)
                .map(classMapping -> Optional.of(baseClassMappingOpt
                        .map(baseClassMapping -> classMapping.mergeWithParentMapping(baseClassMapping))
                        .orElse(classMapping)))
                .orElse(baseClassMappingOpt)
                .map(finalClassMapping -> decorate(ac, finalClassMapping))
                .orElse(ac);
    }

    private Optional<ClassMapping<Object>> getExtraClassMappingOpt(boolean forSerialization, AnnotatedClass ac) {
        Optional<ClassMapping<Object>> extraClassMapping;
        if (forSerialization) {
            extraClassMapping = Optional.ofNullable(serializationOnlyClassesMapping.get(ac.getAnnotated()));
        } else {
            extraClassMapping = Optional.ofNullable(deserializationOnlyClassesMapping.get(ac.getAnnotated()));
        }
        return extraClassMapping;
    }

    private Optional<ClassMapping<Object>> getBaseClassMappingOpt(ClassMappingConfigurer<Object> baseClassMappingConfigurer, boolean forSerialization) {
        if (forSerialization) {
            return Optional.ofNullable(
                    Optional.ofNullable(baseClassMappingConfigurer)
                    .map(classesMappingConfigurer -> classesMappingConfigurer.getSerializationOnlyClassMapping().mergeWithParentMapping(baseClassMappingConfigurer.getClassMapping()))
                    .orElse(null)
            );
        } else {
            return Optional.ofNullable(
                    Optional.ofNullable(baseClassMappingConfigurer)
                    .map(classesMappingConfigurer -> classesMappingConfigurer.getDeserializationOnlyClassMapping().mergeWithParentMapping(baseClassMappingConfigurer.getClassMapping()))
                    .orElse(null)
            );
        }
    }

}
