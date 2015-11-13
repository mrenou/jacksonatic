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
package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jacksonatic.annotation.Annotations;
import org.jacksonatic.annotation.JacksonaticJsonSubTypesType;
import org.jacksonatic.util.MyHashMap;

import java.util.*;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;
import static org.jacksonatic.annotation.JacksonaticJsonSubTypes.jsonSubTypes;
import static org.jacksonatic.annotation.JacksonaticJsonTypeInfo.jsonTypeInfo;
import static org.jacksonatic.annotation.JacksonaticJsonTypeName.jsonTypeName;
import static org.jacksonatic.mapping.FieldMapping.field;
import static org.jacksonatic.mapping.MethodMapping.method;
import static org.jacksonatic.mapping.MethodSignature.methodSignature;
import static org.jacksonatic.util.ReflectionUtil.getFieldsWithInheritance;
import static org.jacksonatic.util.ReflectionUtil.getMethodsWithInheritance;
import static org.jacksonatic.util.StringUtil.firstToUpperCase;

/**
 * Define class mapping
 */
public class ClassMapping<T> implements HasAnnotations {

    private Class<T> type;

    private boolean allFields;

    private Optional<ClassBuilderCriteria> classBuilderCriteriaOpt;

    private MyHashMap<String, FieldMapping> fieldsMapping;

    private MyHashMap<MethodSignature, MethodMapping> methodsMapping;

    private Annotations annotations;

    private Set<String> existingFieldNames;

    private Set<MethodSignature> existingMethodSignatures;

    private Set<String> existingMethodNames;

    ClassMapping(Class<T> type, boolean allFields, Optional<ClassBuilderCriteria> classBuilderCriteriaOpt, MyHashMap<String, FieldMapping> fieldsMapping, MyHashMap<MethodSignature, MethodMapping> methodsMapping, Annotations annotations) {
        this.type = type;
        this.allFields = allFields;
        this.classBuilderCriteriaOpt = classBuilderCriteriaOpt;
        this.fieldsMapping = fieldsMapping;
        this.methodsMapping = methodsMapping;
        this.annotations = annotations;
        this.existingFieldNames = getFieldsWithInheritance(type).map(field -> field.getName()).collect(toSet());
        this.existingMethodSignatures = getMethodsWithInheritance(type).map(method -> methodSignature(method.getName(), method.getParameterTypes())).collect(toSet());
        this.existingMethodNames = getMethodsWithInheritance(type).map(method -> method.getName()).collect(toSet());
    }

    public ClassMapping(Class<T> type) {
        this(type, false, Optional.empty(), new MyHashMap<>(), new MyHashMap<>(), new Annotations());
    }

    public void mapAllFields() {
        this.allFields = true;
    }

    public void ignore(String fieldName) {
        getOrCreateFieldMapping(fieldName).ignore();
    }

    public void map(String fieldName) {
        getOrCreateFieldMapping(fieldName).map();
    }

    public void map(String fieldName, String mappedName) {
        getOrCreateFieldMapping(fieldName).mapTo(mappedName);
    }

    public void onConstructor(ClassBuilderCriteria classBuilderCriteria) {
        classBuilderCriteriaOpt = Optional.of(classBuilderCriteria);
    }

    public void on(FieldMapping fieldMapping) {
        checkFieldExists(fieldMapping.getName());
        fieldsMapping.put(fieldMapping.getName(), fieldMapping);
    }

    public void on(MethodMapping methodMapping) {
        checkMethodExists(methodMapping.getMethodSignature());
        methodsMapping.put(methodMapping.getMethodSignature(), methodMapping);
    }

    private void checkMethodExists(MethodSignature methodSignature) {
            if (!existingMethodSignatures.contains(methodSignature) && !existingMethodNames.contains(methodSignature.name)) {
                throw new IllegalStateException(String.format("Method with signature '%s' doesn't exist in class mapping %s", methodSignature, type.getName()));
            }
    }

    public void mapGetter(String fieldName) {
        this.on(method("get" + firstToUpperCase(fieldName)).add(jsonProperty()));
    }

    public void mapSetter(String fieldName) {
        this.on(method("set" + firstToUpperCase(fieldName)).ignoreParameters().add(jsonProperty()));
    }

    public void mapSetter(String fieldName, Class<?>... parameterTypes) {
        this.on(method("set" + firstToUpperCase(fieldName), parameterTypes).add(jsonProperty()));
    }

    public boolean allFieldsAreMapped() {
        return this.allFields;
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

    public Optional<MethodMapping> getMethodMapping(MethodSignature methodSignature) {
        return methodsMapping.getOpt(methodSignature);
    }

    public FieldMapping getOrCreateFieldMapping(String name) {
        FieldMapping fieldMapping = fieldsMapping.get(name);
        if (fieldMapping == null) {
            fieldMapping = field(name);
            checkFieldExists(name);
            fieldsMapping.put(name, fieldMapping);
        }
        return fieldMapping;
    }

    private void checkFieldExists(String name) {
        if (!existingFieldNames.contains(name)) {
            throw new IllegalStateException(String.format("Field with name '%s' doesn't exist in class mapping %s", name, type.getName()));
        }
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

    ClassMapping<Object> copy() {
        return new ClassMapping(type,
                allFields,
                Optional.ofNullable(classBuilderCriteriaOpt.map(classBuilderCriteria -> classBuilderCriteria.copy()).orElse(null)),
                fieldsMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy(), (v1, V2) -> {
                    throw new UnsupportedOperationException();
                }, () -> new MyHashMap<>())),
                methodsMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy(), (v1, V2) -> {
                    throw new UnsupportedOperationException();
                }, () -> new MyHashMap<>())),
                annotations.copy()
        );
    }

    public ClassMapping<Object> createChildMapping(Class<Object> childClass) {
        return new ClassMapping(childClass,
                allFields,
                Optional.ofNullable(classBuilderCriteriaOpt.map(classBuilderCriteria -> classBuilderCriteria.copy()).orElse(null)),
                fieldsMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy(), (v1, V2) -> {
                    throw new UnsupportedOperationException();
                }, () -> new MyHashMap<>())),
                methodsMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy(), (v1, V2) -> {
                    throw new UnsupportedOperationException();
                }, () -> new MyHashMap<>())),
                annotations.copy()
        );
    }

    public ClassMapping<Object> copyWithParentMapping(ClassMapping<Object> parentMapping) {
        Optional<ClassBuilderCriteria> newClassBuilderCriteria = Optional.ofNullable(classBuilderCriteriaOpt.map(classBuilderCriteria -> classBuilderCriteria.copy()).orElse(parentMapping.classBuilderCriteriaOpt.map(cm -> cm.copy()).orElse(null)));
        boolean newAllFields = allFields == false ? parentMapping.allFields : allFields;
        MyHashMap<String, FieldMapping> newFieldsMapping = fieldsMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy(), (v1, V2) -> {
            throw new UnsupportedOperationException();
        }, () -> new MyHashMap<>()));
        parentMapping.fieldsMapping.values().stream()
                .map(fieldParentMapping -> Optional.ofNullable(newFieldsMapping.get(fieldParentMapping.getName()))
                        .map(fieldMapping -> fieldMapping.copyWithParentMapping(fieldParentMapping))
                        .orElseGet(() -> fieldParentMapping.copy()))
                .forEach(fieldMapping -> newFieldsMapping.put(fieldMapping.getName(), fieldMapping));

        MyHashMap<MethodSignature, MethodMapping> newMethodsMapping = methodsMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy(), (v1, V2) -> {
            throw new UnsupportedOperationException();
        }, () -> new MyHashMap<>()));
        parentMapping.methodsMapping.values().stream()
                .map(methodParentMapping -> Optional.ofNullable(newMethodsMapping.get(methodParentMapping.getMethodSignature()))
                        .map(methodMapping -> methodMapping.copyWithParentMapping(methodParentMapping))
                        .orElseGet(() -> methodParentMapping.copy()))
                .forEach(methodMapping -> newMethodsMapping.put(methodMapping.getMethodSignature(), methodMapping));

        Annotations newAnnotations = parentMapping.annotations.copy();
        annotations.values().stream().forEach(annotation -> newAnnotations.put(annotation.getClass(), annotation));
        return new ClassMapping(type,
                newAllFields,
                newClassBuilderCriteria,
                newFieldsMapping,
                newMethodsMapping,
                newAnnotations);
    }
}
