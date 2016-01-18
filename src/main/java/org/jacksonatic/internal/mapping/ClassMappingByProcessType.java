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
package org.jacksonatic.internal.mapping;

import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.FieldMapping;
import org.jacksonatic.mapping.MethodMapping;
import org.jacksonatic.mapping.ParameterCriteria;

import static org.jacksonatic.internal.mapping.ClassBuilderCriteria.*;
import static org.jacksonatic.internal.mapping.ParameterCriteriaInternal.parameterCriteriaToInternal;

public class ClassMappingByProcessType<T> implements ClassMapping<T> {

    private ClassMappingInternal<T> currentClassMapping;

    private ClassMappingInternal<T> classMapping;

    private ClassMappingInternal<T> serializationOnlyClassMapping;

    private ClassMappingInternal<T> deserializationOnlyClassMapping;

    public ClassMappingByProcessType(Class<T> clazz) {
        classMapping = new ClassMappingInternal<>(clazz);
        serializationOnlyClassMapping = new ClassMappingInternal<>(clazz);
        deserializationOnlyClassMapping = new ClassMappingInternal<>(clazz);
        currentClassMapping = classMapping;
    }

    @Override
    public ClassMapping<T> onSerialization() {
        currentClassMapping = serializationOnlyClassMapping;
        return this;
    }

    @Override
    public ClassMapping<T> onDeserialization() {
        currentClassMapping = deserializationOnlyClassMapping;
        return this;
    }

    @Override
    public ClassMapping<T> on(FieldMapping fieldMapping) {
        currentClassMapping.on((FieldMappingInternal) fieldMapping);
        return this;
    }

    @Override
    public ClassMapping<T> on(MethodMapping methodMapping) {
        currentClassMapping.on((MethodMappingInternal) methodMapping);
        return this;
    }

    @Override
    public ClassMapping<T> mapAll() {
        currentClassMapping.mapAllFields();
        return this;
    }

    @Override
    public ClassMapping<T> map(String fieldName) {
        currentClassMapping.map(fieldName);
        return this;
    }

    @Override
    public ClassMapping<T> map(String fieldName, String jsonProperty) {
        currentClassMapping.map(fieldName, jsonProperty);
        return this;
    }

    @Override
    public ClassMapping<T> ignore(String fieldName) {
        currentClassMapping.ignore(fieldName);
        return this;
    }

    @Override
    public ClassMapping<T> withAConstructorOrStaticFactory() {
        currentClassMapping.onConstructor(mapAConstructorOrStaticFactory());
        return this;
    }

    @Override
    public ClassMapping<T> withConstructor(ParameterCriteria... parameterCriteriaList) {
        currentClassMapping.onConstructor(mapConstructor(currentClassMapping.getType(), parameterCriteriaToInternal(parameterCriteriaList)));
        return this;
    }

    @Override
    public ClassMapping<T> onStaticFactory(String methodName, ParameterCriteria... parameterCriteriaList) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getType(), methodName, parameterCriteriaToInternal(parameterCriteriaList)));
        return this;
    }

    @Override
    public ClassMapping<T> onStaticFactory(ParameterCriteria... parameterCriteriaList) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getType(), parameterCriteriaToInternal(parameterCriteriaList)));
        return this;
    }

    @Override
    public ClassMapping<T> fieldForTypeName(String field) {
        currentClassMapping.fieldForTypeName(field);
        return this;
    }

    @Override
    public ClassMapping<T> typeName(String name) {
        currentClassMapping.typeName(name);
        return this;
    }

    @Override
    public ClassMapping<T> addNamedSubType(Class<? extends T> subType, String name) {
        currentClassMapping.addNamedSubType(subType, name);
        return this;
    }

    @Override
    public ClassMapping<T> mapGetter(String fieldName) {
        currentClassMapping.mapGetter(fieldName);
        return this;
    }

    @Override
    public ClassMapping<T> mapGetter(String fieldName, String jsonProperty) {
        currentClassMapping.mapGetter(fieldName, jsonProperty);
        return this;
    }

    @Override
    public ClassMapping<T> mapSetter(String fieldName) {
        currentClassMapping.mapSetter(fieldName);
        return this;
    }

    @Override
    public ClassMapping<T> mapSetter(String fieldName, String jsonProperty) {
        currentClassMapping.mapSetter(fieldName, jsonProperty);
        return this;
    }

    @Override
    public ClassMapping<T> mapSetter(String fieldName, Class<?>... parameterTypes) {
        currentClassMapping.mapSetter(fieldName, parameterTypes);
        return this;
    }

    @Override
    public ClassMapping<T> mapSetter(String fieldName, String jsonProperty, Class<?>... parameterTypes) {
        currentClassMapping.mapSetter(fieldName, jsonProperty, parameterTypes);
        return this;
    }

    public ClassMappingInternal<T> getClassMapping() {
        return classMapping;
    }

    public ClassMappingInternal<T> getSerializationOnlyClassMapping() {
        return serializationOnlyClassMapping;
    }

    public ClassMappingInternal<T> getDeserializationOnlyClassMapping() {
        return deserializationOnlyClassMapping;
    }

}