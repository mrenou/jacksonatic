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
package org.jacksonatic;

import org.jacksonatic.internal.ClassMappingConfigurerInternal;
import org.jacksonatic.internal.mapping.ParameterCriteria;
import org.jacksonatic.mapping.FieldMapping;
import org.jacksonatic.mapping.MethodMapping;

/**
 * Allowing to define jackson class mapping in a programmatic way.
 */
public interface ClassMappingConfigurer<T> {

    static <T> ClassMappingConfigurer<T> type(Class<T> clazz) {
        return new ClassMappingConfigurerInternal<>(clazz);
    }

    /**
     * Start a class mapping for the given type only for serialization
     *
     * @param clazz
     * @return
     */
    static <T> ClassMappingConfigurer<T> onSerializationOf(Class<T> clazz) {
        return type(clazz).onSerialization();
    }

    /**
     * Start a class mapping for the given type only for deserialization
     *
     * @param clazz
     * @return
     */
    static <T> ClassMappingConfigurer<T> onDeserialisationOf(Class<T> clazz) {
        return type(clazz).onDeserialization();
    }


    /**
     * Next class mapping instructions will be only for serialization
     *
     * @return
     */
    ClassMappingConfigurer<T> onSerialization();

    /**
     * Next class mapping instructions will be only for deserialization
     *
     * @return
     */
    ClassMappingConfigurer<T> onDeserialization();

    /**
     * Start a field mapping
     *
     * @param fieldMapping
     * @return
     */
    ClassMappingConfigurer<T> on(FieldMapping fieldMapping);

    ClassMappingConfigurer<T> on(MethodMapping methodMapping);

    /**
     * Map all fields
     *
     * @return
     */
    ClassMappingConfigurer<T> mapAll();

    /**
     * Map the named field
     *
     * @param fieldName
     * @return
     */
    ClassMappingConfigurer<T> map(String fieldName);

    /**
     * Map the named field with another name
     *
     * @param fieldName
     * @param jsonProperty
     * @return
     */
    ClassMappingConfigurer<T> map(String fieldName, String jsonProperty);

    /**
     * Ignore the named field
     *
     * @param fieldName
     * @return
     */
    ClassMappingConfigurer<T> ignore(String fieldName);

    /**
     * Will try to map a constructor or a static factory for the object creation
     *
     * @return
     */
    ClassMappingConfigurer<T> withAConstructorOrStaticFactory();

    /**
     * Will try to map a constructor with these parameters for the object creation
     *
     * @return
     */
    ClassMappingConfigurer<T> withConstructor(ParameterCriteria... parameterCriterias);

    /**
     * Will try to map the named static factory with these parameters for the object creation
     *
     * @return
     */
    ClassMappingConfigurer<T> onStaticFactory(String methodName, ParameterCriteria... parameterCriterias);

    /**
     * Will try to map a static factory with these parameters for the object creation
     *
     * @return
     */
    ClassMappingConfigurer<T> onStaticFactory(ParameterCriteria... parameterCriterias);

    /**
     * Define the field use to store the type name
     *
     * @param field
     * @return
     */
    ClassMappingConfigurer<T> fieldForTypeName(String field);

    /**
     * Define the type name
     *
     * @param name
     * @return
     */
    ClassMappingConfigurer<T> typeName(String name);

    /**
     * Define a subtype with the given type name
     *
     * @param name
     * @return
     */
    ClassMappingConfigurer<T> addNamedSubType(Class<? extends T> subType, String name);


    ClassMappingConfigurer<T> mapGetter(String fieldName);

    ClassMappingConfigurer<T> mapGetter(String fieldName, String jsonProperty);

    ClassMappingConfigurer<T> mapSetter(String fieldName);

    ClassMappingConfigurer<T> mapSetter(String fieldName, String jsonProperty);

    ClassMappingConfigurer<T> mapSetter(String fieldName, Class<?>... parameterTypes);

    ClassMappingConfigurer<T> mapSetter(String fieldName, String jsonProperty, Class<?>... parameterTypes);
}