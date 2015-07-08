package org.jacksonatic.integration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.MappingConfigurer;
import org.jacksonatic.mapping.ParameterMatcher;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.mapping.ParameterMatcher.matchField;

public class DeserializationOnConstructorTest {

    public static final Pojo POJO = new Pojo("field1", 42);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserialize_on_constructor_with_classes_and_json_properties() throws IOException {
        MappingConfigurer.on(Pojo.class)
                .onConstructor(ParameterMatcher.matchType(String.class).mappedBy("field1"), ParameterMatcher.matchType(Integer.class).mappedBy("field2"))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

    @Test
    public void deserialize_on_constructor_with_classes() throws IOException {
        MappingConfigurer.on(Pojo.class)
                .onConstructor(ParameterMatcher.matchType(String.class), ParameterMatcher.matchType(Integer.class))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

    @Test
    public void deserialize_on_constructor_with_fields_and_json_properties() throws IOException {
        MappingConfigurer.on(Pojo.class)
                .onConstructor(matchField("field1").mappedBy("toto"), matchField("field2").mappedBy("tata"))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"toto\":\"field1\",\"tata\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

}