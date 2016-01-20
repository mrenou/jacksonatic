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
 * Criteria to match a parameter by class or field name class
 */
public interface ParameterCriteria {

    static ParameterCriteria match(Class<?> parameterClass, String jsonProperty) {
        return new ParameterCriteriaInternal(parameterClass, null, jsonProperty);
    }

    static ParameterCriteria match(String fieldName, String jsonProperty) {
        return new ParameterCriteriaInternal(null, fieldName, jsonProperty);
    }

    static ParameterCriteria matchType(Class<?> parameterClass) {
        return new ParameterCriteriaInternal(parameterClass, null, null);
    }

    static ParameterCriteria matchField(String fieldName) {
        return new ParameterCriteriaInternal(null, fieldName, null);
    }

    ParameterCriteria mappedBy(String jsonProperty);
}
