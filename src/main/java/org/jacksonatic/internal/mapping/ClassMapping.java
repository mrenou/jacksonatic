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
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jacksonatic.annotation.JacksonaticJsonSubTypesType;
import org.jacksonatic.internal.annotations.Annotations;
import org.jacksonatic.internal.util.MyHashMap;
import org.jacksonatic.internal.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.jacksonatic.annotation.JacksonaticJsonSubTypes.jsonSubTypes;
import static org.jacksonatic.annotation.JacksonaticJsonTypeInfo.jsonTypeInfo;
import static org.jacksonatic.annotation.JacksonaticJsonTypeName.jsonTypeName;
import static org.jacksonatic.internal.mapping.MethodSignature.methodSignature;
import static org.jacksonatic.internal.mapping.MethodSignature.methodSignatureIgnoringParameters;
import static org.jacksonatic.internal.util.StringUtil.firstToUpperCase;
import static org.jacksonatic.mapping.FieldMapping.field;
import static org.jacksonatic.mapping.MethodMapping.method;

/**
 * Define class mapping
 */
public class ClassMapping<T> implements HasAnnotationsInternal {

    private Class<T> type;

    private boolean mapAllFields;

    private Optional<ClassBuilderCriteria> classBuilderCriteriaOpt;

    private MyHashMap<String, FieldMappingInternal> fieldsMapping;

    private MyHashMap<MethodSignature, MethodMappingInternal> methodsMapping;

    private Annotations annotations;

    private TypeChecker<T> typeChecker;

    public ClassMapping(Class<T> type) {
        this(type, false, Optional.empty(), new MyHashMap<>(), new MyHashMap<>(), new Annotations(), new TypeChecker<>(type));
    }

    ClassMapping(Class<T> type, boolean mapAllFields, Optional<ClassBuilderCriteria> classBuilderCriteriaOpt, MyHashMap<String, FieldMappingInternal> fieldsMapping, MyHashMap<MethodSignature, MethodMappingInternal> methodsMapping, Annotations annotations, TypeChecker<T> typeChecker) {
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
        typeChecker.checkFieldExists(fieldMapping.getName());
        fieldsMapping.put(fieldMapping.getName(), fieldMapping);
    }

    public void on(MethodMappingInternal methodMapping) {
        typeChecker.checkMethodExists(methodMapping.getMethodSignature());
        methodsMapping.put(methodMapping.getMethodSignature(), methodMapping);
    }

    public void mapGetter(String fieldName) {
        this.on((MethodMappingInternal) method("get" + firstToUpperCase(fieldName)).map());
    }

    public void mapGetter(String fieldName, String jsonProperty) {
        this.on((MethodMappingInternal) method("get" + firstToUpperCase(fieldName)).map().mapTo(jsonProperty));
    }

    public void mapSetter(String fieldName) {
        this.on((MethodMappingInternal) method("set" + firstToUpperCase(fieldName)).ignoreParameters().map());
    }

    public void mapSetter(String fieldName, String jsonProperty) {
        this.on((MethodMappingInternal) method("set" + firstToUpperCase(fieldName)).ignoreParameters().mapTo(jsonProperty));
    }

    public void mapSetter(String fieldName, Class<?>... parameterTypes) {
        this.on((MethodMappingInternal) method("set" + firstToUpperCase(fieldName), parameterTypes).map());
    }

    public void mapSetter(String fieldName, String jsonProperty, Class<?>... parameterTypes) {
        this.on((MethodMappingInternal) method("set" + firstToUpperCase(fieldName), parameterTypes).mapTo(jsonProperty));
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
        return findGetterSetterMapping("set" + StringUtil.firstToUpperCase(fieldName), fieldType);
    }

    public Optional<MethodMappingInternal> getGetterMapping(String fieldName, Class<?> fieldType) {
        return findGetterSetterMapping("get" + StringUtil.firstToUpperCase(fieldName), fieldType);
    }

    private Optional<MethodMappingInternal> findGetterSetterMapping(String methodName, Class<?> fieldType) {
        Optional<MethodMappingInternal> methodMapping = getMethodMappingInternal(methodSignature(methodName, fieldType));
        if (!methodMapping.isPresent()) {
            methodMapping = getMethodMappingInternal(methodSignatureIgnoringParameters(methodName));
        }
        return methodMapping;
    }

    public FieldMappingInternal getOrCreateFieldMappingInternal(String name) {
        FieldMappingInternal fieldMapping = fieldsMapping.get(name);
        if (fieldMapping == null) {
            fieldMapping = (FieldMappingInternal) field(name);
            typeChecker.checkFieldExists(name);
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

    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    ClassMapping<T> copy() {
        return new ClassMapping(type,
                mapAllFields,
                Optional.ofNullable(classBuilderCriteriaOpt.map(classBuilderCriteria -> classBuilderCriteria.copy()).orElse(null)),
                fieldsMapping.copy(fieldMapping -> fieldMapping.copy()),
                methodsMapping.copy(fieldMapping -> fieldMapping.copy()),
                annotations.copy(),
                typeChecker

        );
    }

    public ClassMapping<T> copyWithParentMapping(ClassMapping<T> parentMapping) {
        return new ClassMapping(type,
                mapAllFields | parentMapping.mapAllFields,
                Optional.ofNullable(classBuilderCriteriaOpt.map(classBuilderCriteria -> classBuilderCriteria.copy()).orElse(parentMapping.classBuilderCriteriaOpt.map(cm -> cm.copy()).orElse(null))),
                this.fieldsMapping.mergeWith(parentMapping.fieldsMapping, fieldMapping -> fieldMapping.copy(), (fieldMapping, fieldParentMapping) -> fieldMapping.copyWithParentMapping(fieldParentMapping)),
                this.methodsMapping.mergeWith(parentMapping.methodsMapping, methodMapping -> methodMapping.copy(), (methodMapping, methodParentMapping) -> methodMapping.copyWithParentMapping(methodParentMapping)),
                this.annotations.mergeWith(parentMapping.annotations),
                typeChecker
        );
    }

    public ClassMapping<Object> createChildMapping(Class<Object> childClass) {
        return new ClassMapping(childClass,
                mapAllFields,
                Optional.ofNullable(classBuilderCriteriaOpt.map(classBuilderCriteria -> classBuilderCriteria.copy()).orElse(null)),
                fieldsMapping.copy(fieldMapping -> fieldMapping.copy()),
                methodsMapping.copy(fieldMapping -> fieldMapping.copy()),
                annotations.copy(),
                new TypeChecker(childClass)
        );
    }

}
