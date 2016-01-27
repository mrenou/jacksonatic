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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mrenou.jacksonatic.internal.annotations.Annotations;
import com.github.mrenou.jacksonatic.internal.mapping.PropertyMapperInternal;
import com.github.mrenou.jacksonatic.internal.util.Copyable;
import com.github.mrenou.jacksonatic.internal.util.Mergeable;
import com.github.mrenou.jacksonatic.mapping.MethodMapping;

import java.util.Arrays;
import java.util.Optional;

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

    public String getMappedName() {
        return Optional.ofNullable(annotations.get(JsonProperty.class))
                .map(annotation -> ((JsonProperty) annotation).value())
                .filter(name1 -> name1 != null && !name1.isEmpty())
                .orElse(methodSignature.name);
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
