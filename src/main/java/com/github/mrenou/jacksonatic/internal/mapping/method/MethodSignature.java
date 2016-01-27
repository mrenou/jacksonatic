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
package com.github.mrenou.jacksonatic.internal.mapping.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Override
    public String toString() {
        String parameterTypesStr;
        if (ignoreParameters) {
            parameterTypesStr = "*";
        } else {
            parameterTypesStr = parameterTypes.stream().map(Class::getSimpleName).collect(Collectors.joining(","));
        }
        return name + "(" + parameterTypesStr + ")";
    }
}
