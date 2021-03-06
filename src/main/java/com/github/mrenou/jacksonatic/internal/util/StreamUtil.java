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
package com.github.mrenou.jacksonatic.internal.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    @SafeVarargs
    public static <T> Optional<T> getFirstPresent(Supplier<Optional<T>>... suppliers) {
        return Arrays.asList(suppliers).stream()
                .map(Supplier::get)
                .filter(Optional::isPresent)
                .findFirst()
                .flatMap(o -> o);
    }

    public static <T> T throwException(RuntimeException runtimeException) {
        throw runtimeException;
    }

}
