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
package org.jacksonatic.internal.mapping.builder;

import org.jacksonatic.internal.mapping.builder.parameter.ParameterCriteriaInternal;
import org.jacksonatic.internal.mapping.builder.parameter.ParameterMapping;
import org.jacksonatic.internal.mapping.builder.parameter.ParametersMappingBuilder;
import org.jacksonatic.internal.util.Copyable;
import org.jacksonatic.internal.util.Mergeable;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Criteria to match a constructor or a static factory
 * <p>
 * Immutable
 */
public class ClassBuilderCriteria implements Copyable<ClassBuilderCriteria>, Mergeable<ClassBuilderCriteria> {

    private String methodName;

    private List<ParameterMapping> parametersMapping;

    private boolean staticFactory = false;

    private boolean any = false;

    private ParametersMappingBuilder parametersMappingBuilder = new ParametersMappingBuilder();

    public static ClassBuilderCriteria mapConstructor(Class<?> classToBuild, List<ParameterCriteriaInternal> parameterCriteriaList) {
        return new ClassBuilderCriteria(classToBuild, null, parameterCriteriaList, false);
    }

    public static ClassBuilderCriteria mapStaticFactory(Class<?> classToBuild, List<ParameterCriteriaInternal> parameterCriteriaList) {
        return new ClassBuilderCriteria(classToBuild, null, parameterCriteriaList, true);
    }

    public static ClassBuilderCriteria mapStaticFactory(Class<?> classToBuild, String methodName, List<ParameterCriteriaInternal> parameterCriteriaList) {
        return new ClassBuilderCriteria(classToBuild, methodName, parameterCriteriaList, true);
    }

    public static ClassBuilderCriteria mapAConstructorOrStaticFactory() {
        return new ClassBuilderCriteria();
    }


    private ClassBuilderCriteria(Class<?> classToBuild, String methodName, List<ParameterCriteriaInternal> parameterCriteriaList, boolean staticFactory) {
        this.methodName = methodName;
        this.parametersMapping = parametersMappingBuilder.build(classToBuild, parameterCriteriaList);
        this.staticFactory = staticFactory;
        this.any = false;
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

    @Override
    public ClassBuilderCriteria copy() {
        return new ClassBuilderCriteria(methodName, Copyable.copy(parametersMapping), staticFactory, any);
    }

    @Override
    public ClassBuilderCriteria mergeWith(ClassBuilderCriteria other) {
        return this.copy();
    }


    public String mappingAsString() {
        if (staticFactory) {
            return "staticFactory='" + methodName + '(' + parametersMapping.stream().map(ParameterMapping::getParameterClass).collect(toList()) + ")";
        } else if (!any) {
            return "constructor='" + +'(' + parametersMapping.stream().map(ParameterMapping::getParameterClass).collect(toList()) + ")";
        } else {
            return "any";
        }
    }
}