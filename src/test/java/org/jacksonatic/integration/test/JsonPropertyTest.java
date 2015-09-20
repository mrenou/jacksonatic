package org.jacksonatic.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;
import static org.jacksonatic.mapping.PropertyMapping.property;

public class JsonPropertyTest {

    static class Pojo {

        private String field1;

        private Integer field2;

        public Pojo() {
        }

        public Pojo(String field1, Integer field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public Integer getField2() {
            return field2;
        }

        public void setField2(Integer field2) {
            this.field2 = field2;
        }

    }

    public static final Pojo POJO = new Pojo("field1", 42);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void map_without_fields_to_serialize() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .on(type(Pojo.class))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{}");
    }

    @Test
    public void map_without_fields_to_deserialize() throws IOException {
        Pojo expectedPojo = new Pojo(null, null);
        configureMapping()
                .on(type(Pojo.class))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\"}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
    }

    @Test
    public void map_one_basic_field_to_serialize() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .on(property("field1").add(jsonProperty())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\"}");

    }

    @Test
    public void map_one_basic_field_to_deserialize() throws IOException {
        Pojo expectedPojo = new Pojo("field1", null);
        configureMapping()
                .on(type(Pojo.class)
                        .on(property("field1").add(jsonProperty())))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\"}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
    }

    @Test
    public void map_two_basic_fields_to_serialize() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .on(property("field1").add(jsonProperty()))
                        .on(property("field2").add(jsonProperty())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_two_basic_fields_to_deserialize() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .on(property("field1").add(jsonProperty()))
                        .on(property("field2").add(jsonProperty())))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(POJO);
    }

    @Test
    public void map_with_another_name_to_serialize() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .on(property("field1").add(jsonProperty("toto")))
                        .on(property("field2").add(jsonProperty())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field2\":42,\"toto\":\"field1\"}");
    }

    @Test
    public void map_with_another_name_to_deserialize() throws IOException {
        Pojo expectedPojo = new Pojo("field1", 42);
        configureMapping()
                .on(type(Pojo.class)
                        .on(property("field1").add(jsonProperty("toto")))
                        .on(property("field2").add(jsonProperty())))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"toto\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
    }

    static class PojoParent {

        private String field1;

        PojoParent() {

        }

        PojoParent(String field1) {
            this.field1 = field1;
        }
    }

    static class PojoChild extends PojoParent {

        private String field2;

        public PojoChild() {

        }

        public PojoChild(String field1, String field2) {
            super(field1);
            this.field2 = field2;
        }
    }

    @Test
    public void map_one_inherited_field_to_serialize() throws JsonProcessingException {
        configureMapping()
                .on(type(PojoChild.class)
                        .on(property("field1").add(jsonProperty())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(new PojoChild("field1", "field2"));

        assertThat(json).isEqualTo("{\"field1\":\"field1\"}");

    }

    @Test
    public void map_one_parent_field_to_serialize2() throws JsonProcessingException {
        configureMapping()
                .on(type(PojoParent.class)
                        .on(property("field1").add(jsonProperty())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(new PojoChild("field1", "field2"));

        assertThat(json).isEqualTo("{\"field1\":\"field1\"}");

    }

    @Test
    public void map_one_inherited_field_to_deserialize() throws IOException {
        PojoChild expectedPojo = new PojoChild("field1", null);
        configureMapping()
                .on(type(PojoChild.class)
                        .on(property("field1").add(jsonProperty())))
                .registerIn(objectMapper);

        PojoChild pojo = objectMapper.readValue("{\"field1\":\"field1\"}", PojoChild.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
    }

    @Test
    public void map_one_parent_field_to_deserialize() throws IOException {
        PojoChild expectedPojo = new PojoChild("field1", null);
        configureMapping()
                .on(type(PojoParent.class)
                        .on(property("field1").add(jsonProperty())))
                .registerIn(objectMapper);

        PojoChild pojo = objectMapper.readValue("{\"field1\":\"field1\"}", PojoChild.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
    }


}