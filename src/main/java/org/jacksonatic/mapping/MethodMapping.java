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

import org.jacksonatic.internal.annotations.Annotations;
import org.jacksonatic.internal.mapping.MethodMappingInternal;

import java.util.Arrays;

import static org.jacksonatic.internal.mapping.MethodSignature.methodSignature;

/**
 * Allowing to define jackson method mapping in a programmatic way.
 */
public interface MethodMapping extends HasAnnotations<MethodMapping>, PropertyMapper<MethodMapping> {

    /**
     * Start a method mapping for the given field name
     * @param name
     * @param parameterTypes
     * @return
     */
    static MethodMapping method(String name, Class<?>... parameterTypes) {
        return new MethodMappingInternal(methodSignature(name, Arrays.asList(parameterTypes)), new Annotations());
    }

    MethodMapping ignoreParameters();

}
