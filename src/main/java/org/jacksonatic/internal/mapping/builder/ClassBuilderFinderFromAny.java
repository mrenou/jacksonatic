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
package org.jacksonatic.internal.mapping.builder;


import org.jacksonatic.internal.mapping.ClassMappingInternal;
import org.jacksonatic.internal.mapping.PropertyMapperInternal;
import org.jacksonatic.internal.mapping.builder.parameter.ParameterMapping;
import org.jacksonatic.internal.mapping.field.FieldMappingInternal;
import org.jacksonatic.internal.mapping.method.MethodMappingInternal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static org.jacksonatic.internal.util.ReflectionUtil.getFieldsWithInheritance;

/**
 * Use {@link ClassMappingInternal } to find any constructor or static factory to build the type
 *
 * {@see ClassBuilderFinder}
 */
public class ClassBuilderFinderFromAny {

    public Optional<ClassBuilderMapping> find(ClassMappingInternal<Object> classMapping) {
        SortedSet<ClassBuilderMapping> classBuilderMappingScored = new TreeSet<>(
                Comparator.<ClassBuilderMapping>comparingInt(o -> o.getParametersMapping().size())
                        .reversed()
                        .thenComparing(o -> o.getConstructor() != null ? "constructor=" + o.getConstructor() : "staticFactory=" + o.getStaticFactory())
                        .thenComparingInt(o -> o.getConstructor() != null ? o.getConstructor().getParameterCount() : o.getStaticFactory().getParameterCount())
        );
        addConstructorScored(classMapping, classBuilderMappingScored);

        if (classBuilderMappingScored.isEmpty() || (classBuilderMappingScored.first().getParametersMapping().size() < classMapping.getType().getDeclaredFields().length)) {
            addStaticFactoryScored(classMapping, classBuilderMappingScored);
        }

        if (!classBuilderMappingScored.isEmpty()) {
            return Optional.of(classBuilderMappingScored.first());
        }
        return Optional.empty();
    }

    private void addConstructorScored(ClassMappingInternal<Object> classMapping, SortedSet<ClassBuilderMapping> classBuilderMappingScored) {
        for (Constructor<?> constructor : classMapping.getType().getConstructors()) {
            List<Field> declaredFields = getFieldsWithInheritance(classMapping.getType()).collect(toList());
            List<ParameterMapping> parametersMapping = getParametersMapping(classMapping, Arrays.asList(constructor.getParameterTypes()), declaredFields);
            classBuilderMappingScored.add(new ClassBuilderMapping(constructor, parametersMapping));
            if (parametersMapping.size() == declaredFields.size()) {
                break;
            }
        }
    }

    private void addStaticFactoryScored(ClassMappingInternal<Object> classMapping, SortedSet<ClassBuilderMapping> classBuilderMappingScored) {
        for (Method method : classMapping.getType().getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                List<Field> declaredFields = getFieldsWithInheritance(classMapping.getType()).collect(toList());
                List<ParameterMapping> parametersMapping = getParametersMapping(classMapping, Arrays.asList(method.getParameterTypes()), declaredFields);
                classBuilderMappingScored.add(new ClassBuilderMapping(method, parametersMapping));
                if (parametersMapping.size() == declaredFields.size()) {
                    break;
                }
            }
        }
    }

    private List<ParameterMapping> getParametersMapping(ClassMappingInternal<Object> classMapping, List<Class<?>> parameterTypes, List<Field> fields) {
        int iParameterType = 0;
        int iFields = 0;
        List<ParameterMapping> parametersMapping = new ArrayList<>();

        while (iFields < fields.size() && iParameterType < parameterTypes.size()) {
            Field field = fields.get(iFields);
            Class<?> parameterType = parameterTypes.get(iParameterType);
            FieldMappingInternal fieldMapping = classMapping.getOrCreateFieldMappingInternal(field.getName());
            Optional<MethodMappingInternal> setterMapping = classMapping.getSetterMapping(field.getName(), field.getType());
            Optional<String> mappedNameOpt = getMappedNameOpt(classMapping, fieldMapping, setterMapping);
            if (!Modifier.isStatic(field.getModifiers()) && mappedNameOpt.isPresent()) {
                if (parameterType.equals(field.getType())) {
                    parametersMapping.add(new ParameterMapping(field.getType(), mappedNameOpt.get()));
                    iFields++;
                    iParameterType++;
                } else {
                    break;
                }
            } else {
                iFields++;
            }
        }
        return parametersMapping;
    }

    private Optional<String> getMappedNameOpt(ClassMappingInternal<Object> classMapping, FieldMappingInternal fieldMapping, Optional<MethodMappingInternal> methodMappingOpt) {
        if (methodMappingOpt.map(PropertyMapperInternal::isMapped).orElse(false)) {
            return Optional.of(methodMappingOpt.get().getMappedName());
        }
        if (classMapping.allFieldsAreMapped() || fieldMapping.isMapped()) {
            return Optional.of(fieldMapping.getMappedName());
        }
        return Optional.empty();
    }

}
