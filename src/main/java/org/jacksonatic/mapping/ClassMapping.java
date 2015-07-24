package org.jacksonatic.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.jacksonatic.mapping.ClassBuilderFinder.findClassBuilderMapping;

public class ClassMapping<T> {

    private Class<T> type;

    private boolean allProperties;

    private Optional<ClassBuilderMapping> classBuilderMappingOptional;

    private Map<String, PropertyMapping> propertiesMapping = new HashMap<>();

    ClassMapping(Class<T> type, boolean allProperties, Optional<ClassBuilderMapping> classBuilderMappingOptional, Map<String, PropertyMapping> propertiesMapping) {
        this.type = type;
        this.allProperties = allProperties;
        this.classBuilderMappingOptional = classBuilderMappingOptional;
        this.propertiesMapping = propertiesMapping;
    }

    public ClassMapping(Class<T> type) {
        this(type, false, Optional.empty(), asList(type.getDeclaredFields()).stream().collect(toMap(field -> field.getName(), field -> new PropertyMapping(field))));
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
        classBuilderMappingOptional = findClassBuilderMapping(type, classBuilderCriteria);
    }

    public PropertyMapping getPropertyMapping(String name) {
        return Optional.ofNullable(propertiesMapping.get(name))
                .orElseThrow(() -> new IllegalStateException("Field with name " + name + " doesn't exist in class mapping " + type.getName()));
    }

    public boolean allPropertiesAreMapped() {
        return this.allProperties;
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
                propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy())));
    }

    ClassMapping<Object> copyWithParentMapping(ClassMapping<Object> parentMapping) {
        Optional<ClassBuilderMapping> newConstructorMapping = Optional.ofNullable(classBuilderMappingOptional.map(classBuilderMapping -> classBuilderMapping.copy()).orElse(parentMapping.classBuilderMappingOptional.map(cm -> cm.copy()).orElse(null)));
        boolean newAllProperties = allProperties == false ? parentMapping.allProperties : allProperties;
        Map<String, PropertyMapping> newPropertiesMapping = propertiesMapping.entrySet().stream().collect(toMap(e -> e.getKey(), e -> e.getValue().copy()));
        parentMapping.propertiesMapping.values().stream()
                .map(propertyParentMapping -> Optional.ofNullable(newPropertiesMapping.get(propertyParentMapping.getName()))
                        .map(propertyMapping -> propertyMapping.copyWithParentMapping(propertyParentMapping))
                        .orElseGet(() -> propertyParentMapping.copy()))
                .forEach(propertyMapping -> newPropertiesMapping.put(propertyMapping.getName(), propertyMapping));
        return new ClassMapping(type,
                newAllProperties,
                newConstructorMapping,
                newPropertiesMapping);
    }
}
