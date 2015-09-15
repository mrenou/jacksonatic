package org.jacksonatic.util;

import java.lang.reflect.Field;
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

    public static Stream<Field> getPropertiesWithInheritance(Class<?> classToBuild) {
        return getDeclaredFieldsWithInheritance(classToBuild).stream().filter(field -> !Modifier.isStatic(field.getModifiers()));
    }

}
