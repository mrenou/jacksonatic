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
package org.jacksonatic.internal.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Save Object which defines {@link com.fasterxml.jackson.annotation.JsonSubTypes } to assign type name to children objects if it is not already defined.
 */
public class TypeNameAutoAssigner {

    private Set<Class<Object>> typesWithPolymorphism = new HashSet<>();

    public void saveTypeWithJsonSubTypes(ClassMappingByProcessType classMappingConfigurer) {
        if (classMappingConfigurer.getClassMapping().hasAnnotation(JsonSubTypes.class)) {
            typesWithPolymorphism.add(classMappingConfigurer.getClassMapping().getType());
        }
    }

    public void assignTypeNameIfNeccesary(ClassesMapping classesMapping, ClassMappingByProcessType classMappingConfigurer) {
        if (!classMappingConfigurer.getClassMapping().hasAnnotation(JsonTypeName.class)) {
            typesWithPolymorphism.stream()
                    .filter(typeWithPolymorphism -> typeWithPolymorphism.isAssignableFrom(classMappingConfigurer.getClassMapping().getType()))
                    .findFirst()
                    .ifPresent(typeWithPolymorphism -> classesMapping.getOpt(typeWithPolymorphism)
                            .ifPresent(classMapping -> classMapping.getAnnotationOpt(JsonSubTypes.class)
                                            .ifPresent((jsonSubTypes) -> Arrays.asList(jsonSubTypes.value()).stream()
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
