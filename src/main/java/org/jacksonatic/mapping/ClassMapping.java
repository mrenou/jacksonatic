package org.jacksonatic.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClassMapping<T> {

    private Optional<ConstructorMapping> constructorMapping = Optional.empty();

    private Class<T> clazz;

    private boolean allProperties;

    private Map<String, PropertyMapping> propertiesMapping = new HashMap<>();

    ClassMapping(Optional<ConstructorMapping> constructorMapping, Class<T> clazz, boolean allProperties, Map<String, PropertyMapping> propertiesMapping) {
        this.constructorMapping = constructorMapping;
        this.clazz = clazz;
        this.allProperties = allProperties;
        this.propertiesMapping = propertiesMapping;
    }

    public ClassMapping(Class<T> clazz) {
        this.clazz = clazz;
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

    public void onConstructor(ConstructorMapping constructorMapping) {
        this.constructorMapping = Optional.of(constructorMapping);
    }

    public PropertyMapping getPropertyMapping(String propertyName) {
        return Optional.ofNullable(propertiesMapping.get(propertyName))
                .orElseGet(() -> {
                    PropertyMapping value = new PropertyMapping(propertyName);
                    propertiesMapping.put(propertyName, value);
                    return value;
                });
    }

    public boolean allPropertiesAreMapped() {
        return this.allProperties;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Optional<ConstructorMapping> getConstructorMapping() {
        return constructorMapping;
    }

    ClassMapping<Object> copy() {
        return new ClassMapping(Optional.ofNullable(constructorMapping.map(cm -> cm.copy()).orElse(null)),
                clazz,
                allProperties,
                propertiesMapping.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().copy())));
    }

    ClassMapping<Object> copyWithParentMapping(ClassMapping<Object> parentMapping) {
        Optional<ConstructorMapping> newConstructorMapping = Optional.ofNullable(constructorMapping.map(cm -> cm.copy()).orElse(parentMapping.constructorMapping.map(cm -> cm.copy()).orElse(null)));
        boolean newAllProperties = allProperties == false ? parentMapping.allProperties : allProperties;
        Map<String, PropertyMapping> newPropertiesMapping = propertiesMapping.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().copy()));
        parentMapping.propertiesMapping.values().stream()
                .map(propertyParentMapping -> Optional.ofNullable(newPropertiesMapping.get(propertyParentMapping.getName()))
                        .map(propertyMapping -> propertyMapping.copyWithParentMapping(propertyParentMapping))
                        .orElseGet(() -> propertyParentMapping.copy()))
                .forEach(propertyMapping -> newPropertiesMapping.put(propertyMapping.getName(), propertyMapping));
        return new ClassMapping(newConstructorMapping,
                clazz,
                newAllProperties,
                newPropertiesMapping);
    }
}
