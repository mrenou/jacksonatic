package org.jacksonatic;

import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.ParameterMatcher;

import java.util.Arrays;

import static org.jacksonatic.mapping.ConstructorMapping.mapConstructor;
import static org.jacksonatic.mapping.ConstructorMapping.mapStaticFactory;

public class ClassMappingConfigurer<T> {

    private ClassMapping<T> currentClassMapping;

    private ClassMapping<T> classMapping;

    private ClassMapping<T> serializationOnlyClassMapping;

    private ClassMapping<T> deserializationOnlyClassMapping;

    public static ClassMappingConfigurer type(Class<?> clazz) {
        return new ClassMappingConfigurer(clazz);
    }

    public static ClassMappingConfigurer onSerializationOf(Class<?> clazz) {
        return type(clazz).onSerialization();
    }

    public static ClassMappingConfigurer onDeserialisationOf(Class<?> clazz) {
        return type(clazz).onDeserialization();
    }

    private ClassMappingConfigurer(Class<T> clazz) {
        classMapping = new ClassMapping<>(clazz);
        serializationOnlyClassMapping = new ClassMapping<>(clazz);
        deserializationOnlyClassMapping = new ClassMapping<>(clazz);
        currentClassMapping = classMapping;
    }

    public ClassMappingConfigurer onSerialization() {
        currentClassMapping = serializationOnlyClassMapping;
        return this;
    }

    public ClassMappingConfigurer onDeserialization() {
        currentClassMapping = deserializationOnlyClassMapping;
        return this;
    }

    public ClassMappingConfigurer all() {
        currentClassMapping.mapAllProperties();
        return this;
    }

    public ClassMappingConfigurer map(String propertyName) {
        currentClassMapping.map(propertyName);
        return this;
    }

    public ClassMappingConfigurer map(String propertyName, String mappedName) {
        currentClassMapping.map(propertyName, mappedName);
        return this;
    }

    public ClassMappingConfigurer ignore(String propertyName) {
        currentClassMapping.ignore(propertyName);
        return this;
    }

    public ClassMappingConfigurer withConstructor(ParameterMatcher... parameterMatchers) {
        currentClassMapping.onConstructor(mapConstructor(currentClassMapping.getClazz(), Arrays.asList(parameterMatchers)));
        return this;
    }

    public ClassMappingConfigurer onStaticFactory(String methodName, ParameterMatcher... parameterMatchers) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getClazz(), methodName, Arrays.asList(parameterMatchers)));
        return this;
    }

    public ClassMappingConfigurer onStaticFactory(ParameterMatcher... parameterMatchers) {
        currentClassMapping.onConstructor(mapStaticFactory(currentClassMapping.getClazz(), Arrays.asList(parameterMatchers)));
        return this;
    }


    ClassMapping<T> getClassMapping() {
        return classMapping;
    }

    ClassMapping<T> getSerializationOnlyClassMapping() {
        return serializationOnlyClassMapping;
    }

    ClassMapping<T> getDeserializationOnlyClassMapping() {
        return deserializationOnlyClassMapping;
    }
}