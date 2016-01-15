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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.jacksonatic.internal.mapping.MethodSignature.methodSignature;
import static org.jacksonatic.internal.util.ReflectionUtil.getFieldsWithInheritance;
import static org.jacksonatic.internal.util.ReflectionUtil.getMethodsWithInheritance;

public class TypeChecker<T> {

    private final Class<T> type;

    private final Set<String> existingFieldNames;

    private final Set<MethodSignature> existingMethodSignatures;

    private final Set<String> existingMethodNames;

    public TypeChecker(Class<T> type) {
        this.type = type;
        this.existingFieldNames = getFieldsWithInheritance(type).map(Field::getName).collect(toSet());
        this.existingMethodSignatures = getMethodsWithInheritance(type).map(method -> methodSignature(method.getName(), method.getParameterTypes())).collect(toSet());
        this.existingMethodNames = getMethodsWithInheritance(type).map(Method::getName).collect(toSet());
    }

    public void checkFieldExists(String name) {
        if (!existingFieldNames.contains(name)) {
            throw new IllegalStateException(String.format("Field with name '%s' doesn't exist in class mapping %s", name, type.getName()));
        }
    }

    public void checkMethodExists(MethodSignature methodSignature) {
        if (!existingMethodSignatures.contains(methodSignature) && !existingMethodNames.contains(methodSignature.name)) {
            throw new IllegalStateException(String.format("Method with signature '%s' doesn't exist in class mapping %s", methodSignature, type.getName()));
        }
    }
}
