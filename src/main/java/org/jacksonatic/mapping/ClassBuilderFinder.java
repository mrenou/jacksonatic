package org.jacksonatic.mapping;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.Arrays.asList;

public class ClassBuilderFinder {

    public static Optional<ClassBuilderMapping> findClassBuilderMapping(Class<?> type, ClassBuilderCriteria classBuilderCriteria) {
        if (classBuilderCriteria.isAny()) {
            return findClassBuilderFrom(type);
        } else {
            return findClassBuilderFrom(type, classBuilderCriteria);
        }
    }

    public static Optional<ClassBuilderMapping> findClassBuilderFrom(Class<?> type, ClassBuilderCriteria classBuilderCriteria) {
        final Optional<ClassBuilderMapping> classBuilderOptional;
        if (classBuilderCriteria.isStaticFactory()) {
            classBuilderOptional = asList(type.getDeclaredMethods()).stream()
                    .filter(method -> Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()))
                    .map(method -> new ClassBuilderMapping(method, classBuilderCriteria.getParameterCriteria()))
                    .filter(classBuilder -> hasToBuild(classBuilder, classBuilderCriteria))
                    .findFirst();
        } else {
            classBuilderOptional = asList(type.getConstructors()).stream()
                    .map(method -> new ClassBuilderMapping(method, classBuilderCriteria.getParameterCriteria()))
                    .filter(classBuilder -> hasToBuild(classBuilder, classBuilderCriteria))
                    .findFirst();
        }
        return classBuilderOptional;

    }

    static class ClassBuilderMappingScored {

        public final ClassBuilderMapping classBuilderMapping;

        public final int score;

        ClassBuilderMappingScored(ClassBuilderMapping classBuilderMapping, int score) {
            this.classBuilderMapping = classBuilderMapping;
            this.score = score;

        }
    }

    public static boolean hasToBuild(ClassBuilderMapping classBuilderMapping, ClassBuilderCriteria classBuilderCriteria) {
        boolean match = false;
        if ((classBuilderCriteria.getMethodName() == null || classBuilderCriteria.getMethodName().equals(classBuilderMapping.getName()))
                && classBuilderMapping.getParameterCount() == classBuilderCriteria.getParameterCriteria().size()
                && classBuilderMapping.isPublic()) {
            match = true;
            for (int i = 0; i < classBuilderMapping.getParameterCount(); i++) {
                if (!classBuilderMapping.getParameterTypes()[i].equals(classBuilderCriteria.getParameterCriteria().get(i).getParameterClass())) {
                    match = false;
                }
            }
        }
        return match;
    }

    private static Optional<ClassBuilderMapping> findClassBuilderFrom(Class<?> classToBuild) {
        SortedSet<ClassBuilderMappingScored> classBuilderMappingScored = new TreeSet(Comparator.<ClassBuilderMappingScored>comparingInt(pms -> pms.score).reversed());
        for (Constructor<?> constructor : classToBuild.getConstructors()) {
            int i = 0;
            List<ParameterMapping> parametersMapping = new ArrayList<>();
            for (Field field : classToBuild.getDeclaredFields()) {
                if (constructor.getParameterTypes().length > i && constructor.getParameterTypes()[i].equals(field.getType())) {
                    parametersMapping.add(new ParameterMapping(field.getType(), field.getName()));
                } else {
                    break;
                }
                i++;
            }
            classBuilderMappingScored.add(new ClassBuilderMappingScored(new ClassBuilderMapping(constructor, parametersMapping), i));
            if (i == classToBuild.getDeclaredFields().length) {
                break;
            }
        }

        if (classBuilderMappingScored.isEmpty() || (!classBuilderMappingScored.isEmpty() && classBuilderMappingScored.first().score < classToBuild.getDeclaredFields().length)) {
            for (Method method : classToBuild.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                    int i = 0;
                    List<ParameterMapping> parametersMapping = new ArrayList<>();
                    for (Field field : classToBuild.getDeclaredFields()) {
                        if (method.getParameterTypes().length > i && method.getParameterTypes()[i].equals(field.getType())) {
                            parametersMapping.add(new ParameterMapping(field.getType(), field.getName()));
                        } else {
                            break;
                        }
                        i++;
                    }
                    classBuilderMappingScored.add(new ClassBuilderMappingScored(new ClassBuilderMapping(method, parametersMapping), i));
                    if (i == classToBuild.getDeclaredFields().length) {
                        break;
                    }
                }
            }
        }

        if (!classBuilderMappingScored.isEmpty()) {
            return Optional.of(classBuilderMappingScored.first().classBuilderMapping);
        }
        return Optional.empty();
    }

}
