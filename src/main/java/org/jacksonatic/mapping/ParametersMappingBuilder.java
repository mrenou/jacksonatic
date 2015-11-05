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
package org.jacksonatic.mapping;

import org.jacksonatic.util.MyHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static java.util.stream.Collectors.toList;
import static org.jacksonatic.util.ReflectionUtil.getPropertiesWithInheritance;

public class ParametersMappingBuilder {

    public static List<ParameterMapping> buildParametersMapping(Class<?> classToBuild, List<ParameterCriteria> parameterCriterias) {
        MyHashMap<Class<?>, PriorityQueue<String>> propertiesByClass = new MyHashMap<>();
        Map<String, Class<?>> classByProperty = new HashMap<>();
        getPropertiesWithInheritance(classToBuild).forEach(field -> {
            PriorityQueue<String> properties = propertiesByClass.getTyped(field.getType());
            if (properties == null) {
                properties = new PriorityQueue<>();
                propertiesByClass.put(field.getType(), properties);
            }
            properties.add(field.getName());
            classByProperty.put(field.getName(), field.getType());
        });
        return parameterCriterias.stream().map(parameterCriteria -> new ParameterMapping(loadParameterClass(parameterCriteria, classByProperty), loadJsonProperty(parameterCriteria, propertiesByClass))).collect(toList());
    }

    private static Class<?> loadParameterClass(ParameterCriteria parameterCriteria, Map<String, Class<?>> classByProperty) {
        Class<?> parameterClass = parameterCriteria.getParameterClass();
        if (parameterClass == null) {
            parameterClass = classByProperty.get(parameterCriteria.getFieldProperty());
        }
        if (parameterClass == null) {
            throw new RuntimeException("Cannot find class for parameter matcher " + parameterCriteria);
        }
        return parameterClass;
    }

    private static String loadJsonProperty(ParameterCriteria parameterCriteria, Map<Class<?>, PriorityQueue<String>> propertiesByClass) {
        String jsonProperty = parameterCriteria.getJsonProperty();
        if (jsonProperty == null) {
            final PriorityQueue<String> properties = propertiesByClass.get(parameterCriteria.getParameterClass());
            if (properties != null) {
                jsonProperty = properties.poll();
            }
        }
        if (jsonProperty == null) {
            throw new RuntimeException("Cannot find class for parameter matcher " + parameterCriteria);
        }
        return jsonProperty;
    }

}
