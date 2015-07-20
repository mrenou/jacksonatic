package org.jacksonatic.mapping;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class ConstructorMapping {

    private String methodName;

    private List<ParameterMapping> parameters;

    private boolean staticFactory = false;

    public static ConstructorMapping mapConstructor(Class<?> ownerClass, List<ParameterMatcher> parameterMatchers) {
        return new ConstructorMapping(ownerClass, null, parameterMatchers, false);
    }

    public static ConstructorMapping mapStaticFactory(Class<?> ownerClass, List<ParameterMatcher> parameterMatchers) {
        return new ConstructorMapping(ownerClass, null, parameterMatchers, true);
    }

    public static ConstructorMapping mapStaticFactory(Class<?> ownerClass, String methodName, List<ParameterMatcher> parameterMatchers) {
        return new ConstructorMapping(ownerClass, methodName, parameterMatchers, true);
    }

    public static ConstructorMapping mapAConstructorOrStaticFactory(Class<?> ownerClass) {
        return new ConstructorMapping(ownerClass);
    }

    ConstructorMapping(String methodName, List<ParameterMapping> parameters, boolean staticFactory) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.staticFactory = staticFactory;
    }

    private ConstructorMapping(Class<?> ownerClass, String methodName, List<ParameterMatcher> parameterMatchers, boolean staticFactory) {
        this.methodName = methodName;
        this.parameters = loadParametersMapping(ownerClass, parameterMatchers);
        this.staticFactory = staticFactory;
    }

    private ConstructorMapping(Class<?> ownerClass) {
        setParametersAndIsStaticFindingConstructor(ownerClass);
    }

    class ParameterMappingScored {

        public final List<ParameterMapping> parametersMapping;

        public final int score;

        public final boolean staticFactory;

        ParameterMappingScored(List<ParameterMapping> parametersMapping, int score) {
            this(parametersMapping, score, false);
        }

        ParameterMappingScored(List<ParameterMapping> parametersMapping, int score, boolean staticFactory) {
            this.parametersMapping = parametersMapping;
            this.score = score;
            this.staticFactory = staticFactory;
        }
    }

    private void setParametersAndIsStaticFindingConstructor(Class<?> ownerClass) {
        SortedSet<ParameterMappingScored> parametersMappingScored = new TreeSet(Comparator.<ParameterMappingScored>comparingInt(pms -> pms.score).reversed());
        for (Constructor<?> constructor : ownerClass.getConstructors()) {
            int i = 0;
            List<ParameterMapping> parametersMapping = new ArrayList<>();
            for (Field field : ownerClass.getDeclaredFields()) {
                if (constructor.getParameterTypes().length > i && constructor.getParameterTypes()[i].equals(field.getType())) {
                    parametersMapping.add(new ParameterMapping(field.getType(), field.getName()));
                } else {
                    break;
                }
                i++;
            }
            parametersMappingScored.add(new ParameterMappingScored(parametersMapping, i));
            if (i == ownerClass.getDeclaredFields().length) {
                break;
            }
        }

        if (parametersMappingScored.isEmpty() || (!parametersMappingScored.isEmpty() && parametersMappingScored.first().score < ownerClass.getDeclaredFields().length)) {
            for (Method method : ownerClass.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers())) {
                    int i = 0;
                    List<ParameterMapping> parametersMapping = new ArrayList<>();
                    for (Field field : ownerClass.getDeclaredFields()) {
                        if (method.getParameterTypes().length > i && method.getParameterTypes()[i].equals(field.getType())) {
                            parametersMapping.add(new ParameterMapping(field.getType(), field.getName()));
                        } else {
                            break;
                        }
                        i++;
                    }
                    parametersMappingScored.add(new ParameterMappingScored(parametersMapping, i, true));
                    if (i == ownerClass.getDeclaredFields().length) {
                        break;
                    }
                }
            }
        }

        parameters = parametersMappingScored.isEmpty() ? new ArrayList<>() : parametersMappingScored.first().parametersMapping;
        staticFactory =  parametersMappingScored.isEmpty() ? false : parametersMappingScored.first().staticFactory;
    }

    private List<ParameterMapping> loadParametersMapping(Class<?> ownerClass, List<ParameterMatcher> parameterMatchers) {
        Map<Class<?>, PriorityQueue<String>> propertiesByClass = new HashMap<>();
        Map<String, Class<?>> classByProperty = new HashMap<>();
        asList(ownerClass.getDeclaredFields()).stream().forEach(field -> {
            PriorityQueue<String> properties = propertiesByClass.get(field.getType());
            if (properties == null) {
                properties = new PriorityQueue<>();
                propertiesByClass.put(field.getType(), properties);
            }
            properties.add(field.getName());
            classByProperty.put(field.getName(), field.getType());
        });
        return parameterMatchers.stream().map(parameterMatcher -> new ParameterMapping(parameterMatcher, propertiesByClass, classByProperty)).collect(toList());
    }

    public List<ParameterMapping> getParameters() {
        return parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isStaticFactory() {
        return staticFactory;
    }

    ConstructorMapping copy() {
        return new ConstructorMapping(methodName, parameters.stream().map(p -> p.copy()).collect(toList()), staticFactory);
    }

}
