package org.jacksonatic.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ClassMapping<T> {

    private Optional<ConstructorMapping> constructorMapping = Optional.empty();

    private Class<T> clazz;

    private boolean allProperties;

    private Map<String, PropertyMapping> propertiesMapping = new HashMap<>();

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

}
