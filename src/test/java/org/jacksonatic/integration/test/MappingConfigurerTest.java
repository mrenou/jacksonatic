package org.jacksonatic.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.MappingConfigurer;
import org.junit.Test;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.assertj.core.api.Assertions.assertThat;

public class MappingConfigurerTest {

    public static final Pojo POJO = new Pojo("field1", 42);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void map_without_fields() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        MappingConfigurer.on(Pojo.class).registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{}");
    }

    @Test
    public void map_one_basic_field() throws JsonProcessingException {
        MappingConfigurer.on(Pojo.class)
                .map("field1")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\"}");

    }

    @Test
    public void map_two_basic_fields() throws JsonProcessingException {
        MappingConfigurer.on(Pojo.class)
                .map("field1")
                .map("field2")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_all_basic_fields() throws JsonProcessingException {
        MappingConfigurer.on(Pojo.class)
                .all()
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_one_basic_with_another_name() throws JsonProcessingException {
        MappingConfigurer.on(Pojo.class)
                .map("field1", "toto")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"toto\":\"field1\"}");

    }

}