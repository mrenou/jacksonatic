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

import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.ParameterCriteria;
import org.jacksonatic.mapping.PropertyMapping;

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
     * Start a property mapping
     * @param propertyMapping
     * @return
     */
    public ClassMappingConfigurer on(PropertyMapping propertyMapping) {
        currentClassMapping.on(propertyMapping);
        return this;
    }

    /**
     * Map all properties
     * @return
     */
    public ClassMappingConfigurer mapAll() {
        currentClassMapping.mapAllProperties();
        return this;
    }

    /**
     * Map the named property
     * @param propertyName
     * @return
     */
    public ClassMappingConfigurer map(String propertyName) {
        currentClassMapping.map(propertyName);
        return this;
    }

    /**
     * Map the named property with another name
     * @param propertyName
     * @param mappedName
     * @return
     */
    public ClassMappingConfigurer map(String propertyName, String mappedName) {
        currentClassMapping.map(propertyName, mappedName);
        return this;
    }

    /**
     * Ignore the named property
     * @param propertyName
     * @return
     */
    public ClassMappingConfigurer ignore(String propertyName) {
        currentClassMapping.ignore(propertyName);
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
     * Define the property use to store the type name
     * @param property
     * @return
     */
    public ClassMappingConfigurer propertyForTypeName(String property) {
        currentClassMapping.propertyForTypeName(property);
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
}