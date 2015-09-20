package org.jacksonatic;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.jacksonatic.introspection.JacksonaticClassIntrospector;
import org.jacksonatic.mapping.ClassesMapping;

import java.util.function.Function;

/**
 * Entry point of the api, allowing to define a jackson class mapping collection in a programmatic way.
 */
public class MappingConfigurer {

    private ClassesMapping classesMapping = new ClassesMapping();

    private ClassesMapping serializationOnlyClassesMapping = new ClassesMapping();

    private ClassesMapping deserializationOnlyClassesMapping = new ClassesMapping();

    /**
     * Entry point of the api
     * @return
     */
    public static MappingConfigurer configureMapping() {
        return new MappingConfigurer();
    }

    /**
     * to define a class mapping
     * @param classMappingConfigurer
     * @return
     */
    public MappingConfigurer on(ClassMappingConfigurer classMappingConfigurer) {
        addType(classMappingConfigurer);
        return this;
    }

    private void addType(ClassMappingConfigurer classMappingConfigurer) {
        classesMapping.put(classMappingConfigurer.getClassMapping().getType(), classMappingConfigurer.getClassMapping());
        serializationOnlyClassesMapping.put(classMappingConfigurer.getSerializationOnlyClassMapping().getType(), classMappingConfigurer.getSerializationOnlyClassMapping());
        deserializationOnlyClassesMapping.put(classMappingConfigurer.getDeserializationOnlyClassMapping().getType(), classMappingConfigurer.getDeserializationOnlyClassMapping());
    }

    /**
     * to define a class mapping with all its properties mapped
     * @param classMappingConfigurer
     * @return
     */
    public MappingConfigurer mapAllOn(ClassMappingConfigurer classMappingConfigurer) {
        addType(classMappingConfigurer);
        classMappingConfigurer.getClassMapping().mapAllProperties();
        return this;
    }

    /**
     * register mapping configuration in a {@ling com.fasterxml.jackson.databind.ObjectMapper}
     * @param objectMapper
     */
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
        basicClassIntrospector.register(this);
    }

    private void registerForDeserializationIn(ObjectMapper objectMapper) {
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
        ClassIntrospector classIntrospector = deserializationConfig.getClassIntrospector();
        if (!(classIntrospector instanceof JacksonaticClassIntrospector)) {
            objectMapper.setConfig(deserializationConfig.with(new JacksonaticClassIntrospector()));
        }
        JacksonaticClassIntrospector basicClassIntrospector = (JacksonaticClassIntrospector) objectMapper.getDeserializationConfig().getClassIntrospector();
        basicClassIntrospector.register(this);
    }

    public ClassesMapping getClassesMapping() {
        return classesMapping;
    }

    public ClassesMapping getSerializationOnlyClassesMapping() {
        return serializationOnlyClassesMapping;
    }

    public ClassesMapping getDeserializationOnlyClassesMapping() {
        return deserializationOnlyClassesMapping;
    }
}