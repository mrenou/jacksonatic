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
package com.fasterxml.jackson.databind.introspect;


import org.jacksonatic.internal.mapping.ClassBuilderMapping;

import java.util.List;
import java.util.stream.IntStream;

public class AnnotatedClassUpdater {

    public static void setConstructors(AnnotatedClass annotatedClass, List<AnnotatedConstructor> annotatedConstructors) {
        annotatedClass._constructors = annotatedConstructors;
    }

    public static void setCreatorMethods(AnnotatedClass annotatedClass, List<AnnotatedMethod> annotatedConstructors) {
        annotatedClass._creatorMethods = annotatedConstructors;
    }

    public static void setCreatorMethods(AnnotatedClass annotatedClass, ClassBuilderMapping classBuilderMapping) {
        AnnotatedMethod staticFactoryMethod = annotatedClass.getStaticMethods().stream()
                .filter(method -> method.getMember() == classBuilderMapping.getStaticFactory())
                .findFirst()
                .get();
        classBuilderMapping.getAnnotations().values().stream().forEach(annotation -> staticFactoryMethod.addOrOverride(annotation));
        IntStream.range(0, classBuilderMapping.getParametersMapping().size())
                .forEach(index -> classBuilderMapping.getParametersMapping().get(index).getAnnotations().values().stream()
                        .forEach(annotation -> staticFactoryMethod.addOrOverrideParam(index, annotation)));
    }

    public static void setFields(AnnotatedClass annotatedClass, List<AnnotatedField> annotatedFields) {
        annotatedClass._fields = annotatedFields;
    }
}
