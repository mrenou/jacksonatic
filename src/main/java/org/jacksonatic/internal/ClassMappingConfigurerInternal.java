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
package org.jacksonatic.internal;

import org.jacksonatic.ClassMappingConfigurer;
import org.jacksonatic.internal.mapping.ClassMapping;
import org.jacksonatic.internal.mapping.FieldMappingInternal;
import org.jacksonatic.internal.mapping.MethodMappingInternal;
import org.jacksonatic.internal.mapping.ParameterCriteria;
import org.jacksonatic.mapping.FieldMapping;
import org.jacksonatic.mapping.MethodMapping;

import static java.util.Arrays.asList;
import static org.jacksonatic.internal.mapping.ClassBuilderCriteria.*;

public class ClassMappingConfigurerInternal<T> implements ClassMappingConfigurer<T> {

    private ClassMapping<T> currentClassMapping;

    private ClassMapping<T> classMapping;

    private ClassMapping<T> serializationOnlyClassMapping;

    private ClassMapping<T> deserializationOnlyClassMapping;

    public ClassMappingConfigurerInternal(Class<T> clazz) {
        classMapping = new ClassMapping<>(clazz);
        serializationOnlyClassMapping = new ClassMapping<>(clazz);
        deserializationOnlyClassMapping = new ClassMapping<>(clazz);
        currentClassMapping = classMapping;
    }

    @Override
    public ClassMappingConfigurer onSerialization() {
        currentClassMapping = serializationOnlyClassMapping;
        return this;
    }

    @Override
    public ClassMappingConfigurer onDeserialization() {
        currentClassMapping = deserializationOnlyClassMapping;
        return this;
    }

    @Override
    public ClassMappingConfigurer on(FieldMapping fieldMapping) {
        currentClassMapping.on((FieldMappingInternal) fieldMapping);
        return this;
    }

    @Override
    public ClassMappingConfigurer on(MethodMapping methodMapping) {
        currentClassMapping.on((MethodMappingInternal) methodMapping);
        return this;
    }

    @Override
    public ClassMappingConfigurer mapAll() {
        currentClassMapping.mapAllFields();
        return this;
    }

    @Override
    public ClassMappingConfigurer map(String fieldName) {
        currentClassMapping.map(fieldName);
        return this;
    }

    @Override
    public ClassMappingConfigurer map(String fieldName, String jsonProperty) {
        currentClassMapping.map(fieldName, jsonProperty);
        return this;
    }

    @Override
    public ClassMappingConfigurer ignore(String fieldName) {
        currentClassMapping.ignore(fieldName);
        return this;
    }

    @Override
    public ClassMappingConfigurer withAConstructorOrStaticFactory() {
        currentClassMapping.onConstructor(mapAConstructorOrStaticFactory());
        return this;
    }

    @Override
    public ClassMappingConfigurer withConstructor(ParameterCriteria... parameterCriterias) {
        currentClassMapping.onConstructor(mapConstructor(currentClassMapping.getType(), asList(parameterCriterias)));
        return this;
    }

    @Override
    public ClassMappingConfigurer onStaticFactory(String methodName, ParameterCriteria... parameterCriterias) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getType(), methodName, asList(parameterCriterias)));
        return this;
    }

    @Override
    public ClassMappingConfigurer onStaticFactory(ParameterCriteria... parameterCriterias) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getType(), asList(parameterCriterias)));
        return this;
    }

    @Override
    public ClassMappingConfigurer fieldForTypeName(String field) {
        currentClassMapping.fieldForTypeName(field);
        return this;
    }

    @Override
    public ClassMappingConfigurer typeName(String name) {
        currentClassMapping.typeName(name);
        return this;
    }

    @Override
    public ClassMappingConfigurer addNamedSubType(Class<? extends T> subType, String name) {
        currentClassMapping.addNamedSubType(subType, name);
        return this;
    }

    @Override
    public ClassMappingConfigurer mapGetter(String fieldName) {
        currentClassMapping.mapGetter(fieldName);
        return this;
    }

    @Override
    public ClassMappingConfigurer mapGetter(String fieldName, String jsonProperty) {
        currentClassMapping.mapGetter(fieldName, jsonProperty);
        return this;
    }

    @Override
    public ClassMappingConfigurer mapSetter(String fieldName) {
        currentClassMapping.mapSetter(fieldName);
        return this;
    }

    @Override
    public ClassMappingConfigurer mapSetter(String fieldName, String jsonProperty) {
        currentClassMapping.mapSetter(fieldName, jsonProperty);
        return this;
    }

    @Override
    public ClassMappingConfigurer mapSetter(String fieldName, Class<?>... parameterTypes) {
        currentClassMapping.mapSetter(fieldName, parameterTypes);
        return this;
    }

    @Override
    public ClassMappingConfigurer mapSetter(String fieldName, String jsonProperty, Class<?>... parameterTypes) {
        currentClassMapping.mapSetter(fieldName, jsonProperty, parameterTypes);
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