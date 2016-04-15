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
package com.github.mrenou.jacksonatic.internal;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
import com.github.mrenou.jacksonatic.Jacksonatic;
import com.github.mrenou.jacksonatic.JacksonaticOptions;
import com.github.mrenou.jacksonatic.internal.introspection.JacksonaticClassIntrospector;
import com.github.mrenou.jacksonatic.internal.mapping.ClassMappingByOperation;
import com.github.mrenou.jacksonatic.internal.mapping.ClassMappingInternal;
import com.github.mrenou.jacksonatic.internal.mapping.ClassesMapping;
import com.github.mrenou.jacksonatic.internal.mapping.TypeNameAutoAssigner;
import com.github.mrenou.jacksonatic.internal.util.CopyableHashMap;
import com.github.mrenou.jacksonatic.mapping.ClassMapping;

import static com.github.mrenou.jacksonatic.internal.JacksonOperation.*;

public class JacksonaticInternal implements Jacksonatic {

    private CopyableHashMap<JacksonOperation, ClassesMapping> classesMappingByOperation = new CopyableHashMap<>();

    private TypeNameAutoAssigner typeNameAutoAssigner = new TypeNameAutoAssigner();

    private JacksonaticOptions options;

    public JacksonaticInternal(JacksonaticOptions options) {
        this.options = options;
        classesMappingByOperation.put(ANY, new ClassesMapping());
        classesMappingByOperation.put(SERIALIZATION, new ClassesMapping());
        classesMappingByOperation.put(DESERIALIZATION, new ClassesMapping());
    }

    @SuppressWarnings("unchecked")
    @Override
    public JacksonaticInternal on(ClassMapping<?> newClassMapping) {
        ClassMappingByOperation<Object> newClassMappingByOperation = (ClassMappingByOperation<Object>) newClassMapping;
        mergeNew(newClassMappingByOperation);
        typeNameAutoAssigner.assignTypeNameIfNecessary(classesMappingByOperation.get(ANY), newClassMappingByOperation.getClassMappingFor(ANY));
        typeNameAutoAssigner.saveTypeWithJsonSubTypes(newClassMappingByOperation.getClassMappingFor(ANY));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Jacksonatic mapAllFieldsOn(ClassMapping<?> classMapping) {
        ClassMappingByOperation<Object> classMappingByOperation = (ClassMappingByOperation<Object>) classMapping;
        mergeNew(classMappingByOperation);
        classMappingByOperation.getClassMappingFor(ANY).mapAllFields();
        return this;
    }

    private void mergeNew(ClassMappingByOperation<Object> newClassMappingByOperation) {
        mergeValueWithKeyFor(newClassMappingByOperation, ANY);
        mergeValueWithKeyFor(newClassMappingByOperation, SERIALIZATION);
        mergeValueWithKeyFor(newClassMappingByOperation, DESERIALIZATION);
    }

    private void mergeValueWithKeyFor(ClassMappingByOperation<Object> newClassMappingByOperation, JacksonOperation operation) {
        ClassMappingInternal<Object> newClassMapping = newClassMappingByOperation.getClassMappingFor(operation);
        classesMappingByOperation.get(operation).mergeValueWithKey(newClassMapping, newClassMapping.getType());
    }

    @Override
    public void registerIn(ObjectMapper objectMapper) {
        checkTypes();
        registerForSerializationIn(objectMapper);
        registerForDeserializationIn(objectMapper);
    }

    private void checkTypes() {
        if (options.typeChecking()) {
            classesMappingByOperation.forEach((operation, classesMapping) -> classesMapping.forEach((type, classMapping) -> classMapping.checkTypes()));
        }
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
        JacksonaticInternal mappingConfigurerCopy = (JacksonaticInternal) Jacksonatic.configureMapping(options);
        mappingConfigurerCopy.classesMappingByOperation = classesMappingByOperation.copy();
        return mappingConfigurerCopy;
    }

    public CopyableHashMap<JacksonOperation, ClassesMapping> getClassesMappingByOperation() {
        return classesMappingByOperation;
    }
}