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

import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.FieldMapping;
import org.jacksonatic.mapping.MethodMapping;
import org.jacksonatic.mapping.ParameterCriteria;

import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.jacksonatic.internal.mapping.ClassBuilderCriteria.*;
import static org.jacksonatic.internal.mapping.ParameterCriteriaInternal.parameterCriteriasToInternal;

public class ClassMappingByProcessType<T> implements ClassMapping<T> {

    private ClassMappingInternal currentClassMapping;

    private ClassMappingInternal classMapping;

    private ClassMappingInternal serializationOnlyClassMapping;

    private ClassMappingInternal deserializationOnlyClassMapping;

    public ClassMappingByProcessType(Class<T> clazz) {
        classMapping = new ClassMappingInternal(clazz);
        serializationOnlyClassMapping = new ClassMappingInternal(clazz);
        deserializationOnlyClassMapping = new ClassMappingInternal(clazz);
        currentClassMapping = classMapping;
    }

    @Override
    public ClassMapping onSerialization() {
        currentClassMapping = serializationOnlyClassMapping;
        return this;
    }

    @Override
    public ClassMapping onDeserialization() {
        currentClassMapping = deserializationOnlyClassMapping;
        return this;
    }

    @Override
    public ClassMapping on(FieldMapping fieldMapping) {
        currentClassMapping.on((FieldMappingInternal) fieldMapping);
        return this;
    }

    @Override
    public ClassMapping on(MethodMapping methodMapping) {
        currentClassMapping.on((MethodMappingInternal) methodMapping);
        return this;
    }

    @Override
    public ClassMapping mapAll() {
        currentClassMapping.mapAllFields();
        return this;
    }

    @Override
    public ClassMapping map(String fieldName) {
        currentClassMapping.map(fieldName);
        return this;
    }

    @Override
    public ClassMapping map(String fieldName, String jsonProperty) {
        currentClassMapping.map(fieldName, jsonProperty);
        return this;
    }

    @Override
    public ClassMapping ignore(String fieldName) {
        currentClassMapping.ignore(fieldName);
        return this;
    }

    @Override
    public ClassMapping withAConstructorOrStaticFactory() {
        currentClassMapping.onConstructor(mapAConstructorOrStaticFactory());
        return this;
    }

    @Override
    public ClassMapping withConstructor(ParameterCriteria... parameterCriterias) {
        currentClassMapping.onConstructor(mapConstructor(currentClassMapping.getType(), parameterCriteriasToInternal(parameterCriterias)));
        return this;
    }

    @Override
    public ClassMapping onStaticFactory(String methodName, ParameterCriteria... parameterCriterias) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getType(), methodName, parameterCriteriasToInternal(parameterCriterias)));
        return this;
    }

    @Override
    public ClassMapping onStaticFactory(ParameterCriteria... parameterCriterias) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getType(), parameterCriteriasToInternal(parameterCriterias)));
        return this;
    }

    @Override
    public ClassMapping fieldForTypeName(String field) {
        currentClassMapping.fieldForTypeName(field);
        return this;
    }

    @Override
    public ClassMapping typeName(String name) {
        currentClassMapping.typeName(name);
        return this;
    }

    @Override
    public ClassMapping addNamedSubType(Class<? extends T> subType, String name) {
        currentClassMapping.addNamedSubType(subType, name);
        return this;
    }

    @Override
    public ClassMapping mapGetter(String fieldName) {
        currentClassMapping.mapGetter(fieldName);
        return this;
    }

    @Override
    public ClassMapping mapGetter(String fieldName, String jsonProperty) {
        currentClassMapping.mapGetter(fieldName, jsonProperty);
        return this;
    }

    @Override
    public ClassMapping mapSetter(String fieldName) {
        currentClassMapping.mapSetter(fieldName);
        return this;
    }

    @Override
    public ClassMapping mapSetter(String fieldName, String jsonProperty) {
        currentClassMapping.mapSetter(fieldName, jsonProperty);
        return this;
    }

    @Override
    public ClassMapping mapSetter(String fieldName, Class<?>... parameterTypes) {
        currentClassMapping.mapSetter(fieldName, parameterTypes);
        return this;
    }

    @Override
    public ClassMapping mapSetter(String fieldName, String jsonProperty, Class<?>... parameterTypes) {
        currentClassMapping.mapSetter(fieldName, jsonProperty, parameterTypes);
        return this;
    }

    public ClassMappingInternal getClassMapping() {
        return classMapping;
    }

    public ClassMappingInternal getSerializationOnlyClassMapping() {
        return serializationOnlyClassMapping;
    }

    public ClassMappingInternal getDeserializationOnlyClassMapping() {
        return deserializationOnlyClassMapping;
    }

}