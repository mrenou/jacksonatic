package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.jacksonatic.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

import static java.util.stream.Collectors.toMap;
import static org.jacksonatic.mapping.ClassBuilderFinder.findClassBuilderMapping;
import static org.jacksonatic.mapping.PropertyMapping.property;
import static org.jacksonatic.util.ReflectionUtil.getPropertiesWithInheritance;

public class ClassMapping<T> {

    private Class<T> type;

    private boolean allProperties;

    private Optional<ClassBuilderMapping> classBuilderMappingOptional;

    private Map<String, PropertyMapping> propertiesMapping;

    private Annotations annotations;

    private Map<String, Field> fields;

    ClassMapping(Class<T> type, boolean allProperties, Optional<ClassBuilderMapping> classBuilderMappingOptional, Map<String, PropertyMapping> propertiesMapping, Annotations annotations) {
        this.type = type;
        this.allProperties = allProperties;
        this.classBuilderMappingOptional = classBuilderMappingOptional;
        this.propertiesMapping = propertiesMapping;
        this.annotations = annotations;
        this.fields = getPropertiesWithInheritance(type).collect(toMap(Field::getName, f -> f));
    }

    public ClassMapping(Class<T> type) {
        this(type, false, Optional.empty(), new HashMap<>(), new Annotations());
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
        classBuilderMappingOptional = findClassBuilderMapping((ClassMapping<Object>) this, classBuilderCriteria);
    }

    public void on(PropertyMapping propertyMapping) {
        checkFieldExists(propertyMapping.getFieldName());
        propertiesMapping.put(propertyMapping.getFieldName(), propertyMapping);
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
           throw new IllegalStateException("Field with name " + name + " doesn't exist in class mapping " + type.getName());
        }
    }

    public Class<T> getType() {
        return type;
    }

    public Optional<ClassBuilderMapping> getClassBuilderMappingOpt() {
        return classBuilderMappingOptional;
    }

    public Collection<Annotation> getAnnotations() {
        return annotations.values();
    }

    ClassMapping<Object> copy() {
        return new ClassMapping(type,
                allProperties,
                Optional.ofNullable(classBuilderMappingOptional.map(classBuilderMapping -> classBuilderMapping.copy()).orElse(null)),
                propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy())),
                annotations.copy()
        );
    }

    public ClassMapping<Object> createChildMapping(Class<Object> childClass) {
        return new ClassMapping(childClass,
                allProperties,
                Optional.ofNullable(classBuilderMappingOptional.map(classBuilderMapping -> classBuilderMapping.copy()).orElse(null)),
                propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy())),
                annotations.copy()
        );
    }

    public ClassMapping<Object> copyWithParentMapping(ClassMapping<Object> parentMapping) {
        Optional<ClassBuilderMapping> newConstructorMapping = Optional.ofNullable(classBuilderMappingOptional.map(classBuilderMapping -> classBuilderMapping.copy()).orElse(parentMapping.classBuilderMappingOptional.map(cm -> cm.copy()).orElse(null)));
        boolean newAllProperties = allProperties == false ? parentMapping.allProperties : allProperties;
        Map<String, PropertyMapping> newPropertiesMapping = propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy()));
        parentMapping.propertiesMapping.values().stream()
                .map(propertyParentMapping -> Optional.ofNullable(newPropertiesMapping.get(propertyParentMapping.getFieldName()))
                        .map(propertyMapping -> propertyMapping.copyWithParentMapping(propertyParentMapping))
                        .orElseGet(() -> propertyParentMapping.copy()))
                .forEach(propertyMapping -> newPropertiesMapping.put(propertyMapping.getFieldName(), propertyMapping));
        Annotations newAnnotations = parentMapping.annotations.copy();
        annotations.values().stream().forEach(annotation -> newAnnotations.put(annotation.getClass(), annotation));
        return new ClassMapping(type,
                newAllProperties,
                newConstructorMapping,
                newPropertiesMapping,
                newAnnotations);
    }
}
