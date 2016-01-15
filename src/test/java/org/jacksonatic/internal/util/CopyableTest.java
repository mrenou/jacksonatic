package org.jacksonatic.internal.util;

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