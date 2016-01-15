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
package org.jacksonatic.internal.annotations;

import org.jacksonatic.annotation.AnnotationBuilder;
import org.jacksonatic.internal.util.MapUtil;
import org.jacksonatic.internal.util.TypedHashMap;

import java.lang.annotation.Annotation;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class Annotations extends TypedHashMap<Class<? extends Annotation>, Annotation> {

    public void add(AnnotationBuilder annotationBuilder) {
        Annotation annotation = annotationBuilder.build();
        put(annotation.annotationType(), annotation);
    }

    // TODO use copy from typedhashmap
    public Annotations copy() {
        return entrySet().stream().collect(toMap(Map.Entry::getKey,
                Map.Entry::getValue,
                (v1, v2) -> {
                    throw new UnsupportedOperationException();
                },
                () -> new Annotations()
        ));
    }

    public Annotations mergeWith(Annotations map) {
        return MapUtil.merge(this, map, annotation -> annotation, (annotation, annotationParent) -> annotation);
    }
}
