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
import org.jacksonatic.Jacksonatic;
import org.jacksonatic.internal.introspection.JacksonaticClassIntrospector;
import org.jacksonatic.internal.mapping.ClassMappingByProcessType;
import org.jacksonatic.internal.mapping.ClassesMapping;
import org.jacksonatic.internal.mapping.TypeNameAutoAssigner;
import org.jacksonatic.mapping.ClassMapping;

public class JacksonaticInternal implements Jacksonatic {

    private ClassesMapping classesMapping = new ClassesMapping();

    private ClassesMapping serializationOnlyClassesMapping = new ClassesMapping();

    private ClassesMapping deserializationOnlyClassesMapping = new ClassesMapping();

    private TypeNameAutoAssigner typeNameAutoAssigner = new TypeNameAutoAssigner();

    @SuppressWarnings("unchecked")
    @Override
    public JacksonaticInternal on(ClassMapping<?> classMapping) {
        ClassMappingByProcessType<Object> classMappingByProcessType = (ClassMappingByProcessType<Object>) classMapping;
        addType(classMappingByProcessType);
        typeNameAutoAssigner.assignTypeNameIfNecessary(classesMapping, classMappingByProcessType);
        typeNameAutoAssigner.saveTypeWithJsonSubTypes(classMappingByProcessType);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Jacksonatic mapAllFieldsOn(ClassMapping<?> classMapping) {
        ClassMappingByProcessType<Object> classMappingByProcessType = (ClassMappingByProcessType<Object>) classMapping;
        addType(classMappingByProcessType);
        classMappingByProcessType.getClassMapping().mapAllFields();
        return this;
    }

    private void addType(ClassMappingByProcessType<Object> classMappingByProcessType) {
        classesMapping.mergeValueWithKey(classMappingByProcessType.getClassMapping(), classMappingByProcessType.getClassMapping().getType());
        serializationOnlyClassesMapping.mergeValueWithKey(classMappingByProcessType.getSerializationOnlyClassMapping(), classMappingByProcessType.getSerializationOnlyClassMapping().getType());
        deserializationOnlyClassesMapping.mergeValueWithKey(classMappingByProcessType.getDeserializationOnlyClassMapping(), classMappingByProcessType.getDeserializationOnlyClassMapping().getType());
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
    public Jacksonatic copy() {
        JacksonaticInternal mappingConfigurerCopy = (JacksonaticInternal) Jacksonatic.configureMapping();
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