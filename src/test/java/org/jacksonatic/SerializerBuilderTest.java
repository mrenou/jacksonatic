package org.jacksonatic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.SerializerBuilder;
import org.jacksonatic.TypedParameter;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.assertj.core.api.Assertions.assertThat;

public class SerializerBuilderTest {

    public static final Pojo POJO = new Pojo("field1", 42);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void map_without_fields() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        SerializerBuilder.on(Pojo.class).registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{}");
    }

    @Test
    public void map_one_basic_field() throws JsonProcessingException {
        SerializerBuilder.on(Pojo.class)
                .map("field1")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\"}");

    }

    @Test
    public void map_two_basic_fields() throws JsonProcessingException {
        SerializerBuilder.on(Pojo.class)
                .map("field1")
                .map("field2")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_all_basic_fields() throws JsonProcessingException {
        SerializerBuilder.on(Pojo.class)
                .all()
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void deserialize_on_constructor() throws IOException {
        SerializerBuilder.on(Pojo.class)
                .onConstructor(new TypedParameter<>(String.class, "field1"), new TypedParameter<>(Integer.class, "field2"))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

    @Test
    public void deserialize_on_named_static_factory() throws IOException {
        SerializerBuilder.on(Pojo.class)
                .onStaticFactory("newPojo", new TypedParameter<>(String.class, "field1"), new TypedParameter<>(Integer.class, "field2"))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

    @Test(expected = JsonMappingException.class)
    public void not_deserialize_on_named_static_factory() throws IOException {
        SerializerBuilder.on(Pojo.class)
                .onStaticFactory("other", new TypedParameter<>(String.class, "field1"), new TypedParameter<>(Integer.class, "field2"))
                .registerIn(objectMapper);

        objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);
    }

    @Test
    public void deserialize_on_unnamed_static_factory() throws IOException {
        SerializerBuilder.on(Pojo.class)
                .onStaticFactory(new TypedParameter<>(String.class, "field1"), new TypedParameter<>(Integer.class, "field2"))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualTo(POJO);
    }

}