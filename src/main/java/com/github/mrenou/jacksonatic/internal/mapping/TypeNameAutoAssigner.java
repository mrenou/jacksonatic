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
package com.github.mrenou.jacksonatic.internal.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Save Object which defines {@link com.fasterxml.jackson.annotation.JsonSubTypes } to assign type name to children objects if it is not already defined.
 */
public class TypeNameAutoAssigner {

    private Set<Class<Object>> typesWithPolymorphism = new HashSet<>();

    public void saveTypeWithJsonSubTypes(ClassMappingInternal<Object> currentClassMapping) {
        if (currentClassMapping.hasAnnotation(JsonSubTypes.class)) {
            typesWithPolymorphism.add(currentClassMapping.getType());
        }
    }

    public void assignTypeNameIfNecessary(ClassesMapping classesMapping, ClassMappingInternal<Object> currentClassMapping) {
        if (!currentClassMapping.hasAnnotation(JsonTypeName.class)) {
            typesWithPolymorphism.stream()
                    .filter(typeWithPolymorphism -> typeWithPolymorphism.isAssignableFrom(currentClassMapping.getType()))
                    .findFirst()
                    .ifPresent(typeWithPolymorphism -> classesMapping.getOpt(typeWithPolymorphism)
                            .ifPresent(classMapping -> classMapping.getAnnotationOpt(JsonSubTypes.class)
                                    .ifPresent((jsonSubTypes) -> Arrays.asList(jsonSubTypes.value()).stream()
                                            .filter(subtype -> subtype.value().equals(classMapping.getType()))
                                            .findFirst()
                                            .map(JsonSubTypes.Type::name)
                                            .ifPresent(classMapping::typeName)
                                    )
                            )
                    );
        }
    }
}
