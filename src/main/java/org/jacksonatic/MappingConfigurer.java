package org.jacksonatic;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.assertj.core.util.Preconditions;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.ClassesMapping;
import org.jacksonatic.mapping.ParameterMatcher;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.jacksonatic.mapping.ConstructorMapping.mapConstructor;
import static org.jacksonatic.mapping.ConstructorMapping.mapStaticFactory;

public class MappingConfigurer {

    private ClassesMapping classesMapping = new ClassesMapping();

    private ClassesMapping serializationOnlyClassesMapping = new ClassesMapping();

    private ClassesMapping deserializationOnlyClassesMapping = new ClassesMapping();

    private ClassMapping<Object> currentClassMapping;

    private boolean onSerializationOnly = false;

    private boolean onDeserializationOnly = false;

    private ClassMapping getClassMapping(Class<Object> clazz) {
        Map<Class<Object>, ClassMapping<Object>> classesMapping = getClassesMapping();
        return Optional.ofNullable(classesMapping.get(clazz))
                .orElseGet(() -> {
                    ClassMapping<Object> value = new ClassMapping(clazz);
                    classesMapping.put(clazz, value);
                    return value;
                });
    }

    private Map<Class<Object>, ClassMapping<Object>> getClassesMapping() {
        if (onSerializationOnly) {
            return serializationOnlyClassesMapping;
        }
        if (onDeserializationOnly) {
            return deserializationOnlyClassesMapping;
        }
        return classesMapping;
    }

    public static MappingConfigurer configureMapping() {
        return new MappingConfigurer();
    }

    public MappingConfigurer on(Class<?> clazz) {
        currentClassMapping = getClassMapping((Class<Object>) clazz);
        return this;
    }

    public MappingConfigurer onSerializationOf(Class<?> clazz) {
        return on(clazz).onSerialization();
    }

    public MappingConfigurer onSerialization() {
        onSerializationOnly = true;
        onDeserializationOnly = false;
        currentClassMapping = getClassMapping(currentClassMapping.getClazz());
        return this;
    }

    public MappingConfigurer onDeserialisationOf(Class<?> clazz) {
        return on(clazz).onDeserialization();
    }

    public MappingConfigurer onDeserialization() {
        onDeserializationOnly = true;
        onSerializationOnly = false;
        currentClassMapping = getClassMapping(currentClassMapping.getClazz());
        return this;
    }

    public MappingConfigurer all() {
        getCurrentClassMapping().mapAllProperties();
        return this;
    }


    public MappingConfigurer map(String propertyName) {
        getCurrentClassMapping().map(propertyName);
        return this;
    }

    public MappingConfigurer map(String propertyName, String mappedName) {
        getCurrentClassMapping().map(propertyName, mappedName);
        return this;
    }

    public MappingConfigurer ignore(String propertyName) {
        getCurrentClassMapping().ignore(propertyName);
        return this;
    }

    public void registerIn(ObjectMapper objectMapper) {
        registerForSerializationIn(objectMapper);
        registerForDeserializationIn(objectMapper);
    }

    private void registerForSerializationIn(ObjectMapper objectMapper) {
        SerializationConfig serializationConfig = objectMapper.getSerializationConfig();
        ClassIntrospector classIntrospector = serializationConfig.getClassIntrospector();
        if (!(classIntrospector instanceof JacksonaticClassIntrospector)) {
            objectMapper.setConfig(serializationConfig.with(new JacksonaticClassIntrospector()));
        }
        JacksonaticClassIntrospector basicClassIntrospector = (JacksonaticClassIntrospector) objectMapper.getSerializationConfig().getClassIntrospector();
        basicClassIntrospector.register(classesMapping, serializationOnlyClassesMapping);
    }

    private void registerForDeserializationIn(ObjectMapper objectMapper) {
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
        ClassIntrospector classIntrospector = deserializationConfig.getClassIntrospector();
        if (!(classIntrospector instanceof JacksonaticClassIntrospector)) {
            objectMapper.setConfig(deserializationConfig.with(new JacksonaticClassIntrospector()));
        }
        JacksonaticClassIntrospector basicClassIntrospector = (JacksonaticClassIntrospector) objectMapper.getDeserializationConfig().getClassIntrospector();
        basicClassIntrospector.register(classesMapping, deserializationOnlyClassesMapping);
    }

    public MappingConfigurer withConstructor(ParameterMatcher... parameterMatchers) {
        getCurrentClassMapping().onConstructor(mapConstructor(getCurrentClassMapping().getClazz(), Arrays.asList(parameterMatchers)));
        return this;
    }

    public MappingConfigurer onStaticFactory(String methodName, ParameterMatcher... parameterMatchers) {
        getCurrentClassMapping().onConstructor(mapStaticFactory(getCurrentClassMapping().getClazz(), methodName, Arrays.asList(parameterMatchers)));
        return this;
    }

    public MappingConfigurer onStaticFactory(ParameterMatcher... parameterMatchers) {
        getCurrentClassMapping().onConstructor(mapStaticFactory(getCurrentClassMapping().getClazz(), Arrays.asList(parameterMatchers)));
        return this;
    }

    ClassMapping<?> getCurrentClassMapping() {
        Preconditions.checkNotNull(currentClassMapping, "No class selected");
        return currentClassMapping;
    }

}