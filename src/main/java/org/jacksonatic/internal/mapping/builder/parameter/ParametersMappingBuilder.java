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
package org.jacksonatic.internal.mapping.builder.parameter;

import org.jacksonatic.internal.util.TypedHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static java.util.stream.Collectors.toList;
import static org.jacksonatic.internal.util.ReflectionUtil.getFieldsWithInheritance;

/**
 * Build parameters mapping from parameter criteria for a given class
 * if the type is not provided, it guesses it from field type with the given field name
 * if the json property is not provided, it guesses it from next field name with the same given type
 */
public class ParametersMappingBuilder {

    private TypedHashMap<Class<?>, PriorityQueue<String>> fieldNamesByType = new TypedHashMap<>();
    private Map<String, Class<?>> typeByFieldName = new HashMap<>();
    private List<ParameterCriteriaInternal> parameterCriteriaList;

    public static ParametersMappingBuilder forClass(Class<?> classUsed) {
        return new ParametersMappingBuilder(classUsed);
    }

    private ParametersMappingBuilder(Class<?> classUsed) {
        buildFieldNamesByTypeAndTypeByFieldName(classUsed);
    }

    private void buildFieldNamesByTypeAndTypeByFieldName(Class<?> classUsed) {
        getFieldsWithInheritance(classUsed).forEach(field -> {
            PriorityQueue<String> fieldNames = fieldNamesByType.getTyped(field.getType());
            if (fieldNames == null) {
                fieldNames = new PriorityQueue<>();
                fieldNamesByType.put(field.getType(), fieldNames);
            }
            fieldNames.add(field.getName());
            typeByFieldName.put(field.getName(), field.getType());
        });
    }

    private Class<?> loadParameterClass(ParameterCriteriaInternal parameterCriteria, Map<String, Class<?>> typeByFieldName) {
        Class<?> parameterClass = parameterCriteria.getParameterClass();
        if (parameterClass == null) {
            parameterClass = typeByFieldName.get(parameterCriteria.getFieldName());
        }
        if (parameterClass == null) {
            throw new RuntimeException("Cannot find class from the parameter criteria " + parameterCriteria);
        }
        return parameterClass;
    }

    private String loadJsonProperty(ParameterCriteriaInternal parameterCriteria, Map<Class<?>, PriorityQueue<String>> fieldNamesByType) {
        String jsonProperty = parameterCriteria.getJsonProperty();
        if (jsonProperty == null) {
            final PriorityQueue<String> fieldNames = fieldNamesByType.get(parameterCriteria.getParameterClass());
            if (fieldNames != null) {
                jsonProperty = fieldNames.poll();
            }
        }
        if (jsonProperty == null) {
            throw new RuntimeException("Cannot find json property from the parameter criteria " + parameterCriteria);
        }
        return jsonProperty;
    }

    public ParametersMappingBuilder from(List<ParameterCriteriaInternal> parameterCriteriaList) {
        this.parameterCriteriaList = parameterCriteriaList;
        return this;
    }

    public List<ParameterMapping> buildParametersMapping() {
        return parameterCriteriaList.stream()
                .map(parameterCriteria -> new ParameterMapping(
                        loadParameterClass(parameterCriteria, typeByFieldName),
                        loadJsonProperty(parameterCriteria, fieldNamesByType)))
                .collect(toList());
    }

}
