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

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.jacksonatic.mapping.ParametersMappingBuilder.buildParametersMapping;

/**
 * Criteria to match a constructor or a static factory
 */
public class ClassBuilderCriteria {

    private String methodName;

    private List<ParameterMapping> parametersMapping;

    private boolean staticFactory = false;

    private boolean any = false;

    public static ClassBuilderCriteria mapConstructor(Class<?> classToBuild, List<ParameterCriteria> parameterCriteriaList) {
        return new ClassBuilderCriteria(classToBuild, null, parameterCriteriaList, false);
    }

    public static ClassBuilderCriteria mapStaticFactory(Class<?> classToBuild, List<ParameterCriteria> parameterCriteriaList) {
        return new ClassBuilderCriteria(classToBuild, null, parameterCriteriaList, true);
    }

    public static ClassBuilderCriteria mapStaticFactory(Class<?> classToBuild, String methodName, List<ParameterCriteria> parameterCriteriaList) {
        return new ClassBuilderCriteria(classToBuild, methodName, parameterCriteriaList, true);
    }

    public static ClassBuilderCriteria mapAConstructorOrStaticFactory() {
        return new ClassBuilderCriteria();
    }


    private ClassBuilderCriteria(Class<?> classToBuild, String methodName, List<ParameterCriteria> parameterCriterias, boolean staticFactory) {
        this(methodName, buildParametersMapping(classToBuild, parameterCriterias), staticFactory, false);
    }

    private ClassBuilderCriteria() {
        this("", new ArrayList<>(), false, true);
    }

    private ClassBuilderCriteria(String methodName, List<ParameterMapping> parametersMapping, boolean staticFactory, boolean any) {
        this.methodName = methodName;
        this.parametersMapping = parametersMapping;
        this.staticFactory = staticFactory;
        this.any = any;
    }

    public boolean isAny() {
        return any;
    }

    public List<ParameterMapping> getParametersMapping() {
        return parametersMapping;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isStaticFactory() {
        return staticFactory;
    }

    ClassBuilderCriteria copy() {
        return new ClassBuilderCriteria(methodName,
                parametersMapping,
                staticFactory,
                any);
    }

    public String mappingAsString() {
        if (staticFactory) {
            return "staticFactory='" + methodName + '(' + parametersMapping.stream().map(pm -> pm.getParameterClass()).collect(toList()) + ")";
        } else if (!any) {
            return "constructor='" + + '(' + parametersMapping.stream().map(pm -> pm.getParameterClass()).collect(toList()) + ")";
        } else {
            return "any";
        }
    }
}
