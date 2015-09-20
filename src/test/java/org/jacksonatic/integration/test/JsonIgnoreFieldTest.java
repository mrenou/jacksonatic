package org.jacksonatic.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.annotation.JacksonaticJsonProperty;
import org.junit.Test;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;
import static org.jacksonatic.annotation.JacksonaticJsonIgnore.jsonIgnore;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;
import static org.jacksonatic.mapping.PropertyMapping.property;

public class JsonIgnoreFieldTest {

    public static final Pojo POJO = new Pojo("field1", 42);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void ignore_one_field() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .all()
                        .on(property("field1").add(jsonIgnore())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field2\":42}");
    }

    @Test
    public void ignore_all_fields() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .on(type(Pojo.class)
                        .all()
                        .on(property("field1").add(jsonIgnore()))
                        .on(property("field2").add(jsonIgnore())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{}");
    }

    @Test
    public void ignore_and_map_field() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .on(type(Pojo.class)
                        .on(property("field1")
                                .add(jsonProperty())
                                .add(jsonIgnore())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\"}");
    }


}