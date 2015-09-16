package org.jacksonatic.integration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;

public class DefaultMappingTest {

    public static String firstConstructorCalled = "";

    static class Pojo {

        private String field1;

        private Integer field2;

        public Pojo(String field1) {
            setConstructorCallIfEmpty("public Pojo(String field1)");
            this.field1 = field1;
        }

        public Pojo(String field1, Integer field2) {
            setConstructorCallIfEmpty("public Pojo(String field1, Integer field2)");
            this.field1 = field1;
            this.field2 = field2;
        }

        public static Pojo newPojo(String field1, Integer field2) {
            setConstructorCallIfEmpty("public static Pojo newPojo(String field1, Integer field2)");
            return new Pojo(field1, field2);
        }

        public String getField1() {
            return field1;
        }

        public Integer getField2() {
            return field2;
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static final Pojo POJO = new Pojo("field1", 42);

    @Test
    public void find_constructor_by_default() throws IOException {
        configureMapping()
                .forEach((clazz -> type(clazz).all().withAConstructorOrStaticFactory()))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(POJO);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    private static void setConstructorCallIfEmpty(String constructor) {
        if (firstConstructorCalled.equals("")) {
            firstConstructorCalled = constructor;
        }
    }

    @Test
    public void map_all_by_default() throws IOException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .forEach((clazz -> type(clazz).all()))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_none_by_default() throws IOException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .forEach((clazz -> type(clazz)))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{}");
    }

    @Test
    public void map_no_default() throws IOException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }
}