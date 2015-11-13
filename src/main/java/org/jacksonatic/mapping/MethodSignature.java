package org.jacksonatic.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MethodSignature {

    public final String name;

    public final List<Class<?>> parameterTypes;

    public final boolean ignoreParameters;

    private MethodSignature(String name, List<Class<?>> parameterTypes, boolean ignoreParameters) {
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.ignoreParameters = ignoreParameters;
    }

    public static MethodSignature methodSignature(String name, Class<?>... parameterTypes) {
        return methodSignature(name, Arrays.asList(parameterTypes));
    }

    public static MethodSignature methodSignature(String name, List<Class<?>> parameterTypes) {
        return new MethodSignature(name, parameterTypes, false);
    }

    public static MethodSignature methodSignatureIgnoringParameters(String name) {
        return new MethodSignature(name, new ArrayList<>(), true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodSignature that = (MethodSignature) o;
        return ignoreParameters == that.ignoreParameters &&
                Objects.equals(name, that.name) &&
                Objects.equals(parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameterTypes, ignoreParameters);
    }
}
