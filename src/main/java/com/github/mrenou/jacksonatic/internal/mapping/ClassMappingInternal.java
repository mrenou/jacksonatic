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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.mrenou.jacksonatic.annotation.JacksonaticJsonSubTypesType;
import com.github.mrenou.jacksonatic.internal.annotations.Annotations;
import com.github.mrenou.jacksonatic.internal.mapping.builder.ClassBuilderCriteria;
import com.github.mrenou.jacksonatic.internal.mapping.field.FieldMappingInternal;
import com.github.mrenou.jacksonatic.internal.mapping.method.MethodMappingInternal;
import com.github.mrenou.jacksonatic.internal.mapping.method.MethodSignature;
import com.github.mrenou.jacksonatic.internal.util.Copyable;
import com.github.mrenou.jacksonatic.internal.util.CopyableMergeableHashMap;
import com.github.mrenou.jacksonatic.internal.util.Mergeable;
import com.github.mrenou.jacksonatic.internal.util.StringUtil;
import com.github.mrenou.jacksonatic.mapping.MethodMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.github.mrenou.jacksonatic.annotation.JacksonaticJsonSubTypes.jsonSubTypes;
import static com.github.mrenou.jacksonatic.annotation.JacksonaticJsonTypeInfo.jsonTypeInfo;
import static com.github.mrenou.jacksonatic.annotation.JacksonaticJsonTypeName.jsonTypeName;
import static com.github.mrenou.jacksonatic.internal.mapping.method.MethodSignature.methodSignature;
import static com.github.mrenou.jacksonatic.internal.mapping.method.MethodSignature.methodSignatureIgnoringParameters;
import static com.github.mrenou.jacksonatic.internal.util.StringUtil.firstToUpperCase;
import static com.github.mrenou.jacksonatic.mapping.FieldMapping.field;
import static com.github.mrenou.jacksonatic.mapping.MethodMapping.method;

/**
 * Define annotations class mapping
 *
 * Can map : all fields, specific field or method, class builder (constructor or static factory)
 */
public class ClassMappingInternal<T> implements HasAnnotationsInternal<ClassMappingInternal<T>>, Copyable<ClassMappingInternal<T>>, Mergeable<ClassMappingInternal<T>> {

    private Class<T> type;

    private boolean mapAllFields;

    private Optional<ClassBuilderCriteria> classBuilderCriteriaOpt;

    private CopyableMergeableHashMap<String, FieldMappingInternal> fieldsMapping;

    private CopyableMergeableHashMap<MethodSignature, MethodMappingInternal> methodsMapping;

    private Annotations annotations;

    private TypeChecker<T> typeChecker;

    public ClassMappingInternal(Class<T> type) {
        this(type, false, Optional.empty(), new CopyableMergeableHashMap<>(), new CopyableMergeableHashMap<>(), new Annotations(), new TypeChecker<>(type));
    }

    private ClassMappingInternal(Class<T> type, boolean mapAllFields, Optional<ClassBuilderCriteria> classBuilderCriteriaOpt, CopyableMergeableHashMap<String, FieldMappingInternal> fieldsMapping, CopyableMergeableHashMap<MethodSignature, MethodMappingInternal> methodsMapping, Annotations annotations, TypeChecker<T> typeChecker) {
        this.type = type;
        this.mapAllFields = mapAllFields;
        this.classBuilderCriteriaOpt = classBuilderCriteriaOpt;
        this.fieldsMapping = fieldsMapping;
        this.methodsMapping = methodsMapping;
        this.annotations = annotations;
        this.typeChecker = typeChecker;
    }

    public void mapAllFields() {
        this.mapAllFields = true;
    }

    public void ignore(String fieldName) {
        getOrCreateFieldMappingInternal(fieldName).ignore();
    }

    public void map(String fieldName) {
        getOrCreateFieldMappingInternal(fieldName).map();
    }

    public void map(String fieldName, String mappedName) {
        getOrCreateFieldMappingInternal(fieldName).mapTo(mappedName);
    }

    public void onConstructor(ClassBuilderCriteria classBuilderCriteria) {
        classBuilderCriteriaOpt = Optional.of(classBuilderCriteria);
    }

    public void on(FieldMappingInternal fieldMapping) {
        FieldMappingInternal fieldMappingToStore = fieldsMapping.getOpt(fieldMapping.getName())
                .map(existingFieldMapping -> fieldMapping.mergeWith(existingFieldMapping))
                .orElse(fieldMapping);
        fieldsMapping.put(fieldMapping.getName(), fieldMappingToStore);
    }

    public void on(MethodMappingInternal methodMapping) {
        MethodMappingInternal methodMappingToStore = methodsMapping.getOpt(methodMapping.getMethodSignature())
                .map(existingMethodMapping -> methodMapping.mergeWith(existingMethodMapping))
                .orElse(methodMapping);
        methodsMapping.put(methodMapping.getMethodSignature(), methodMappingToStore);
    }

    public void mapGetter(String fieldName) {
        this.on((MethodMappingInternal) getterFor(fieldName).map());
    }

    public void mapGetter(String fieldName, String jsonProperty) {
        this.on((MethodMappingInternal) getterFor(fieldName).map().mapTo(jsonProperty));
    }

    public void mapSetter(String fieldName) {
        this.on((MethodMappingInternal) setterFor(fieldName).ignoreParameters().map());
    }


    public void mapSetter(String fieldName, String jsonProperty) {
        this.on((MethodMappingInternal) setterFor(fieldName).ignoreParameters().mapTo(jsonProperty));
    }

    public void mapSetter(String fieldName, Class<?>... parameterTypes) {
        this.on((MethodMappingInternal) setterFor(fieldName, parameterTypes).map());
    }

    public void mapSetter(String fieldName, String jsonProperty, Class<?>... parameterTypes) {
        this.on((MethodMappingInternal) setterFor(fieldName, parameterTypes).mapTo(jsonProperty));
    }

    public void ignoreGetter(String fieldName) {
        this.on((MethodMappingInternal) getterFor(fieldName).ignore());
    }


    public void ignoreSetter(String fieldName) {
        this.on((MethodMappingInternal) setterFor(fieldName).ignoreParameters().ignore());
    }

    public void ignoreSetter(String fieldName, Class<?>... parameterTypes) {
        this.on((MethodMappingInternal) setterFor(fieldName, parameterTypes).ignore());
    }

    private MethodMapping getterFor(String fieldName) {
        return method("get" + firstToUpperCase(fieldName));
    }

    private MethodMapping setterFor(String fieldName) {
        return method("set" + firstToUpperCase(fieldName));
    }

    private MethodMapping setterFor(String fieldName, Class<?>... parameterTypes) {
        return method("set" + firstToUpperCase(fieldName), parameterTypes);
    }

    public boolean allFieldsAreMapped() {
        return this.mapAllFields;
    }

    public void fieldForTypeName(String field) {
        annotations.add(jsonTypeInfo().use(JsonTypeInfo.Id.NAME).property(field));
    }

    public void typeName(String name) {
        annotations.add(jsonTypeName(name));
    }

    public void addNamedSubType(Class<? extends T> subType, String name) {
        List<JsonSubTypes.Type> types = Optional.ofNullable(annotations.get(JsonSubTypes.class))
                .map(annotation -> new ArrayList<>(Arrays.asList(((JsonSubTypes) annotation).value())))
                .orElse(new ArrayList<>());
        types.add(JacksonaticJsonSubTypesType.type(name, subType).build());
        annotations.add(jsonSubTypes(types.toArray(new JsonSubTypes.Type[types.size()])));
    }

    public Optional<MethodMappingInternal> getMethodMappingInternal(MethodSignature methodSignature) {
        return methodsMapping.getOpt(methodSignature);
    }

    public Optional<MethodMappingInternal> getSetterMapping(String fieldName, Class<?> fieldType) {
        return findGetterSetterMapping(methodSignature("set" + StringUtil.firstToUpperCase(fieldName), fieldType));
    }

    public Optional<MethodMappingInternal> getGetterMapping(String fieldName) {
        return findGetterSetterMapping(methodSignature("get" + StringUtil.firstToUpperCase(fieldName)));
    }

    private Optional<MethodMappingInternal> findGetterSetterMapping(MethodSignature methodSignature) {
        Optional<MethodMappingInternal> methodMapping = getMethodMappingInternal(methodSignature);
        if (!methodMapping.isPresent()) {
            methodMapping = getMethodMappingInternal(methodSignatureIgnoringParameters(methodSignature.name));
        }
        return methodMapping;
    }

    public FieldMappingInternal getOrCreateFieldMappingInternal(String name) {
        FieldMappingInternal fieldMapping = fieldsMapping.get(name);
        if (fieldMapping == null) {
            fieldMapping = (FieldMappingInternal) field(name);
            fieldsMapping.put(name, fieldMapping);
        }
        return fieldMapping;
    }


    public Class<T> getType() {
        return type;
    }

    public Optional<ClassBuilderCriteria> getClassBuilderCriteriaOpt() {
        return classBuilderCriteriaOpt;
    }

    public void checkTypes() {
        fieldsMapping.forEach((name, fieldMapping) -> typeChecker.checkFieldExists(name));
        methodsMapping.forEach((methodSignature, methodMapping) -> typeChecker.checkMethodExists(methodSignature));
    }

    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    @Override
    public ClassMappingInternal<T> copy() {
        return new ClassMappingInternal<>(type,
                mapAllFields,
                Copyable.copy(classBuilderCriteriaOpt),
                fieldsMapping.copy(),
                methodsMapping.copy(),
                annotations.copy(),
                typeChecker

        );
    }

    @Override
    public ClassMappingInternal<T> mergeWith(ClassMappingInternal<T> parentMapping) {
        return new ClassMappingInternal<>(type,
                mapAllFields | parentMapping.mapAllFields,
                Mergeable.mergeOrCopy(classBuilderCriteriaOpt, parentMapping.classBuilderCriteriaOpt),
                fieldsMapping.mergeWith(parentMapping.fieldsMapping),
                methodsMapping.mergeWith(parentMapping.methodsMapping),
                annotations.mergeWithParent(parentMapping.annotations),
                typeChecker
        );
    }

}
