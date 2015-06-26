package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;

public class SerializerBuilderTest {

    public static final Pojo POJO = new Pojo("field1", 42);

    @Test
    public void map_without_fields() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SerializerBuilder.on(Pojo.class).registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        Assertions.assertThat(json).isEqualTo("{}");

    }

    @Test
    public void map_one_basic_field() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SerializerBuilder.on(Pojo.class)
                .map("field1")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        Assertions.assertThat(json).isEqualTo("{\"field1\":\"field1\"}");

    }

    @Test
    public void map_two_basic_fields() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SerializerBuilder.on(Pojo.class)
                .map("field1")
                .map("field2")
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        Assertions.assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_all_basic_fields() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        SerializerBuilder.on(Pojo.class)
                .all()
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        Assertions.assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void deserialize() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SerializerBuilder.on(Pojo.class)
                .all()
                .onConstructor(new TypedParameter<>(String.class, "field1"), new TypedParameter<>(Integer.class, "field2"))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        Assertions.assertThat(pojo).isEqualTo(POJO);
    }
}