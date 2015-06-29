package org.jacksonatic;

import java.util.List;

public class ConstructorMapping {

    private String methodName;

    private List<TypedParameter<?>> parameters;

    private boolean staticFactory = false;

    public static ConstructorMapping mapConstructor(List<TypedParameter<?>> parameters) {
        return new ConstructorMapping(null, parameters, false);
    }

    public static ConstructorMapping mapStaticFactory(List<TypedParameter<?>> parameters) {
        return new ConstructorMapping(null, parameters, true);
    }

    public static ConstructorMapping mapStaticFactory(String methodName, List<TypedParameter<?>> parameters) {
        return new ConstructorMapping(methodName, parameters, true);
    }

    public ConstructorMapping(String methodName, List<TypedParameter<?>> parameters,  boolean staticFactory) {
        this.methodName = methodName;
        this.parameters = parameters;
        this.staticFactory = staticFactory;
    }

    public List<TypedParameter<?>> getParameters() {
        return parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isStaticFactory() {
        return staticFactory;
    }
}
