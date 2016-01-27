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
package com.github.mrenou.jacksonatic.internal.mapping.builder.parameter;

import com.github.mrenou.jacksonatic.internal.annotations.Annotations;
import com.github.mrenou.jacksonatic.internal.util.Copyable;
import com.github.mrenou.jacksonatic.mapping.HasAnnotations;

import static com.github.mrenou.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;

/**
 * Define parameter annotations mapping
 */
public class ParameterMapping implements HasAnnotations<ParameterMapping>, Copyable<ParameterMapping> {

    private Class<?> parameterClass;

    private Annotations annotations;

    public ParameterMapping(Class<?> parameterClass, String jsonProperty) {
        this(parameterClass, new Annotations());
        map(jsonProperty);
    }

    private ParameterMapping(Class<?> parameterClass, Annotations annotations) {
        this.parameterClass = parameterClass;
        this.annotations = annotations;
    }

    private void map(String mappedName) {
        add(jsonProperty(mappedName));
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    @Override
    public ParameterMapping copy() {
        return new ParameterMapping(parameterClass, annotations.copy());
    }
}
