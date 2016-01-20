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
package org.jacksonatic.internal.util;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public interface Copyable<T extends Copyable<T>> {

    T copy();

    static <T extends Copyable<T>> T copy(T o) {
        return copy(Optional.of(o)).orElse(null);
    }

    static <T extends Copyable<T>> Optional<T> copy(Optional<T> opt) {
        return Optional.ofNullable(opt.map(o -> o.copy()).orElse(null));
    }

    static <T extends Copyable<T>> List<T> copy(List<T> list) {
        return list.stream().map(o -> o.copy()).collect(toList());
    }
}
