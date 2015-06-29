package org.jacksonatic;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;

import java.util.Arrays;

import static org.jacksonatic.ConstructorMapping.mapConstructor;
import static org.jacksonatic.ConstructorMapping.mapStaticFactory;

public class SerializerBuilder<T> {

    private ClassMapping classMapping;

    public static <T> SerializerBuilder on(Class<T> clazz) {
        return new SerializerBuilder(clazz);
    }

    public SerializerBuilder(Class<T> clazz) {
        this.classMapping = new ClassMapping(clazz);
    }

    public SerializerBuilder all() {
        classMapping.mapAllProperties();
        return this;
    }

    public SerializerBuilder map(String propertyName) {
        classMapping.map(propertyName);
        return this;
    }

    public SerializerBuilder ignore(String propertyName) {
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

    public SerializerBuilder onConstructor(TypedParameter<?>... typedParameters) {
        classMapping.onConstructor(mapConstructor(Arrays.asList(typedParameters)));
        return this;
    }

    public SerializerBuilder onStaticFactory(String methodName, TypedParameter<?>... typedParameters) {
        classMapping.onConstructor(mapStaticFactory(methodName, Arrays.asList(typedParameters)));
        return this;
    }

    public SerializerBuilder onStaticFactory(TypedParameter<?>... typedParameters) {
        classMapping.onConstructor(mapStaticFactory(Arrays.asList(typedParameters)));
        return this;
    }
}