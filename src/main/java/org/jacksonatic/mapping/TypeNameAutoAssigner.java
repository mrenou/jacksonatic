package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jacksonatic.ClassMappingConfigurer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Save Object which defines {@link com.fasterxml.jackson.annotation.JsonSubTypes } to assign type name to children objects if it is not already defined.
 */
public class TypeNameAutoAssigner {

    private Set<Class<Object>> typesWithPolymorphism = new HashSet<>();

    public void saveTypeWithJsonSubTypes(ClassMappingConfigurer classMappingConfigurer) {
        if (classMappingConfigurer.getClassMapping().hasAnnotation(JsonSubTypes.class)) {
            typesWithPolymorphism.add(classMappingConfigurer.getClassMapping().getType());
        }
    }

    public void assignTypeNameIfNeccesary(ClassesMapping classesMapping, ClassMappingConfigurer classMappingConfigurer) {
        if (!classMappingConfigurer.getClassMapping().hasAnnotation(JsonTypeName.class)) {
            typesWithPolymorphism.stream()
                    .filter(typeWithPolymorphism -> typeWithPolymorphism.isAssignableFrom(classMappingConfigurer.getClassMapping().getType()))
                    .findFirst()
                    .ifPresent(typeWithPolymorphism -> classesMapping.getOpt(typeWithPolymorphism)
                                    .ifPresent(classMapping -> classMapping.getAnnotationOpt(JsonSubTypes.class)
                                                    .ifPresent(jsonSubTypes -> Arrays.asList(jsonSubTypes.value()).stream()
                                                                    .filter(subtype -> subtype.value().equals(classMappingConfigurer.getClassMapping().getType()))
                                                                    .findFirst()
                                                                    .map(subtype -> subtype.name())
                                                                    .ifPresent(typeName -> classMappingConfigurer.getClassMapping().typeName(typeName))
                                                    )
                                    )
                    );
        }
    }
}
