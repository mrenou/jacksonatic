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
package org.jacksonatic.internal.mapping;

import org.jacksonatic.internal.util.TypedHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static java.util.stream.Collectors.toList;
import static org.jacksonatic.internal.util.ReflectionUtil.getFieldsWithInheritance;

class ParametersMappingBuilder {

    public static List<ParameterMapping> buildParametersMapping(Class<?> classToBuild, List<ParameterCriteriaInternal> parameterCriteriaList) {
        TypedHashMap<Class<?>, PriorityQueue<String>> fieldNamesByType = new TypedHashMap<>();
        Map<String, Class<?>> typeByFieldName = new HashMap<>();
        getFieldsWithInheritance(classToBuild).forEach(field -> {
            PriorityQueue<String> fieldNames = fieldNamesByType.getTyped(field.getType());
            if (fieldNames == null) {
                fieldNames = new PriorityQueue<>();
                fieldNamesByType.put(field.getType(), fieldNames);
            }
            fieldNames.add(field.getName());
            typeByFieldName.put(field.getName(), field.getType());
        });
        return parameterCriteriaList.stream().map(parameterCriteria -> new ParameterMapping(loadParameterClass(parameterCriteria, typeByFieldName), loadJsonProperty(parameterCriteria, fieldNamesByType))).collect(toList());
    }

    private static Class<?> loadParameterClass(ParameterCriteriaInternal parameterCriteria, Map<String, Class<?>> typeByFieldName) {
        Class<?> parameterClass = parameterCriteria.getParameterClass();
        if (parameterClass == null) {
            parameterClass = typeByFieldName.get(parameterCriteria.getFieldName());
        }
        if (parameterClass == null) {
            throw new RuntimeException("Cannot find class for parameter matcher " + parameterCriteria);
        }
        return parameterClass;
    }

    private static String loadJsonProperty(ParameterCriteriaInternal parameterCriteria, Map<Class<?>, PriorityQueue<String>> fieldNamesByType) {
        String jsonProperty = parameterCriteria.getJsonProperty();
        if (jsonProperty == null) {
            final PriorityQueue<String> fieldNames = fieldNamesByType.get(parameterCriteria.getParameterClass());
            if (fieldNames != null) {
                jsonProperty = fieldNames.poll();
            }
        }
        if (jsonProperty == null) {
            throw new RuntimeException("Cannot find class for parameter matcher " + parameterCriteria);
        }
        return jsonProperty;
    }

}
