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

import org.jacksonatic.internal.mapping.builder.parameter.ParameterCriteriaInternal;

/**
 * Criteria to match a parameter by type or field name class
 */
public interface ParameterCriteria {

    /**
     * Match a parameter with the type and use the name as json property
     *
     * @param parameterClass the type
     * @param jsonProperty the json property
     * @return the parameter criteria
     */
    static ParameterCriteria match(Class<?> parameterClass, String jsonProperty) {
        return new ParameterCriteriaInternal(parameterClass, null, jsonProperty);
    }

    /**
     * MMatch a parameter with the same type of the next field and use this name as json property
     *
     * @param fieldName the type
     * @param jsonProperty the json property
     * @return the parameter criteria
     */
    static ParameterCriteria match(String fieldName, String jsonProperty) {
        return new ParameterCriteriaInternal(null, fieldName, jsonProperty);
    }

    /**
     * Match a parameter with the type and use the name of the next field with the same type
     *
     * @param parameterClass the type
     * @return the parameter criteria
     */
    static ParameterCriteria matchType(Class<?> parameterClass) {
        return new ParameterCriteriaInternal(parameterClass, null, null);
    }

    /**
     * Match a parameter with the same type of the next field and use its name as json property
     *
     * @param fieldName the field name
     * @return the parameter criteria
     */
    static ParameterCriteria matchField(String fieldName) {
        return new ParameterCriteriaInternal(null, fieldName, null);
    }

    /**
     * Use this name as json property
     *
     * @param jsonProperty the name of the json property
     * @return the parameter criteria
     */
    ParameterCriteria mappedBy(String jsonProperty);
}
