package org.jacksonatic.internal.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Optional;

public class MergeableTest {

    public class StringMergeable implements Mergeable<StringMergeable> {

        public String value;

        public StringMergeable(String value) {
            this.value = value;
        }

        @Override
        public StringMergeable mergeWith(StringMergeable other) {
            return new StringMergeable(value + other.value);
        }
    }

    @Test
    public void should_merge_one_value() {
        StringMergeable string = Mergeable.merge(new StringMergeable("un"));

        Assertions.assertThat(string.value).isEqualTo("un");
    }

    @Test
    public void should_merge_two_value() {
        StringMergeable string = Mergeable.merge(new StringMergeable("un"), new StringMergeable("deux"));

        Assertions.assertThat(string.value).isEqualTo("undeux");
    }

    @Test
    public void should_merge_two_value_with_first_null() {
        StringMergeable string = Mergeable.merge(null, new StringMergeable("deux"));

        Assertions.assertThat(string.value).isEqualTo("deux");
    }

    @Test
    public void should_merge_two_value_with_second_null() {
        StringMergeable string = Mergeable.merge(new StringMergeable("un"), null);

        Assertions.assertThat(string.value).isEqualTo("un");
    }

    @Test
    public void should_merge_multiple_values() {
        StringMergeable string = Mergeable.merge(new StringMergeable("un"), new StringMergeable("deux"), new StringMergeable("trois"), new StringMergeable("quatre"));

        Assertions.assertThat(string.value).isEqualTo("undeuxtroisquatre");
    }

    @Test
    public void should_merge_multiple_values_one_is_null() {
        StringMergeable string = Mergeable.merge(new StringMergeable("un"), null, new StringMergeable("trois"), new StringMergeable("quatre"));

        Assertions.assertThat(string.value).isEqualTo("untroisquatre");
    }

    @Test
    public void should_merge_multiple_optional_values() {
        Optional<StringMergeable> stringOpt = Mergeable.merge(Optional.of(new StringMergeable("un")), Optional.of(new StringMergeable("deux")), Optional.of(new StringMergeable("trois")), Optional.of(new StringMergeable("quatre")));

        Assertions.assertThat(stringOpt.get().value).isEqualTo("undeuxtroisquatre");
    }

    @Test
    public void should_merge_null_values() {
        StringMergeable string = Mergeable.merge((StringMergeable) null, (StringMergeable) null);

        Assertions.assertThat(string).isNull();
    }

    public class StringMergeableCopiable implements Mergeable<StringMergeableCopiable>, Copyable<StringMergeableCopiable> {

        public String value;

        public StringMergeableCopiable(String value) {
            this.value = value;
        }

        @Override
        public StringMergeableCopiable mergeWith(StringMergeableCopiable other) {
            return new StringMergeableCopiable(value + other.value);
        }

        @Override
        public StringMergeableCopiable copy() {
            return new StringMergeableCopiable(value);
        }
    }

    @Test
    public void should_merge_and_copy_one_value() {
        StringMergeableCopiable initialString = new StringMergeableCopiable("un");

        StringMergeableCopiable string = Mergeable.mergeOrCopy(initialString);
        string.value = "boom";

        Assertions.assertThat(string.value).isEqualTo("boom");
        Assertions.assertThat(initialString.value).isEqualTo("un");
    }

    @Test
    public void should_merge_and_copy_two_value_with_first_null() {
        StringMergeableCopiable initialString = new StringMergeableCopiable("deux");

        StringMergeableCopiable string = Mergeable.mergeOrCopy(null, initialString);
        string.value = "boom";

        Assertions.assertThat(string.value).isEqualTo("boom");
        Assertions.assertThat(initialString.value).isEqualTo("deux");
    }

    @Test
    public void should_merge_and_copy_two_value_with_second_null() {
        StringMergeableCopiable initialString = new StringMergeableCopiable("un");

        StringMergeableCopiable string = Mergeable.mergeOrCopy(initialString, null);
        string.value = "boom";

        Assertions.assertThat(string.value).isEqualTo("boom");
        Assertions.assertThat(initialString.value).isEqualTo("un");
    }

}