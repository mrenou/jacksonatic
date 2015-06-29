package org.jacksonatic;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MyBasicClassIntrospector extends BasicClassIntrospector {

    private Map<Class<?>, ClassMapping<?>> classesMapping = new HashMap<>();

    public void register(ClassMapping<?> classesMapping) {
        this.classesMapping.put(classesMapping.getClazz(), classesMapping);
    }

    @Override
    protected POJOPropertiesCollector collectProperties(MapperConfig<?> config,
                                                        JavaType type, MixInResolver r, boolean forSerialization,
                                                        String mutatorPrefix) {
        boolean useAnnotations = config.isAnnotationProcessingEnabled();
        AnnotatedClass ac = AnnotatedClass.construct(type.getRawClass(),
                (useAnnotations ? config.getAnnotationIntrospector() : null), r);
        AnnotatedClass acProcessed = Optional.ofNullable(classesMapping.get(ac.getAnnotated())).map(classMapping -> ClassProcess.process(ac, classMapping)).orElse(ac);
        return constructPropertyCollector(config, acProcessed, type, forSerialization, mutatorPrefix).collect();
    }

}
