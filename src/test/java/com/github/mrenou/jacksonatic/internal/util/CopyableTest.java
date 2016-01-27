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

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;

public class CopyableTest {

    public class StringCopiable implements Copyable<StringCopiable> {

        public String value;

        public StringCopiable(String value) {
            this.value = value;
        }

        @Override
        public StringCopiable copy() {
            return new StringCopiable(value);
        }
    }

    @Test
    public void should_copy_one_value() {
        StringCopiable initialValue = new StringCopiable("un");

        StringCopiable string = Copyable.copy(initialValue);
        initialValue.value = "boom";

        Assertions.assertThat(string.value).isEqualTo("un");
    }

    @Test
    public void should_copy_one_optional_value() {
        StringCopiable initialValue = new StringCopiable("un");

        Optional<StringCopiable> string = Copyable.copy(Optional.of(initialValue));
        initialValue.value = "boom";

        Assertions.assertThat(string.get().value).isEqualTo("un");
    }

}