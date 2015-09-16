package org.jacksonatic.mapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;
import static org.jacksonatic.mapping.ClassBuilderFinder.findClassBuilderMapping;
import static org.jacksonatic.util.ReflectionUtil.getPropertiesWithInheritance;

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
        this(type, false, Optional.empty(), collectProperties((Class<Object>) type));
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

    public ClassMapping<Object> mergeWithParentMapping(ClassMapping<Object> parentMapping) {
        classBuilderMappingOptional = Optional.ofNullable(classBuilderMappingOptional.orElse(parentMapping.classBuilderMappingOptional.orElse(null)));
        allProperties = allProperties == false ? parentMapping.allProperties : allProperties;
        parentMapping.propertiesMapping.values().stream()
                .filter(propertyParentMapping -> propertiesMapping.get(propertyParentMapping.getName()).getAnnotations().isEmpty())
                .forEach(propertyParentMapping -> propertiesMapping.put(propertyParentMapping.getName(), propertyParentMapping));
        return (ClassMapping<Object>)this;
    }

}
