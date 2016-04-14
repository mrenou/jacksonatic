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
package com.github.mrenou.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mrenou.jacksonatic.annotation.AnnotationBuilder;
import com.github.mrenou.jacksonatic.annotation.JacksonaticJsonIgnore;
import com.github.mrenou.jacksonatic.annotation.JacksonaticJsonProperty;
import com.github.mrenou.jacksonatic.internal.annotations.Annotations;

import java.lang.annotation.Annotation;

/**
 * Can have annotations
 */
public interface HasAnnotations<T> {

    /**
     * Add an annotation
     *
     * @param annotationBuilder the annotation builder
     * @return the instance adding annotation
     */
    @SuppressWarnings("unchecked")
    default T add(AnnotationBuilder annotationBuilder) {
        if (annotationBuilder.getClass().equals(JacksonaticJsonProperty.Builder.class)) {
            remove(JsonIgnore.class);
        }
        if (annotationBuilder.getClass().equals(JacksonaticJsonIgnore.Builder.class)) {
            remove(JsonProperty.class);
        }
        getAnnotations().add(annotationBuilder);
        return (T) this;
    }

    default T remove(Class<? extends Annotation> annotationClass) {
        getAnnotations().remove(annotationClass);
        return (T) this;
    }

    Annotations getAnnotations();

}
