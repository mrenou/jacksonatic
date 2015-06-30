package org.jacksonatic;

import java.util.*;
import java.util.stream.Collectors;

public class ConstructorMapping {

    private String methodName;

    private List<ParameterMapping> parameters;

    private boolean staticFactory = false;

    public static ConstructorMapping mapConstructor(Class<?> ownerClass, List<ParameterMatcher> parameters) {
        return new ConstructorMapping(ownerClass, null, parameters, false);
    }

    public static ConstructorMapping mapStaticFactory(Class<?> ownerClass, List<ParameterMatcher> parameters) {
        return new ConstructorMapping(ownerClass, null, parameters, true);
    }

    public static ConstructorMapping mapStaticFactory(Class<?> ownerClass, String methodName, List<ParameterMatcher> parameters) {
        return new ConstructorMapping(ownerClass, methodName, parameters, true);
    }

    public ConstructorMapping(Class<?> ownerClass, String methodName, List<ParameterMatcher> parameterMatchers, boolean staticFactory) {
        this.methodName = methodName;
        this.parameters = loadParmatersMapping(ownerClass, parameterMatchers);
        this.staticFactory = staticFactory;
    }

    private List<ParameterMapping> loadParmatersMapping(Class<?> ownerClass, List<ParameterMatcher> parameterMatchers) {
        Map<Class<?>, PriorityQueue<String>> propertiesByClass = new HashMap<>();
        Map<String, Class<?>> classByProperty = new HashMap<>();
        Arrays.asList(ownerClass.getDeclaredFields()).stream().forEach(field -> {
            PriorityQueue<String> properties = propertiesByClass.get(field.getClass());
            if (properties == null) {
                properties = new PriorityQueue<>();
                propertiesByClass.put(field.getType(), properties);
            }
            properties.add(field.getName());
            classByProperty.put(field.getName(), field.getType());
        });
        return parameterMatchers.stream().map(parameterMatcher -> new ParameterMapping(parameterMatcher, propertiesByClass, classByProperty)).collect(Collectors.toList());
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
}
