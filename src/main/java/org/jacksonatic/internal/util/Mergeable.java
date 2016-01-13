/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic.internal.util;

import java.util.Optional;

public interface Mergeable<T extends Mergeable<T>> {

    T mergeWith(T other);

    static <T extends Mergeable<T>> T merge(T o1, T o2) {
        return merge(Optional.ofNullable(o1), Optional.ofNullable(o2)).orElse(null);
    }

    static <T extends Mergeable<T>> Optional<T> merge(Optional<T> opt1, Optional<T> opt2) {
        return Optional.ofNullable(opt1.map(o1 -> opt2.map(o2 -> o1.mergeWith(o2)).orElse(o1)).orElse(opt2.orElse(null)));
    }

    static <T extends Mergeable<T> & Copyable<T>> T mergeOrCopy(T o1, T o2) {
        return mergeOrCopy(Optional.ofNullable(o1), Optional.ofNullable(o2)).orElse(null);
    }

    static <T extends Mergeable<T> & Copyable<T>> Optional<T> mergeOrCopy(Optional<T> opt1, Optional<T> opt2) {
        return Optional.ofNullable(opt1.map(o1 -> opt2.map(o2 -> o1.mergeWith(o2)).orElse(o1.copy())).orElse(opt2.map(o2 -> o2.copy()).orElse(null)));
    }
}
