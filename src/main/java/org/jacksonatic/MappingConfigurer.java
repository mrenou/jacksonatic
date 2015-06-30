package org.jacksonatic;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;

import java.util.Arrays;

import static org.jacksonatic.ConstructorMapping.mapConstructor;
import static org.jacksonatic.ConstructorMapping.mapStaticFactory;

public class MappingConfigurer<T> {

    private ClassMapping classMapping;

    public static <T> MappingConfigurer on(Class<T> clazz) {
        return new MappingConfigurer(clazz);
    }

    public MappingConfigurer(Class<T> clazz) {
        this.classMapping = new ClassMapping(clazz);
    }

    public MappingConfigurer all() {
        classMapping.mapAllProperties();
        return this;
    }

    public MappingConfigurer map(String propertyName) {
        classMapping.map(propertyName);
        return this;
    }

    public MappingConfigurer ignore(String propertyName) {
        classMapping.ignore(propertyName);
        return this;
    }

    public void registerIn(ObjectMapper objectMapper) {
        registerForSerializationIn(objectMapper);
        registerForDeserializationIn(objectMapper);
    }

    public void registerForSerializationIn(ObjectMapper objectMapper) {
        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
        ClassIntrospector classIntrospector = serializationConfig.getClassIntrospector();
        if (!(classIntrospector instanceof MyBasicClassIntrospector)) {
            objectMapper.setConfig(serializationConfig.with(new MyBasicClassIntrospector()));
        }
        MyBasicClassIntrospector basicClassIntrospector = (MyBasicClassIntrospector) objectMapper.getSerializationConfig().getClassIntrospector();
        basicClassIntrospector.register(classMapping);
    }

    public void registerForDeserializationIn(ObjectMapper objectMapper) {
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
        ClassIntrospector classIntrospector = deserializationConfig.getClassIntrospector();
        if (!(classIntrospector instanceof MyBasicClassIntrospector)) {
            objectMapper.setConfig(deserializationConfig.with(new MyBasicClassIntrospector()));
        }
        MyBasicClassIntrospector basicClassIntrospector = (MyBasicClassIntrospector) objectMapper.getDeserializationConfig().getClassIntrospector();
        basicClassIntrospector.register(classMapping);
    }

    public MappingConfigurer onConstructor(ParameterMatcher... parameterMatchers) {
        classMapping.onConstructor(mapConstructor(classMapping.getClazz(), Arrays.asList(parameterMatchers)));
        return this;
    }

    public MappingConfigurer onStaticFactory(String methodName, ParameterMatcher... parameterMatchers) {
        classMapping.onConstructor(mapStaticFactory(classMapping.getClazz(), methodName, Arrays.asList(parameterMatchers)));
        return this;
    }

    public MappingConfigurer onStaticFactory(ParameterMatcher... parameterMatchers) {
        classMapping.onConstructor(mapStaticFactory(classMapping.getClazz(), Arrays.asList(parameterMatchers)));
        return this;
    }
}