package org.jacksonatic.integration.test;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.mapping.ParameterMatcher;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.on;
import static org.jacksonatic.MappingConfigurer.configureMapping;
import static org.jacksonatic.mapping.ParameterMatcher.matchField;
import static org.jacksonatic.mapping.ParameterMatcher.matchType;

public class DeserializationOnStaticFactoryest {

    public static final Pojo POJO = new Pojo("field1", 42);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void deserialize_on_static_factory_with_classes_and_json_properties() throws IOException {
        configureMapping()
                .config(on(Pojo.class)
                        .onStaticFactory(matchType(String.class).mappedBy("field1"), matchType(Integer.class).mappedBy("field2")))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

    @Test
    public void deserialize_on_static_factory_with_classes() throws IOException {
        configureMapping()
                .config(on(Pojo.class)
                        .onStaticFactory(matchType(String.class), matchType(Integer.class)))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

    @Test
    public void deserialize_on_static_factory_with_fields_and_json_properties() throws IOException {
        configureMapping()
                .config(on(Pojo.class)
                        .onStaticFactory(matchField("field1").mappedBy("toto"), matchField("field2").mappedBy("tata")))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"toto\":\"field1\",\"tata\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

    @Test(expected = JsonMappingException.class)
    public void not_deserialize_on_named_static_factory() throws IOException {
        configureMapping()
                .config(on(Pojo.class)
                        .onStaticFactory("other", ParameterMatcher.match(String.class, "field1"), ParameterMatcher.match(Integer.class, "field2")))
                .registerIn(objectMapper);

        objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);
    }

    @Test
    public void deserialize_on_unnamed_static_factory() throws IOException {
        configureMapping()
                .config(on(Pojo.class)
                        .onStaticFactory(ParameterMatcher.match(String.class, "field1"), ParameterMatcher.match(Integer.class, "field2")))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

}