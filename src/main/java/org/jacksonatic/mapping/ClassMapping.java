package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jacksonatic.annotation.JacksonaticJsonSubTypes;
import org.jacksonatic.annotation.JacksonaticJsonSubTypesType;
import org.jacksonatic.annotation.JacksonaticJsonTypeInfo;
import org.jacksonatic.annotation.JacksonaticJsonTypeName;

import java.lang.annotation.Annotation;
import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.jacksonatic.mapping.ClassBuilderFinder.findClassBuilderMapping;
import static org.jacksonatic.util.ReflectionUtil.getPropertiesWithInheritance;

public class ClassMapping<T> {

    private Class<T> type;

    private boolean allProperties;

    private Optional<ClassBuilderMapping> classBuilderMappingOptional;

    private Map<String, PropertyMapping> propertiesMapping = new HashMap<>();

    private Map<Class<? extends Annotation>, Annotation> annotations;

    ClassMapping(Class<T> type, boolean allProperties, Optional<ClassBuilderMapping> classBuilderMappingOptional, Map<String, PropertyMapping> propertiesMapping, Map<Class<? extends Annotation>, Annotation> annotations) {
        this.type = type;
        this.allProperties = allProperties;
        this.classBuilderMappingOptional = classBuilderMappingOptional;
        this.propertiesMapping = propertiesMapping;
        this.annotations = annotations;
    }

    public ClassMapping(Class<T> type) {
        this(type, false, Optional.empty(), collectProperties((Class<Object>) type), new HashMap<>());
    }

    private static Map<String, PropertyMapping> collectProperties(Class<Object> type) {
        return getPropertiesWithInheritance(type).collect(toMap(field -> field.getName(), field -> new PropertyMapping(field), (field1, field2) -> field1));
    }

    public void mapAllProperties() {
        this.allProperties = true;
    }

    public void ignore(String propertyName) {
        getPropertyMapping(propertyName).ignore();
    }

    public void map(String propertyName) {
        getPropertyMapping(propertyName).map();
    }

    public void map(String propertyName, String mappedName) {
        getPropertyMapping(propertyName).map(mappedName);
    }

    public void onConstructor(ClassBuilderCriteria classBuilderCriteria) {
        classBuilderMappingOptional = findClassBuilderMapping((ClassMapping<Object>) this, classBuilderCriteria);
    }

    public boolean allPropertiesAreMapped() {
        return this.allProperties;
    }

    public void propertyForTypeName(String property) {
        annotations.put(JsonTypeInfo.class, new JacksonaticJsonTypeInfo(JsonTypeInfo.Id.NAME, null, property, null, false));
    }

    public void typeName(String name) {
        annotations.put(JsonTypeName.class, new JacksonaticJsonTypeName(name));
    }

    public void addNamedSubType(Class<? extends T> subType, String name) {
        List<JsonSubTypes.Type> types = Optional.ofNullable(annotations.get(JsonSubTypes.class))
                .map(annotation -> new ArrayList<>(Arrays.asList(((JsonSubTypes) annotation).value())))
                .orElse(new ArrayList<>());
        types.add(new JacksonaticJsonSubTypesType(name, subType));
        annotations.put(JsonSubTypes.class, new JacksonaticJsonSubTypes(types.toArray(new JsonSubTypes.Type[types.size()])));
    }

    public PropertyMapping getPropertyMapping(String name) {
        return findPropertyMapping(name)
                .orElseThrow(() -> new IllegalStateException("Field with name " + name + " doesn't exist in class mapping " + type.getName()));
    }

    public Optional<PropertyMapping> findPropertyMapping(String name) {
        return Optional.ofNullable(propertiesMapping.get(name));
    }

    public Class<T> getType() {
        return type;
    }

    public Optional<ClassBuilderMapping> getClassBuilderMappingOpt() {
        return classBuilderMappingOptional;
    }

    ClassMapping<Object> copy() {
        return new ClassMapping(type,
                allProperties,
                Optional.ofNullable(classBuilderMappingOptional.map(classBuilderMapping -> classBuilderMapping.copy()).orElse(null)),
                propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy())),
                annotations.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue()))
        );
    }

    public ClassMapping<Object> mergeWithParentMapping(ClassMapping<Object> parentMapping) {
        classBuilderMappingOptional = Optional.ofNullable(classBuilderMappingOptional.orElse(parentMapping.classBuilderMappingOptional.orElse(null)));
        allProperties = allProperties == false ? parentMapping.allProperties : allProperties;
        parentMapping.propertiesMapping.values().stream()
                .filter(propertyParentMapping -> propertiesMapping.get(propertyParentMapping.getName()).getAnnotations().isEmpty())
                .forEach(propertyParentMapping -> propertiesMapping.put(propertyParentMapping.getName(), propertyParentMapping));
        return (ClassMapping<Object>)this;
    }

    public ClassMapping<Object> copyWithParentMapping(ClassMapping<Object> parentMapping) {
        Optional<ClassBuilderMapping> newConstructorMapping = Optional.ofNullable(classBuilderMappingOptional.map(classBuilderMapping -> classBuilderMapping.copy()).orElse(parentMapping.classBuilderMappingOptional.map(cm -> cm.copy()).orElse(null)));
        boolean newAllProperties = allProperties == false ? parentMapping.allProperties : allProperties;
        Map<String, PropertyMapping> newPropertiesMapping = propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy()));
        parentMapping.propertiesMapping.values().stream()
                .map(propertyParentMapping -> Optional.ofNullable(newPropertiesMapping.get(propertyParentMapping.getName()))
                        .map(propertyMapping -> propertyMapping.copyWithParentMapping(propertyParentMapping))
                        .orElseGet(() -> propertyParentMapping.copy()))
                .forEach(propertyMapping -> newPropertiesMapping.put(propertyMapping.getName(), propertyMapping));
        Map<Class<? extends Annotation>, Annotation> newAnnotations =  parentMapping.annotations.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue()));
        annotations.values().stream().forEach(annotation -> newAnnotations.put(annotation.getClass(), annotation));
        return new ClassMapping(type,
                newAllProperties,
                newConstructorMapping,
                newPropertiesMapping,
                newAnnotations);
    }

    public Collection<Annotation> getAnnotations() {
        return annotations.values();
    }
}
