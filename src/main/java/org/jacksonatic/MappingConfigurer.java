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

    public static MappingConfigurer configureMapping() {
        return new MappingConfigurer();
    }

    public MappingConfigurer config(ClassMappingConfigurer classMappingConfigurer) {
        classesMapping.put(classMappingConfigurer.getClassMapping().getClazz(), classMappingConfigurer.getClassMapping());
        serializationOnlyClassesMapping.put(classMappingConfigurer.getSerializationOnlyClassMapping().getClazz(), classMappingConfigurer.getSerializationOnlyClassMapping());
        deserializationOnlyClassesMapping.put(classMappingConfigurer.getDeserializationOnlyClassMapping().getClazz(), classMappingConfigurer.getDeserializationOnlyClassMapping());
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

}