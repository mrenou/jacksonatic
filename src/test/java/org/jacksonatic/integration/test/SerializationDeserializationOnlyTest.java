package org.jacksonatic.integration.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;
import static org.jacksonatic.mapping.ParameterCriteria.matchType;

public class SerializationDeserializationOnlyTest {

    static class Pojo {

        public String field1;

        public String field2;

        public String field3;

        public Pojo(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public Pojo(String field1, String field2, String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }

        private Pojo() {
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void map_is_different_between_serialization_and_deserialization() throws IOException {
        Pojo pojo = new Pojo("1", "2", "3");

        configureMapping()
                .on(type(Pojo.class)
                        .map("field1")
                        .onSerialization()
                        .map("field2")
                        .onDeserialization()
                        .map("field3"))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(pojo);
        assertThat(json).isEqualTo("{\"field1\":\"1\",\"field2\":\"2\"}");

        Pojo pojoFromJson = objectMapper.readValue("{\"field1\":\"1\",\"field2\":\"2\",\"field3\":\"3\"}", Pojo.class);
        assertThat(pojoFromJson.field1).isEqualTo("1");
        assertThat(pojoFromJson.field2).isNull();
        assertThat(pojoFromJson.field3).isEqualTo("3");
    }

    @Test
    public void serialization_map_overrides_parent_mapping() throws IOException {
        Pojo pojo = new Pojo("1", "2", "3");

        configureMapping()
                .on(type(Pojo.class)
                        .map("field1")
                        .map("field2")
                        .onSerialization()
                        .ignore("field2"))
                .registerIn(objectMapper);


        String json = objectMapper.writeValueAsString(pojo);
        assertThat(json).isEqualTo("{\"field1\":\"1\"}");
    }

    @Test
    public void serialization_ignore_overrides_parent_mapping() throws IOException {
        Pojo pojo = new Pojo("1", "2", "3");

        configureMapping()
                .on(type(Pojo.class)
                        .all()
                        .map("field1")
                        .ignore("field2")
                        .ignore("field3")
                        .onSerialization()
                        .map("field2"))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(pojo);
        assertThat(json).isEqualTo("{\"field1\":\"1\",\"field2\":\"2\"}");
    }

    @Test
    public void serialization_mapname_overrides_parent_mapping() throws IOException {
        Pojo pojo = new Pojo("1", "2", "3");

        configureMapping()
                .on(type(Pojo.class)
                        .map("field1", "toto")
                        .onSerialization()
                        .map("field1", "tata"))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(pojo);
        assertThat(json).isEqualTo("{\"tata\":\"1\"}");
    }

    @Test
    public void serialization_constructor_overrides_parent_mapping() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .withConstructor(matchType(String.class), matchType(String.class), matchType(String.class))
                        .onDeserialization()
                        .withConstructor(matchType(String.class), matchType(String.class)))
                .registerIn(objectMapper);

        Pojo pojoFromJson = objectMapper.readValue("{\"field1\":\"1\",\"field2\":\"2\",\"field3\":\"3\"}", Pojo.class);
        assertThat(pojoFromJson.field1).isEqualTo("1");
        assertThat(pojoFromJson.field2).isEqualTo("2");
        assertThat(pojoFromJson.field3).isNull();
    }
}
