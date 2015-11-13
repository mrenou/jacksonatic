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
package org.jacksonatic.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ReflectionUtil {

    public static List<Field> getDeclaredFieldsWithInheritance(Class<?> classToBuild) {
        if (classToBuild == null || classToBuild.equals(Object.class)) {
            return new ArrayList<>();
        } else {
            List<Field> declaredFields = getDeclaredFieldsWithInheritance(classToBuild.getSuperclass());
            declaredFields.addAll(Arrays.asList(classToBuild.getDeclaredFields()));
            return declaredFields;

        }
    }

    public static Stream<Field> getFieldsWithInheritance(Class<?> classToBuild) {
        return getDeclaredFieldsWithInheritance(classToBuild).stream().filter(field -> !Modifier.isStatic(field.getModifiers()));
    }

    public static List<Method> getDeclaredMethodsWithInheritance(Class<?> classToBuild) {
        if (classToBuild == null || classToBuild.equals(Object.class)) {
            return new ArrayList<>();
        } else {
            List<Method> declaredMethods = getDeclaredMethodsWithInheritance(classToBuild.getSuperclass());
            declaredMethods.addAll(Arrays.asList(classToBuild.getDeclaredMethods()));
            return declaredMethods;

        }
    }

    public static Stream<Method> getMethodsWithInheritance(Class<?> classToBuild) {
        return getDeclaredMethodsWithInheritance(classToBuild).stream().filter(method -> !Modifier.isStatic(method.getModifiers()));
    }

}
