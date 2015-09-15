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

    private static boolean hasToBuild(ClassBuilderMapping classBuilderMapping, ClassBuilderCriteria classBuilderCriteria) {
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
        SortedSet<ClassBuilderMapping> classBuilderMappingScored = new TreeSet(
                Comparator.<ClassBuilderMapping>comparingInt(o -> o.getParametersMapping().size())
                        .reversed()
                        .thenComparing(o -> o.getConstructor() != null ? "constructor=" + o.getConstructor() : "staticFactory=" + o.getStaticFactory())
        );
        addConstructorScored(classToBuild, classBuilderMappingScored);

        if (classBuilderMappingScored.isEmpty() || (!classBuilderMappingScored.isEmpty() && classBuilderMappingScored.first().getParametersMapping().size() < classToBuild.getDeclaredFields().length)) {
            addStaticFactoryScored(classToBuild, classBuilderMappingScored);
        }

        if (!classBuilderMappingScored.isEmpty()) {
            return Optional.of(classBuilderMappingScored.first());
        }
        return Optional.empty();
    }

    private static void addConstructorScored(Class<?> classToBuild, SortedSet<ClassBuilderMapping> classBuilderMappingScored) {
        for (Constructor<?> constructor : classToBuild.getConstructors()) {
            List<ParameterMapping> parametersMapping = getParametersMapping(classToBuild, constructor.getParameterTypes());
            classBuilderMappingScored.add(new ClassBuilderMapping(constructor, parametersMapping));
            if (parametersMapping.size() == classToBuild.getDeclaredFields().length) {
                break;
            }
        }
    }

    private static void addStaticFactoryScored(Class<?> classToBuild, SortedSet<ClassBuilderMapping> classBuilderMappingScored) {
        for (Method method : classToBuild.getDeclaredMethods()) {
            List<ParameterMapping> parametersMapping = getParametersMapping(classToBuild, method.getParameterTypes());
            classBuilderMappingScored.add(new ClassBuilderMapping(method, parametersMapping));
            if (parametersMapping.size() == classToBuild.getDeclaredFields().length) {
                break;
            }
        }
    }

    private static List<ParameterMapping> getParametersMapping(Class<?> classToBuild, Class<?>[] parameterTypes) {
        int iParameterType = 0;
        int iFields = 0;
        List<ParameterMapping> parametersMapping = new ArrayList<>();

        while (iFields < classToBuild.getDeclaredFields().length && iParameterType < parameterTypes.length) {
            Field field = classToBuild.getDeclaredFields()[iFields];
            Class<?> parameterType = parameterTypes[iParameterType];

            if (!Modifier.isStatic(field.getModifiers())) {
                if (parameterType.equals(field.getType())) {
                    parametersMapping.add(new ParameterMapping(field.getType(), field.getName()));
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

}
