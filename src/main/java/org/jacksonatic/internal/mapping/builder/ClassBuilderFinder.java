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
package org.jacksonatic.internal.mapping.builder;


import org.jacksonatic.exception.ClassBuilderNotFoundException;
import org.jacksonatic.internal.mapping.ClassMappingInternal;

import java.util.Optional;

/**
 * Use {@link ClassMappingInternal } and {@link ClassBuilderCriteria } to find constructor or static factory to build
 * the type
 *
 * If criteria is any, try to find a constructor with a parametric signature having same types (or less) than the types
 * of class fields, ignoring static fields. If no constructor is found with all field types, try to find a static
 * factory with the same algorithm. The constructor is used if a constructor and a static factory match same field types
 *
 * Otherwise, try to find a constructor or a static factory with the same signature described in
 * {@link ClassBuilderCriteria }.
 */
public class ClassBuilderFinder {

    public ClassBuilderFinderFromAny classBuilderFinderFromAny = new ClassBuilderFinderFromAny();

    public ClassBuilderFinderFromCriteria classBuilderFinderFromCriteria = new ClassBuilderFinderFromCriteria();

    public Optional<ClassBuilderMapping> find(ClassMappingInternal<Object> classMapping, ClassBuilderCriteria classBuilderCriteria) {
        Optional<ClassBuilderMapping> classBuilderMappingOpt;
        if (classBuilderCriteria.isAny()) {
            classBuilderMappingOpt = classBuilderFinderFromAny.find(classMapping);
        } else {
            classBuilderMappingOpt = classBuilderFinderFromCriteria.find(classMapping, classBuilderCriteria);
        }
        if (!classBuilderMappingOpt.isPresent() && !classBuilderCriteria.isAny()) {
            throw new ClassBuilderNotFoundException(classBuilderCriteria, classMapping.getType());
        }
        return classBuilderMappingOpt;
    }

}
