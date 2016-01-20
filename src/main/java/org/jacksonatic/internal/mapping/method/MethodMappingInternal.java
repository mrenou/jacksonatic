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
package org.jacksonatic.internal.mapping.method;

import org.jacksonatic.internal.annotations.Annotations;
import org.jacksonatic.internal.mapping.PropertyMapperInternal;
import org.jacksonatic.internal.util.Copyable;
import org.jacksonatic.internal.util.Mergeable;
import org.jacksonatic.mapping.MethodMapping;

import java.util.Arrays;

/**
 * Define annotations mapping for a method
 */
public class MethodMappingInternal implements MethodMapping, PropertyMapperInternal<MethodMapping>, Copyable<MethodMappingInternal>, Mergeable<MethodMappingInternal> {

    private MethodSignature methodSignature;

    private Annotations annotations;

    public static MethodMappingInternal method(String name, Class<?>... parameterTypes) {
        return new MethodMappingInternal(MethodSignature.methodSignature(name, Arrays.asList(parameterTypes)), new Annotations());
    }

    public MethodMappingInternal(MethodSignature methodSignature, Annotations annotations) {
        this.methodSignature = methodSignature;
        this.annotations = annotations;
    }

    public MethodMappingInternal ignoreParameters() {
        methodSignature = MethodSignature.methodSignatureIgnoringParameters(methodSignature.name);
        return this;
    }

    public MethodSignature getMethodSignature() {
        return methodSignature;
    }

    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    @Override
    public MethodMappingInternal copy() {
        return new MethodMappingInternal(methodSignature, this.annotations.copy());
    }

    @Override
    public MethodMappingInternal mergeWith(MethodMappingInternal methodParentMapping) {
        return new MethodMappingInternal(methodSignature, this.annotations.size() == 0 ? methodParentMapping.annotations.copy() : annotations.copy());
    }

}
