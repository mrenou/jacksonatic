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
package org.jacksonatic;

import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.FieldMapping;
import org.jacksonatic.mapping.MethodMapping;
import org.jacksonatic.mapping.ParameterCriteria;

import static java.util.Arrays.asList;
import static org.jacksonatic.mapping.ClassBuilderCriteria.*;

/**
 * Allowing to define jackson class mapping in a programmatic way.
 */
public class ClassMappingConfigurer<T> {

    private ClassMapping<T> currentClassMapping;

    private ClassMapping<T> classMapping;

    private ClassMapping<T> serializationOnlyClassMapping;

    private ClassMapping<T> deserializationOnlyClassMapping;

    /**
     * Start a class mapping for the given type
     * @param clazz
     * @return
     */
    public static ClassMappingConfigurer type(Class<?> clazz) {
        return new ClassMappingConfigurer(clazz);
    }

    /**
     * Start a class mapping for the given type only for serialization
     * @param clazz
     * @return
     */
    public static ClassMappingConfigurer onSerializationOf(Class<?> clazz) {
        return type(clazz).onSerialization();
    }

    /**
     * Start a class mapping for the given type only for deserialization
     * @param clazz
     * @return
     */
    public static ClassMappingConfigurer onDeserialisationOf(Class<?> clazz) {
        return type(clazz).onDeserialization();
    }

    private ClassMappingConfigurer(Class<T> clazz) {
        classMapping = new ClassMapping<>(clazz);
        serializationOnlyClassMapping = new ClassMapping<>(clazz);
        deserializationOnlyClassMapping = new ClassMapping<>(clazz);
        currentClassMapping = classMapping;
    }

    /**
     * Next class mapping instructions will be only for serialization
     * @return
     */
    public ClassMappingConfigurer onSerialization() {
        currentClassMapping = serializationOnlyClassMapping;
        return this;
    }

    /**
     * Next class mapping instructions will be only for deserialization
     * @return
     */
    public ClassMappingConfigurer onDeserialization() {
        currentClassMapping = deserializationOnlyClassMapping;
        return this;
    }

    /**
     * Start a field mapping
     * @param fieldMapping
     * @return
     */
    public ClassMappingConfigurer on(FieldMapping fieldMapping) {
        currentClassMapping.on(fieldMapping);
        return this;
    }

    public ClassMappingConfigurer on(MethodMapping methodMapping) {
        currentClassMapping.on(methodMapping);
        return this;
    }

    /**
     * Map all fields
     * @return
     */
    public ClassMappingConfigurer mapAll() {
        currentClassMapping.mapAllFields();
        return this;
    }

    /**
     * Map the named field
     * @param fieldName
     * @return
     */
    public ClassMappingConfigurer map(String fieldName) {
        currentClassMapping.map(fieldName);
        return this;
    }

    /**
     * Map the named field with another name
     * @param fieldName
     * @param mappedName
     * @return
     */
    public ClassMappingConfigurer map(String fieldName, String mappedName) {
        currentClassMapping.map(fieldName, mappedName);
        return this;
    }

    /**
     * Ignore the named field
     * @param fieldName
     * @return
     */
    public ClassMappingConfigurer ignore(String fieldName) {
        currentClassMapping.ignore(fieldName);
        return this;
    }

    /**
     * Will try to map a constructor or a static factory for the object creation
     * @return
     */
    public ClassMappingConfigurer withAConstructorOrStaticFactory() {
        currentClassMapping.onConstructor(mapAConstructorOrStaticFactory());
        return this;
    }

    /**
     * Will try to map a constructor with these parameters for the object creation
     * @return
     */
    public ClassMappingConfigurer withConstructor(ParameterCriteria... parameterCriterias) {
        currentClassMapping.onConstructor(mapConstructor(currentClassMapping.getType(), asList(parameterCriterias)));
        return this;
    }

    /**
     * Will try to map the named static factory with these parameters for the object creation
     * @return
     */
    public ClassMappingConfigurer onStaticFactory(String methodName, ParameterCriteria... parameterCriterias) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getType(), methodName, asList(parameterCriterias)));
        return this;
    }

    /**
     * Will try to map a static factory with these parameters for the object creation
     * @return
     */
    public ClassMappingConfigurer onStaticFactory(ParameterCriteria... parameterCriterias) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getType(), asList(parameterCriterias)));
        return this;
    }

    /**
     * Define the field use to store the type name
     * @param field
     * @return
     */
    public ClassMappingConfigurer fieldForTypeName(String field) {
        currentClassMapping.fieldForTypeName(field);
        return this;
    }

    /**
     * Define the type name
     * @param name
     * @return
     */
    public ClassMappingConfigurer typeName(String name) {
        currentClassMapping.typeName(name);
        return this;
    }

    /**
     * Define a subtype with the given type name
     * @param name
     * @return
     */
    public ClassMappingConfigurer addNamedSubType(Class<? extends T> subType, String name) {
        currentClassMapping.addNamedSubType(subType, name);
        return this;
    }

    public ClassMapping<T> getClassMapping() {
        return classMapping;
    }

    public ClassMapping<T> getSerializationOnlyClassMapping() {
        return serializationOnlyClassMapping;
    }

    public ClassMapping<T> getDeserializationOnlyClassMapping() {
        return deserializationOnlyClassMapping;
    }

    public ClassMappingConfigurer mapGetter(String fieldName) {
        currentClassMapping.mapGetter(fieldName);
        return this;
    }

    public ClassMappingConfigurer mapSetter(String fieldName) {
        currentClassMapping.mapSetter(fieldName);
        return this;
    }

    public ClassMappingConfigurer mapSetter(String fieldName, Class<?>... parameterTypes) {
        currentClassMapping.mapSetter(fieldName, parameterTypes);
        return this;
    }
}