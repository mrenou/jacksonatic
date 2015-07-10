package org.jacksonatic.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.MappingConfigurer;
import org.junit.Test;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.assertj.core.api.Assertions.assertThat;

public class IgnoreFieldTest {

    public static final Pojo POJO = new Pojo("field1", 42);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void ignore_one_field() throws JsonProcessingException {
        MappingConfigurer.on(Pojo.class)
                .all()
                .ignore("field1")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field2\":42}");
    }

    @Test
    public void ignore_all_fields() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        MappingConfigurer.on(Pojo.class)
                .all()
                .ignore("field1")
                .ignore("field2")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{}");
    }

    @Test
    public void ignore_and_map_field() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        MappingConfigurer.on(Pojo.class)
                .map("field1")
                .ignore("field1")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{}");
    }


}