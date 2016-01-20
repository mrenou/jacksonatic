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
package org.jacksonatic.internal.mapping.builder.parameter;

import org.jacksonatic.mapping.ParameterCriteria;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class ParameterCriteriaInternal implements org.jacksonatic.mapping.ParameterCriteria {

    private Class<?> parameterClass;

    private String jsonProperty;

    private String fieldName;

    public static List<ParameterCriteriaInternal> parameterCriteriaToInternal(ParameterCriteria... parameterCriteriaArray) {
        return parameterCriteriaToInternal(asList(parameterCriteriaArray));
    }

    public static List<ParameterCriteriaInternal> parameterCriteriaToInternal(List<ParameterCriteria> parameterCriteriaList) {
        return parameterCriteriaList.stream().map(parameterCriteria -> (ParameterCriteriaInternal) parameterCriteria).collect(toList());
    }

    public ParameterCriteriaInternal(Class<?> parameterClass, String jsonProperty, String fieldName) {
        this.parameterClass = parameterClass;
        this.jsonProperty = jsonProperty;
        this.fieldName = fieldName;
    }

    public String getJsonProperty() {
        return jsonProperty;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    @Override
    public ParameterCriteria mappedBy(String jsonProperty) {
        this.jsonProperty = jsonProperty;
        return this;
    }

    @Override
    public String toString() {
        return "ParameterCriteria{" +
                "parameterClass=" + parameterClass +
                ", jsonProperty='" + jsonProperty + '\'' +
                ", fieldName='" + fieldName + '\'' +
                '}';
    }


}
