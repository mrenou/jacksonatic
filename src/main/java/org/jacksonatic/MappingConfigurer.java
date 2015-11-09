/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.jacksonatic.introspection.JacksonaticClassIntrospector;
import org.jacksonatic.mapping.ClassMapping;
import org.jacksonatic.mapping.ClassesMapping;
import org.jacksonatic.mapping.TypeNameAutoAssigner;

import java.util.Optional;

/**
 * Entry point of the api, allowing to define a jackson class mapping collection in a programmatic way.
 */
public class MappingConfigurer {

    private ClassesMapping classesMapping = new ClassesMapping();

    private ClassesMapping serializationOnlyClassesMapping = new ClassesMapping();

    private ClassesMapping deserializationOnlyClassesMapping = new ClassesMapping();

    private TypeNameAutoAssigner typeNameAutoAssigner = new TypeNameAutoAssigner();

    /**
     * Entry point of the api
     *
     * @return
     */
    public static MappingConfigurer configureMapping() {
        return new MappingConfigurer();
    }

    /**
     * to define a class mapping
     *
     * @param classMappingConfigurer
     * @return
     */
    public MappingConfigurer on(ClassMappingConfigurer classMappingConfigurer) {
        addType(classMappingConfigurer);
        typeNameAutoAssigner.assignTypeNameIfNeccesary(classesMapping, classMappingConfigurer);
        typeNameAutoAssigner.saveTypeWithJsonSubTypes(classMappingConfigurer);
        return this;
    }

    private void addType(ClassMappingConfigurer classMappingConfigurer) {
        mergeClassMappingInClassesMapping(classMappingConfigurer.getClassMapping(), classesMapping);
        mergeClassMappingInClassesMapping(classMappingConfigurer.getSerializationOnlyClassMapping(), serializationOnlyClassesMapping);
        mergeClassMappingInClassesMapping(classMappingConfigurer.getDeserializationOnlyClassMapping(), deserializationOnlyClassesMapping);
    }

    private void mergeClassMappingInClassesMapping(ClassMapping classMapping, ClassesMapping classesMapping) {
        classesMapping.put(classMapping.getType(),
                Optional.ofNullable(classesMapping.get(classMapping.getType()))
                        .map(parentClassMapping -> classMapping.copyWithParentMapping(parentClassMapping))
                        .orElse(classMapping));
    }

    /**
     * to define a class mapping with all its properties mapped
     *
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
     *
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

    public MappingConfigurer copy() {
        MappingConfigurer mappingConfigurerCopy = configureMapping();
        mappingConfigurerCopy.classesMapping = classesMapping.copy();
        mappingConfigurerCopy.serializationOnlyClassesMapping = serializationOnlyClassesMapping.copy();
        mappingConfigurerCopy.deserializationOnlyClassesMapping = deserializationOnlyClassesMapping.copy();
        return mappingConfigurerCopy;
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