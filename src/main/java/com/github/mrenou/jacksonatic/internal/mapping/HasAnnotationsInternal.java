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
package com.github.mrenou.jacksonatic.internal.mapping;

import com.github.mrenou.jacksonatic.mapping.HasAnnotations;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * Has annotation interface with method for an internal use
 */
interface HasAnnotationsInternal<T> extends HasAnnotations<T> {

    default boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return getAnnotations().containsKey(annotationType);
    }

    @SuppressWarnings("unchecked")
    default <A extends Annotation> Optional<A> getAnnotationOpt(Class<A> annotationType) {
        return (Optional<A>) getAnnotations().getOpt(annotationType);
    }
}
