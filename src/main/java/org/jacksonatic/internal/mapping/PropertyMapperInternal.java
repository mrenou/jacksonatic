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
package org.jacksonatic.internal.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jacksonatic.mapping.PropertyMapper;

/**
 * Property mapper interface with method for an internal use
 */
public interface PropertyMapperInternal<T> extends PropertyMapper<T>, HasAnnotationsInternal<T> {

    default boolean isMapped() {
        return getAnnotations().containsKey(JsonProperty.class) && !getAnnotations().containsKey(JsonIgnore.class);
    }

}
