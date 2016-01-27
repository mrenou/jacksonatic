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

import com.github.mrenou.jacksonatic.internal.util.Copyable;
import com.github.mrenou.jacksonatic.internal.util.CopyableMergeableHashMap;

import static com.github.mrenou.jacksonatic.internal.util.StreamUtil.throwException;
import static java.util.stream.Collectors.toMap;

/**
 * Class mapping map
 */
public class ClassesMapping extends CopyableMergeableHashMap<Class<Object>, ClassMappingInternal<Object>> implements Copyable<ClassesMapping> {

    @Override
    public ClassesMapping copy() {
        return this.entrySet().stream().collect(toMap(
                Entry::getKey,
                e -> e.getValue().copy(),
                (v1, v2) -> throwException(new UnsupportedOperationException()),
                ClassesMapping::new)
        );
    }

}
