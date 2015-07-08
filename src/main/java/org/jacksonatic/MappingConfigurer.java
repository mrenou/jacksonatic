package org.jacksonatic;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.ParameterMatcher;

import java.util.Arrays;

import static org.jacksonatic.mapping.ConstructorMapping.mapConstructor;
import static org.jacksonatic.mapping.ConstructorMapping.mapStaticFactory;

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

    public MappingConfigurer map(String propertyName, String mappedName) {
        classMapping.map(propertyName, mappedName);
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
        if (!(classIntrospector instanceof JacksonaticClassIntrospector)) {
            objectMapper.setConfig(serializationConfig.with(new JacksonaticClassIntrospector()));
        }
        JacksonaticClassIntrospector basicClassIntrospector = (JacksonaticClassIntrospector) objectMapper.getSerializationConfig().getClassIntrospector();
        basicClassIntrospector.register(classMapping);
    }

    public void registerForDeserializationIn(ObjectMapper objectMapper) {
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
        ClassIntrospector classIntrospector = deserializationConfig.getClassIntrospector();
        if (!(classIntrospector instanceof JacksonaticClassIntrospector)) {
            objectMapper.setConfig(deserializationConfig.with(new JacksonaticClassIntrospector()));
        }
        JacksonaticClassIntrospector basicClassIntrospector = (JacksonaticClassIntrospector) objectMapper.getDeserializationConfig().getClassIntrospector();
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