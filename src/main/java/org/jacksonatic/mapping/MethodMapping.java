/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic.mapping;

import org.jacksonatic.annotation.Annotations;

import java.util.Arrays;

import static org.jacksonatic.mapping.MethodSignature.methodSignature;
import static org.jacksonatic.mapping.MethodSignature.methodSignatureIgnoringParameters;

/**
 * Allowing to define jackson method mapping in a programmatic way.
 */
public class MethodMapping implements HasAnnotations<MethodMapping>, PropertyMapper<MethodMapping> {

    private MethodSignature methodSignature;

    private Annotations annotations;

    /**
     * Start a method mapping for the given field name
     * @param name
     * @param parameterTypes
     * @return
     */
    public static MethodMapping method(String name, Class<?>... parameterTypes) {
        return new MethodMapping(methodSignature(name, Arrays.asList(parameterTypes)), new Annotations());
    }

    private MethodMapping(MethodSignature methodSignature, Annotations annotations) {
        this.methodSignature = methodSignature;
        this.annotations = annotations;
    }

    public MethodMapping ignoreParameters() {
        methodSignature = methodSignatureIgnoringParameters(methodSignature.name);
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
    public MethodMapping builder() {
        return this;
    }

    MethodMapping copy() {
        return new MethodMapping(methodSignature, this.annotations.copy());
    }

    public MethodMapping copyWithParentMapping(MethodMapping methodParentMapping) {
        return new MethodMapping(methodSignature, this.annotations.size() == 0 ? methodParentMapping.annotations.copy() : annotations.copy());
    }
}
