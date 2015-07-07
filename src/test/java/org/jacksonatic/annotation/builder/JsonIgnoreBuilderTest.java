package org.jacksonatic.annotation.builder;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import org.assertj.core.api.StrictAssertions;
import org.jacksonatic.mapping.ClassMapping;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonIgnoreBuilderTest {

    private final Map<String, AnnotatedField> annotedFields = StreamSupport.stream(AnnotatedClass.construct(Pojo.class, null, null).fields().spliterator(), false)
            .collect(Collectors.toMap(AnnotatedField::getName, field -> field));

    static class Pojo {

    }

    @Test
    public void should_match_when_field_has_not_been_mapped() {
        ClassMapping<Pojo> classMapping = new ClassMapping<>(Pojo.class);

        final boolean hasToBuild = new JsonIgnoreBuilder().hasToBuild(annotedFields.get("field1"), classMapping, classMapping.getPropertyMapping("field1"));

        assertThat(hasToBuild).isTrue();
    }

    @Test
    public void should_not_match_when_field_has_been_mapped() {
        ClassMapping<Pojo> classMapping = new ClassMapping<>(Pojo.class);
        classMapping.map("field1");

        final boolean hasToBuild = new JsonIgnoreBuilder().hasToBuild(annotedFields.get("field1"), classMapping, classMapping.getPropertyMapping("field1"));

        assertThat(hasToBuild).isFalse();
    }

    @Test
    public void should_not_match_when_field_has_not_been_mapped_but_all_properties_have_been_mapped() {
        ClassMapping<Pojo> classMapping = new ClassMapping<>(Pojo.class);
        classMapping.mapAllProperties();

        final boolean hasToBuild = new JsonIgnoreBuilder().hasToBuild(annotedFields.get("field1"), classMapping, classMapping.getPropertyMapping("field1"));

        assertThat(hasToBuild).isFalse();
    }

    @Test
    public void should_match_when_field_has_been_ignored_but_all_properties_have_been_mapped() {
        ClassMapping<Pojo> classMapping = new ClassMapping<>(Pojo.class);
        classMapping.mapAllProperties();
        classMapping.ignore("field1");

        final boolean hasToBuild = new JsonIgnoreBuilder().hasToBuild(annotedFields.get("field1"), classMapping, classMapping.getPropertyMapping("field1"));

        assertThat(hasToBuild).isFalse();
    }

    @Test
    public void should_build() {
        ClassMapping<Pojo> classMapping = new ClassMapping<>(Pojo.class);

        final Annotation annotation = new JsonIgnoreBuilder().build(annotedFields.get("field1"), classMapping, classMapping.getPropertyMapping("field1"));

        assertThat(annotation).isInstanceOf(JsonIgnore.class);
        StrictAssertions.assertThat(((JsonIgnore) annotation).value()).isTrue();
    }

}