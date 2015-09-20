package org.jacksonatic.mapping;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.jacksonatic.util.ReflectionUtil.getPropertiesWithInheritance;

public class ClassBuilderFinder {

    public static Optional<ClassBuilderMapping> findClassBuilderMapping(ClassMapping<Object> classMapping, ClassBuilderCriteria classBuilderCriteria) {
        Optional<ClassBuilderMapping> classBuilderMappingOpt;
        if (classBuilderCriteria.isAny()) {
            classBuilderMappingOpt = findClassBuilderFrom(classMapping);
        } else {
            classBuilderMappingOpt = findClassBuilderFrom(classMapping, classBuilderCriteria);
        }
        if (!classBuilderMappingOpt.isPresent() && !classBuilderCriteria.isAny()) {
            throw new IllegalArgumentException("Cannot find constructor with criteria " + classBuilderCriteria.mappingAsString());
        }
        return classBuilderMappingOpt;
    }

    public static Optional<ClassBuilderMapping> findClassBuilderFrom(ClassMapping<Object> classMapping, ClassBuilderCriteria classBuilderCriteria) {
        final Optional<ClassBuilderMapping> classBuilderOptional;
        if (classBuilderCriteria.isStaticFactory()) {
            classBuilderOptional = asList(classMapping.getType().getDeclaredMethods()).stream()
                    .filter(method -> Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()))
                    .map(method -> new ClassBuilderMapping(method, classBuilderCriteria.getParametersMapping()))
                    .filter(classBuilder -> hasToBuild(classBuilder, classBuilderCriteria))
                    .findFirst();
        } else {
            classBuilderOptional = asList(classMapping.getType().getConstructors()).stream()
                    .map(method -> new ClassBuilderMapping(method, classBuilderCriteria.getParametersMapping()))
                    .filter(classBuilder -> hasToBuild(classBuilder, classBuilderCriteria))
                    .findFirst();
        }
        return classBuilderOptional;

    }

    private static boolean hasToBuild(ClassBuilderMapping classBuilderMapping, ClassBuilderCriteria classBuilderCriteria) {
        boolean match = false;
        if ((classBuilderCriteria.getMethodName() == null || classBuilderCriteria.getMethodName().equals(classBuilderMapping.getName()))
                && classBuilderMapping.getParameterCount() == classBuilderCriteria.getParametersMapping().size()
                && classBuilderMapping.isPublic()) {
            match = true;
            for (int i = 0; i < classBuilderMapping.getParameterCount(); i++) {
                if (!classBuilderMapping.getParameterTypes()[i].equals(classBuilderCriteria.getParametersMapping().get(i).getParameterClass())) {
                    match = false;
                }
            }
        }
        return match;
    }

    private static Optional<ClassBuilderMapping> findClassBuilderFrom(ClassMapping<Object> classMapping) {
        SortedSet<ClassBuilderMapping> classBuilderMappingScored = new TreeSet(
                Comparator.<ClassBuilderMapping>comparingInt(o -> o.getParametersMapping().size())
                        .reversed()
                        .thenComparing(o -> o.getConstructor() != null ? "constructor=" + o.getConstructor() : "staticFactory=" + o.getStaticFactory())
                        .thenComparingInt(o -> o.getConstructor() != null ? o.getConstructor().getParameterCount() : o.getStaticFactory().getParameterCount())
        );
        addConstructorScored(classMapping, classBuilderMappingScored);

        if (classBuilderMappingScored.isEmpty() || (!classBuilderMappingScored.isEmpty() && classBuilderMappingScored.first().getParametersMapping().size() < classMapping.getType().getDeclaredFields().length)) {
            addStaticFactoryScored(classMapping, classBuilderMappingScored);
        }

        if (!classBuilderMappingScored.isEmpty()) {
            return Optional.of(classBuilderMappingScored.first());
        }
        return Optional.empty();
    }

    private static void addConstructorScored(ClassMapping<Object> classMapping, SortedSet<ClassBuilderMapping> classBuilderMappingScored) {
        for (Constructor<?> constructor : classMapping.getType().getConstructors()) {
            List<Field> declaredFields = getPropertiesWithInheritance(classMapping.getType()).collect(toList());
            List<ParameterMapping> parametersMapping = getParametersMapping(classMapping, Arrays.asList(constructor.getParameterTypes()), declaredFields);
            classBuilderMappingScored.add(new ClassBuilderMapping(constructor, parametersMapping));
            if (parametersMapping.size() == declaredFields.size()) {
                break;
            }
        }
    }

    private static void addStaticFactoryScored(ClassMapping<Object> classMapping, SortedSet<ClassBuilderMapping> classBuilderMappingScored) {
        for (Method method : classMapping.getType().getDeclaredMethods()) {
            if (Modifier.isStatic(method.getModifiers())) {
                List<Field> declaredFields = getPropertiesWithInheritance(classMapping.getType()).collect(toList());
                List<ParameterMapping> parametersMapping = getParametersMapping(classMapping, Arrays.asList(method.getParameterTypes()), declaredFields);
                classBuilderMappingScored.add(new ClassBuilderMapping(method, parametersMapping));
                if (parametersMapping.size() == declaredFields.size()) {
                    break;
                }
            }
        }
    }

    private static List<ParameterMapping> getParametersMapping(ClassMapping<Object> classMapping, List<Class<?>> parameterTypes, List<Field> fields) {
        int iParameterType = 0;
        int iFields = 0;
        List<ParameterMapping> parametersMapping = new ArrayList<>();

        while (iFields < fields.size() && iParameterType < parameterTypes.size()) {
            Field field = fields.get(iFields);
            Class<?> parameterType = parameterTypes.get(iParameterType);
            PropertyMapping propertyMapping = classMapping.getOrCreatePropertyMapping(field.getName());

            if (!Modifier.isStatic(field.getModifiers()) && (classMapping.allPropertiesAreMapped() || propertyMapping.isMapped())) {
                if (parameterType.equals(field.getType())) {
                    parametersMapping.add(new ParameterMapping(field.getType(), propertyMapping.getMappedName()));
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
