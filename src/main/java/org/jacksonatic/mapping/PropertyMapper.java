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

import static org.jacksonatic.annotation.JacksonaticJsonIgnore.jsonIgnore;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;

public interface PropertyMapper extends HasAnnotations {

    /**
     * map the field
     *
     * @return
     */
    default PropertyMapper map() {
        add(jsonProperty());
        return this;
    }

    /**
     * map the field with the given name
     *
     * @return
     */
    default PropertyMapper mapTo(String jsonProperty) {
        add(jsonProperty(jsonProperty));
        return this;
    }

    /**
     * ignore the field
     *
     * @return
     */
    default PropertyMapper ignore() {
        add(jsonIgnore());
        return this;
    }

}
