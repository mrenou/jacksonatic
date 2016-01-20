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
package org.jacksonatic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.internal.JacksonaticInternal;
import org.jacksonatic.internal.util.Copyable;
import org.jacksonatic.mapping.ClassMapping;

/**
 * Entry point of the api, allowing to define a jackson class mapping collection in a programmatic way.
 */
public interface Jacksonatic extends Copyable<Jacksonatic> {

    static Jacksonatic configureMapping() {
        return new JacksonaticInternal();
    }

    /**
     * add a class mapping
     *
     * @param classMapping the class mapping to add
     * @return jacksonatic
     */
    Jacksonatic on(ClassMapping<?> classMapping);

    /**
     * add a class mapping with all its fields mapped
     *
     * @param classMapping the class mapping to add
     * @return jacksonatic
     */
    Jacksonatic mapAllFieldsOn(ClassMapping<?> classMapping);

    /**
     * register mapping configuration in a {@link com.fasterxml.jackson.databind.ObjectMapper}
     *
     * @param objectMapper object mapper to use
     */
    void registerIn(ObjectMapper objectMapper);

}