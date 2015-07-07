package org.jacksonatic.annotation.builder;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.jacksonatic.mapping.ClassMapping;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonPropertyBuilderTest {

    private final Map<String, AnnotatedField> annotedFields = StreamSupport.stream(AnnotatedClass.construct(Pojo.class, null, null).fields().spliterator(), false)
            .collect(Collectors.toMap(AnnotatedField::getName, field -> field));

    static class Pojo {

    }

    @Test
    public void should_match_when_field_has_been_mapped_with_specific_name() {
        ClassMapping<Pojo> classMapping = new ClassMapping<>(Pojo.class);
        classMapping.map("field1", "toto");

        final boolean hasToBuild = new JsonPropertyBuilder().hasToBuild(annotedFields.get("field1"), classMapping, classMapping.getPropertyMapping("field1"));

        assertThat(hasToBuild).isTrue();
    }

    @Test
    public void should_not_match_when_field_has_been_just_mapped() {
        ClassMapping<Pojo> classMapping = new ClassMapping<>(Pojo.class);
        classMapping.map("field1");

        final boolean hasToBuild = new JsonPropertyBuilder().hasToBuild(annotedFields.get("field1"), classMapping, classMapping.getPropertyMapping("field1"));

        assertThat(hasToBuild).isFalse();
    }

    @Test
    public void should_build_with_mapped_name() {
        ClassMapping<Pojo> classMapping = new ClassMapping<>(Pojo.class);
        classMapping.map("field1", "toto");

        final Annotation annotation = new JsonPropertyBuilder().build(annotedFields.get("field1"), classMapping, classMapping.getPropertyMapping("field1"));

        assertThat(annotation).isInstanceOf(JsonProperty.class);
        assertThat(((JsonProperty)annotation).value()).isEqualTo("toto");
    }
}