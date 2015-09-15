package org.jacksonatic;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.ClassesMapping;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.jacksonatic.mapping.ClassBuilderCriteria.mapStaticFactory;

public class MappingConfigurer {

    private Function<Class<Object>, ClassMappingConfigurer<Object>> defaultClassMappingProducer = (clazz) ->  null;

    ClassesMapping classesMapping = new ClassesMapping();

    ClassesMapping serializationOnlyClassesMapping = new ClassesMapping();

    ClassesMapping deserializationOnlyClassesMapping = new ClassesMapping();

    public static MappingConfigurer configureMapping() {
        return new MappingConfigurer();
    }

    public MappingConfigurer forEach(Function<Class<Object>, ClassMappingConfigurer<Object>> defaultClassMappingProducer) {
        this.defaultClassMappingProducer= defaultClassMappingProducer;
        return this;
    }

    public MappingConfigurer on(ClassMappingConfigurer classMappingConfigurer) {
        addType(classMappingConfigurer);
        return this;
    }

    private void addType(ClassMappingConfigurer classMappingConfigurer) {
        classesMapping.put(classMappingConfigurer.getClassMapping().getType(), classMappingConfigurer.getClassMapping());
        serializationOnlyClassesMapping.put(classMappingConfigurer.getSerializationOnlyClassMapping().getType(), classMappingConfigurer.getSerializationOnlyClassMapping());
        deserializationOnlyClassesMapping.put(classMappingConfigurer.getDeserializationOnlyClassMapping().getType(), classMappingConfigurer.getDeserializationOnlyClassMapping());
    }

    public MappingConfigurer mapAllOn(ClassMappingConfigurer classMappingConfigurer) {
        addType(classMappingConfigurer);
        classMappingConfigurer.getClassMapping().mapAllProperties();
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
        basicClassIntrospector.register(defaultClassMappingProducer, this);
    }

    private void registerForDeserializationIn(ObjectMapper objectMapper) {
        DeserializationConfig deserializationConfig = objectMapper.getDeserializationConfig();
        ClassIntrospector classIntrospector = deserializationConfig.getClassIntrospector();
        if (!(classIntrospector instanceof JacksonaticClassIntrospector)) {
            objectMapper.setConfig(deserializationConfig.with(new JacksonaticClassIntrospector()));
        }
        JacksonaticClassIntrospector basicClassIntrospector = (JacksonaticClassIntrospector) objectMapper.getDeserializationConfig().getClassIntrospector();
        basicClassIntrospector.register(defaultClassMappingProducer, this);
    }

}