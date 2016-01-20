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
package org.jacksonatic.mapping;

import org.jacksonatic.internal.mapping.field.FieldMappingInternal;

/**
 * Allowing to define jackson field mapping in a programmatic way.
 */
public interface FieldMapping extends PropertyMapper<FieldMapping> {

    /**
     * Start a field mapping for the given field name
     *
     * @param fieldName the name of the field
     * @return the field mapping
     */
    static FieldMapping field(String fieldName) {
        return new FieldMappingInternal(fieldName);
    }

}
