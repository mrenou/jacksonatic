package org.jacksonatic;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import org.jacksonatic.mapping.ClassesMapping;

import java.util.Optional;

import static org.jacksonatic.annotation.ClassAnnotationDecorator.decorate;

class JacksonaticClassIntrospector extends BasicClassIntrospector {

    private ClassesMapping classesMapping;

    public void register(ClassesMapping parentClassesMapping, ClassesMapping childClassesMapping) {
        this.classesMapping = childClassesMapping.copyWithParentMapping(parentClassesMapping);
    }

    @Override
    protected POJOPropertiesCollector collectProperties(MapperConfig<?> config,
                                                        JavaType type, MixInResolver r, boolean forSerialization,
                                                        String mutatorPrefix) {
        boolean useAnnotations = config.isAnnotationProcessingEnabled();
        AnnotatedClass ac = AnnotatedClass.construct(type.getRawClass(),
                (useAnnotations ? config.getAnnotationIntrospector() : null), r);
        AnnotatedClass acProcessed = Optional.ofNullable(classesMapping.get(ac.getAnnotated())).map(classMapping -> decorate(ac, classMapping)).orElse(ac);
        return constructPropertyCollector(config, acProcessed, type, forSerialization, mutatorPrefix).collect();
    }

}
