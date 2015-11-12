package org.jacksonatic.mapping;

import java.util.Arrays;
import java.util.List;

public class MethodSignature {

    public final String name;

    public final List<Class<?>> parameterTypes;

    public MethodSignature(String name, List<Class<?>> parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    public MethodSignature(String name, Class<?>[] parameterTypes) {
        this(name, Arrays.asList(parameterTypes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodSignature that = (MethodSignature) o;

        if (!name.equals(that.name)) return false;
        return parameterTypes.equals(that.parameterTypes);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + parameterTypes.hashCode();
        return result;
    }
}
