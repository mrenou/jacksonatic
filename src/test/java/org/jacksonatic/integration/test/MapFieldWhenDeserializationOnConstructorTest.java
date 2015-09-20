package org.jacksonatic.integration.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;

public class MapFieldWhenDeserializationOnConstructorTest {

    public static boolean captureConstructor = false;

    public static String firstConstructorCalled = "";

    static class Pojo {

        private String field1;

        private Integer field2;

        public Pojo() {
            setConstructorCallIfEmpty("public Pojo()");
        }

        public Pojo(String field1) {
            setConstructorCallIfEmpty("public Pojo(String field1)");
            this.field1 = field1;
        }

        public Pojo(String field1, Integer field2) {
            setConstructorCallIfEmpty("public Pojo(String field1, Integer field2)");
            this.field1 = field1;
            this.field2 = field2;
        }

    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before() {
        firstConstructorCalled = "";
        captureConstructor = false;
    }

    @Test
    public void map_without_fields_to_deserialize_by_constructor() throws IOException {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Pojo expectedPojo = new Pojo(null, null);
        configureMapping()
                .on(type(Pojo.class)
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\"}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo()");
    }

    @Test
    public void map_one_basic_field_to_deserialize_by_constructor() throws IOException {
        Pojo expectedPojo = new Pojo("field1");
        configureMapping()
                .on(type(Pojo.class)
                        .map("field1")
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\"}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1)");
    }

    @Test
    public void map_two_basic_fields_to_deserialize_by_constructor() throws IOException {
        Pojo expectedPojo = new Pojo("field1", 42);
        configureMapping()
                .on(type(Pojo.class)
                        .map("field1")
                        .map("field2")
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    @Test
    public void map_all_basic_fields_to_deserialize_by_constructor() throws IOException {
        Pojo expectedPojo = new Pojo("field1", 42);
        configureMapping()
                .on(type(Pojo.class)
                        .mapAll()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    @Test
    public void map_with_another_name_to_deserialize_by_constructor() throws IOException {
        Pojo expectedPojo = new Pojo("field1", 42);
        configureMapping()
                .on(type(Pojo.class)
                        .map("field1", "toto")
                        .map("field2")
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"toto\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    static class PojoParent {

        private String field1;

        PojoParent(String field1) {
            this.field1 = field1;
        }
    }

    static class PojoChild extends PojoParent {

        private String field2 = "poeut";

        public PojoChild(String field1) {
            super(field1);
            setConstructorCallIfEmpty("public PojoChild(String field1)");
        }
    }

    @Test
    public void map_one_inherited_field_to_deserialize() throws IOException {
        PojoChild expectedPojo = new PojoChild("field1");
        configureMapping()
                .on(type(PojoChild.class)
                        .map("field1")
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        PojoChild pojo = objectMapper.readValue("{\"field1\":\"field1\"}", PojoChild.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public PojoChild(String field1)");
    }

    private void captureConstructor() {
        captureConstructor = true;
    }

    private static void setConstructorCallIfEmpty(String constructor) {
        if (firstConstructorCalled.equals("") && captureConstructor) {
            firstConstructorCalled = constructor;
        }
    }

}