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
package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jacksonatic.annotation.Annotations;
import org.jacksonatic.annotation.JacksonaticJsonSubTypesType;
import org.jacksonatic.util.MyHashMap;

import java.lang.reflect.Field;
import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;
import static org.jacksonatic.annotation.JacksonaticJsonSubTypes.jsonSubTypes;
import static org.jacksonatic.annotation.JacksonaticJsonTypeInfo.jsonTypeInfo;
import static org.jacksonatic.annotation.JacksonaticJsonTypeName.jsonTypeName;
import static org.jacksonatic.mapping.MethodMapping.method;
import static org.jacksonatic.mapping.PropertyMapping.property;
import static org.jacksonatic.util.ReflectionUtil.getPropertiesWithInheritance;
import static org.jacksonatic.util.StringUtil.firstToUpperCase;

/**
 * Define class mapping
 */
public class ClassMapping<T> implements HasAnnotations {

    private Class<T> type;

    private boolean allProperties;

    private Optional<ClassBuilderCriteria> classBuilderCriteriaOpt;

    private MyHashMap<String, PropertyMapping> propertiesMapping;

    private MyHashMap<MethodSignature, MethodMapping> methodsMapping;

    private Annotations annotations;

    private Map<String, Field> fields;

    ClassMapping(Class<T> type, boolean allProperties, Optional<ClassBuilderCriteria> classBuilderCriteriaOpt, MyHashMap<String, PropertyMapping> propertiesMapping, MyHashMap<MethodSignature, MethodMapping> methodsMapping, Annotations annotations) {
        this.type = type;
        this.allProperties = allProperties;
        this.classBuilderCriteriaOpt = classBuilderCriteriaOpt;
        this.propertiesMapping = propertiesMapping;
        this.methodsMapping = methodsMapping;
        this.annotations = annotations;
        this.fields = getPropertiesWithInheritance(type).collect(toMap(Field::getName, f -> f));
    }

    public ClassMapping(Class<T> type) {
        this(type, false, Optional.empty(), new MyHashMap<>(), new MyHashMap<>(), new Annotations());
    }

    public void mapAllProperties() {
        this.allProperties = true;
    }

    public void ignore(String propertyName) {
        getOrCreatePropertyMapping(propertyName).ignore();
    }

    public void map(String propertyName) {
        getOrCreatePropertyMapping(propertyName).map();
    }

    public void map(String propertyName, String mappedName) {
        getOrCreatePropertyMapping(propertyName).mapTo(mappedName);
    }

    public void onConstructor(ClassBuilderCriteria classBuilderCriteria) {
        classBuilderCriteriaOpt = Optional.of(classBuilderCriteria);
    }

    public void on(PropertyMapping propertyMapping) {
        checkFieldExists(propertyMapping.getFieldName());
        propertiesMapping.put(propertyMapping.getFieldName(), propertyMapping);
    }

    public void on(MethodMapping methodMapping) {
        methodsMapping.put(methodMapping.getMethodSignature(), methodMapping);
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

    public boolean allPropertiesAreMapped() {
        return this.allProperties;
    }

    public void propertyForTypeName(String property) {
        annotations.add(jsonTypeInfo().use(JsonTypeInfo.Id.NAME).property(property));
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

    public PropertyMapping getOrCreatePropertyMapping(String name) {
        PropertyMapping propertyMapping = propertiesMapping.get(name);
        if (propertyMapping == null) {
            propertyMapping = property(name);
            checkFieldExists(name);
            propertiesMapping.put(name, propertyMapping);
        }
        return propertyMapping;
    }

    private void checkFieldExists(String name) {
        if (!fields.containsKey(name)) {
            // TODO to enablee when method inspection will be implemented
            //throw new IllegalStateException("Field with name " + name + " doesn't exist in class mapping " + type.getName());
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
                allProperties,
                Optional.ofNullable(classBuilderCriteriaOpt.map(classBuilderCriteria -> classBuilderCriteria.copy()).orElse(null)),
                propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy(), (v1, V2) -> {
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
                allProperties,
                Optional.ofNullable(classBuilderCriteriaOpt.map(classBuilderCriteria -> classBuilderCriteria.copy()).orElse(null)),
                propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy(), (v1, V2) -> {
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
        boolean newAllProperties = allProperties == false ? parentMapping.allProperties : allProperties;
        MyHashMap<String, PropertyMapping> newPropertiesMapping = propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy(), (v1, V2) -> {
            throw new UnsupportedOperationException();
        }, () -> new MyHashMap<>()));
        parentMapping.propertiesMapping.values().stream()
                .map(propertyParentMapping -> Optional.ofNullable(newPropertiesMapping.get(propertyParentMapping.getFieldName()))
                        .map(propertyMapping -> propertyMapping.copyWithParentMapping(propertyParentMapping))
                        .orElseGet(() -> propertyParentMapping.copy()))
                .forEach(propertyMapping -> newPropertiesMapping.put(propertyMapping.getFieldName(), propertyMapping));

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
                newAllProperties,
                newClassBuilderCriteria,
                newPropertiesMapping,
                newMethodsMapping,
                newAnnotations);
    }
}
