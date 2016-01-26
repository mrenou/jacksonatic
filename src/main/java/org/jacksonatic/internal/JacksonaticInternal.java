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
import org.jacksonatic.internal.mapping.ClassMappingByOperation;
import org.jacksonatic.internal.mapping.ClassMappingInternal;
import org.jacksonatic.internal.mapping.ClassesMapping;
import org.jacksonatic.internal.mapping.TypeNameAutoAssigner;
import org.jacksonatic.internal.util.CopyableHashMap;
import org.jacksonatic.mapping.ClassMapping;

import static org.jacksonatic.internal.JacksonOperation.*;

public class JacksonaticInternal implements Jacksonatic {

    private CopyableHashMap<JacksonOperation, ClassesMapping> classesMappingByOperation = new CopyableHashMap<>();

    private TypeNameAutoAssigner typeNameAutoAssigner = new TypeNameAutoAssigner();

    public JacksonaticInternal() {
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
        mappingConfigurerCopy.classesMappingByOperation = classesMappingByOperation.copy();
        return mappingConfigurerCopy;
    }

    public CopyableHashMap<JacksonOperation, ClassesMapping> getClassesMappingByOperation() {
        return classesMappingByOperation;
    }
}