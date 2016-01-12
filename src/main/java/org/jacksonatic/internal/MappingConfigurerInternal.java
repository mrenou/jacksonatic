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
package org.jacksonatic.internal;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import org.jacksonatic.ClassMappingConfigurer;
import org.jacksonatic.MappingConfigurer;
import org.jacksonatic.internal.introspection.JacksonaticClassIntrospector;
import org.jacksonatic.internal.mapping.ClassMapping;
import org.jacksonatic.internal.mapping.ClassesMapping;
import org.jacksonatic.internal.mapping.TypeNameAutoAssigner;

import java.util.Optional;

public class MappingConfigurerInternal implements MappingConfigurer {

    private ClassesMapping classesMapping = new ClassesMapping();

    private ClassesMapping serializationOnlyClassesMapping = new ClassesMapping();

    private ClassesMapping deserializationOnlyClassesMapping = new ClassesMapping();

    private TypeNameAutoAssigner typeNameAutoAssigner = new TypeNameAutoAssigner();

    @Override
    public MappingConfigurerInternal on(ClassMappingConfigurer classMappingConfigurer) {
        ClassMappingConfigurerInternal classMappingConfigurerInternal = (ClassMappingConfigurerInternal) classMappingConfigurer;
        addType(classMappingConfigurerInternal);
        typeNameAutoAssigner.assignTypeNameIfNeccesary(classesMapping, classMappingConfigurerInternal);
        typeNameAutoAssigner.saveTypeWithJsonSubTypes(classMappingConfigurerInternal);
        return this;
    }

    private void addType(ClassMappingConfigurerInternal classMappingConfigurer) {
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

    @Override
    public MappingConfigurer mapAllFieldsOn(ClassMappingConfigurer classMappingConfigurer) {
        ClassMappingConfigurerInternal classMappingConfigurerInternal = (ClassMappingConfigurerInternal) classMappingConfigurer;
        addType(classMappingConfigurerInternal);
        (classMappingConfigurerInternal).getClassMapping().mapAllFields();
        return this;
    }

    @Override
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

    @Override
    public MappingConfigurer copy() {
        MappingConfigurerInternal mappingConfigurerCopy = (MappingConfigurerInternal) MappingConfigurer.configureMapping();
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