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
package com.github.mrenou.jacksonatic.internal.annotations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mrenou.jacksonatic.annotation.AnnotationBuilder;
import com.github.mrenou.jacksonatic.internal.util.MapUtil;
import com.github.mrenou.jacksonatic.internal.util.TypedHashMap;

import java.lang.annotation.Annotation;

public class Annotations extends TypedHashMap<Class<? extends Annotation>, Annotation> {

    public void add(AnnotationBuilder annotationBuilder) {
        Annotation annotation = annotationBuilder.build();
        put(annotation.annotationType(), annotation);
    }

    public void remove(Class<? extends Annotation> annotationClass) {
        super.remove(annotationClass);
    }

    public Annotations copy() {
        return super.copy(Annotations::new);
    }

    public Annotations mergeWith(Annotations map) {
        return MapUtil.merge(this, map, annotation -> annotation, (annotation, annotationParent) -> annotation);
    }

    public Annotations mergeWithParent(Annotations parentAnnotations) {
        boolean ignoredButMappedByParent = parentAnnotations.containsKey(JsonProperty.class) && containsKey(JsonIgnore.class);
        boolean mappedButIgnoredByParent = parentAnnotations.containsKey(JsonIgnore.class) && containsKey(JsonProperty.class);
        Annotations annotations = MapUtil.merge(this, parentAnnotations, annotation -> annotation, (annotation, annotationParent) -> annotation);
        if (ignoredButMappedByParent) {
            annotations.remove(JsonProperty.class);
        }
        if (mappedButIgnoredByParent) {
            annotations.remove(JsonIgnore.class);
        }
        return annotations;
    }
}
