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
package com.github.mrenou.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.github.mrenou.jacksonatic.annotation.JacksonaticJsonIgnore.jsonIgnore;
import static com.github.mrenou.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;

/**
 * Can map a property
 */
public interface PropertyMapper<T> extends HasAnnotations<T> {

    /**
     * map the property
     *
     * @return the instance mapper
     */
    @SuppressWarnings("unchecked")
    default T map() {
        add(jsonProperty());
        return (T) this;
    }

    /**
     * map the property with the given name
     *
     * @param jsonProperty the new name
     * @return the instance mapper
     */
    @SuppressWarnings("unchecked")
    default T mapTo(String jsonProperty) {
        add(jsonProperty(jsonProperty));
        return (T) this;
    }

    /**
     * ignore the property
     *
     * @return the instance mapper
     */
    @SuppressWarnings("unchecked")
    default T ignore() {
        add(jsonIgnore());
        return (T) this;
    }

}
