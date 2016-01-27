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


import com.github.mrenou.jacksonatic.internal.JacksonOperation;
import com.github.mrenou.jacksonatic.internal.annotations.Annotations;
import com.github.mrenou.jacksonatic.internal.mapping.field.FieldMappingInternal;
import com.github.mrenou.jacksonatic.internal.mapping.method.MethodMappingInternal;
import com.github.mrenou.jacksonatic.mapping.ClassMapping;
import com.github.mrenou.jacksonatic.mapping.FieldMapping;
import com.github.mrenou.jacksonatic.mapping.MethodMapping;
import com.github.mrenou.jacksonatic.mapping.ParameterCriteria;

import java.util.HashMap;
import java.util.Map;

import static com.github.mrenou.jacksonatic.internal.JacksonOperation.*;
import static com.github.mrenou.jacksonatic.internal.mapping.builder.ClassBuilderCriteria.*;
import static com.github.mrenou.jacksonatic.internal.mapping.builder.parameter.ParameterCriteriaInternal.parameterCriteriaToInternal;

/**
 * Class mapping
 */
public class ClassMappingByOperation<T> implements ClassMapping<T> {

    private ClassMappingInternal<T> currentClassMapping;

    private Map<JacksonOperation, ClassMappingInternal<T>> classMappingByOperation = new HashMap<>();

    public ClassMappingByOperation(Class<T> clazz) {
        classMappingByOperation.put(ANY, new ClassMappingInternal<>(clazz));
        classMappingByOperation.put(SERIALIZATION, new ClassMappingInternal<>(clazz));
        classMappingByOperation.put(DESERIALIZATION, new ClassMappingInternal<>(clazz));
        currentClassMapping = classMappingByOperation.get(ANY);
    }

    @Override
    public ClassMapping<T> onSerialization() {
        currentClassMapping = classMappingByOperation.get(SERIALIZATION);
        return this;
    }

    @Override
    public ClassMapping<T> onDeserialization() {
        currentClassMapping = classMappingByOperation.get(DESERIALIZATION);
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

    @Override
    public ClassMapping<T> ignoreGetter(String fieldName) {
        currentClassMapping.ignoreGetter(fieldName);
        return this;
    }

    @Override
    public ClassMapping<T> ignoreSetter(String fieldName) {
        currentClassMapping.ignoreSetter(fieldName);
        return this;
    }

    @Override
    public ClassMapping<T> ignoreSetter(String fieldName, Class<?>... parameterTypes) {
        currentClassMapping.ignoreSetter(fieldName, parameterTypes);
        return this;
    }

    @Override
    public Annotations getAnnotations() {
        return currentClassMapping.getAnnotations();
    }

    public ClassMappingInternal<T> getClassMappingFor(JacksonOperation operation) {
        return classMappingByOperation.get(operation);
    }
}