package org.jacksonatic.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.jacksonatic.util.ReflectionUtil.getDeclaredFieldsWithInheritance;
import static org.jacksonatic.util.ReflectionUtil.getPropertiesWithInheritance;

public class ParametersMappingBuilder {

    public static List<ParameterMapping> buildParametersMapping(Class<?> classToBuild, List<ParameterCriteria> parameterCriterias) {
        Map<Class<?>, PriorityQueue<String>> propertiesByClass = new HashMap<>();
        Map<String, Class<?>> classByProperty = new HashMap<>();
        getPropertiesWithInheritance(classToBuild).forEach(field -> {
            PriorityQueue<String> properties = propertiesByClass.get(field.getType());
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
