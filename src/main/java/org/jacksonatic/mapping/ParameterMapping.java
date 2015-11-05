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

import org.jacksonatic.annotation.AnnotationBuilder;
import org.jacksonatic.annotation.Annotations;

import java.lang.annotation.Annotation;
import java.util.Map;

import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;

/**
 * Define parameter mapping
 */
public class ParameterMapping implements HasAnnotations {

    private Class<?> parameterClass;

    private Annotations annotations;

    public ParameterMapping(Class<?> parameterClass, String jsonProperty) {
        this(parameterClass, new Annotations());
        map(jsonProperty);
    }

    ParameterMapping(Class<?> parameterClass, Annotations annotations) {
        this.parameterClass = parameterClass;
        this.annotations = annotations;
    }

    public void addAnnotation(AnnotationBuilder annotationBuilder) {
        Annotation annotation = annotationBuilder.build();
        annotations.put(annotation.getClass(), annotation);
    }

    public void map(String mappedName) {
        addAnnotation(jsonProperty(mappedName));
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    ParameterMapping copy() {
        return new ParameterMapping(parameterClass, annotations.copy());
    }
}
