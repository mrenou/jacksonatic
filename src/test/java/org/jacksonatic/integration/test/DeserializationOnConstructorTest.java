package org.jacksonatic.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;
import static org.jacksonatic.mapping.ParameterCriteria.matchField;
import static org.jacksonatic.mapping.ParameterCriteria.matchType;

public class DeserializationOnConstructorTest {

    public static boolean captureConstructor = false;

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

    }

    public static final Pojo POJO = new Pojo("field1", 42);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before() {
        firstConstructorCalled = "";
        captureConstructor = false;
    }

    @Test
    public void deserialize_on_constructor_with_classes_and_json_properties() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .all()
                        .withConstructor(matchType(String.class).mappedBy("field1"), matchType(Integer.class).mappedBy("field2")))
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(POJO);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    @Test
    public void deserialize_on_constructor_with_classes() throws IOException {
        configureMapping()
                .on(type(Pojo.class).all().withConstructor(matchType(String.class), matchType(Integer.class)))
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(POJO);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    @Test
    public void deserialize_on_constructor_with_fields_and_json_properties() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .all()
                        .withConstructor(matchField("field1").mappedBy("toto"), matchField("field2").mappedBy("tata")))
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"toto\":\"field1\",\"tata\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(POJO);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    @Test
    public void find_a_constructor_with_exact_fields_to_deserialize() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .all()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(POJO);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    @Test
    public void find_a_constructor_which_starts_exact_fields_to_deserialize() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .all()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(POJO);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    static class Pojo2 {

        private String field1;

        private Integer field2;

        private Pojo2(String field1, Integer field2) {
            setConstructorCallIfEmpty("private Pojo2(String field1, Integer field2)");
            this.field1 = field1;
            this.field2 = field2;
        }

        public static Pojo2 newFakePojo(Integer other, Integer field2) {
            setConstructorCallIfEmpty("public static Pojo2 newFakePojo(Integer other, Integer field2)");
            return null;
        }

        public static Pojo2 newPojoLight(String field1) {
            setConstructorCallIfEmpty("public static Pojo2 newPojoLight");
            return new Pojo2(field1, null);
        }

        public static Pojo2 newPojo(String field1, Integer field2) {
            setConstructorCallIfEmpty("public static Pojo2 newPojo");
            return new Pojo2(field1, field2);
        }
    }

    @Test
    public void find_a_static_factory_which_starts_exact_fields_to_deserialize() throws IOException {
        Pojo2 expectedPojo = Pojo2.newPojo("field1", 42);
        configureMapping()
                .on(type(Pojo2.class)
                        .all()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo2 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo2.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public static Pojo2 newPojo");
    }


    static class Pojo3 {

        public static Integer staticToIgnore = 42;

        private String field1;

        private Integer field2;

        private String field3;

        public Pojo3(Integer other, Integer field2) {

        }

        private Pojo3(String field1, Integer field2) {
            setConstructorCallIfEmpty("public Pojo3");
            this.field1 = field1;
            this.field2 = field2;
        }

        public static Pojo3 newPojo(String field1, Integer field2) {
            setConstructorCallIfEmpty("public static Pojo3 newPojo");
            return new Pojo3(field1, field2);
        }

    }

    @Test
    public void find_a_constructor_which_starts_same_fields_to_deserialize_avoiding_static() throws IOException {
        Pojo3 expectedPojo = new Pojo3("field1", 42);
        configureMapping()
                .on(type(Pojo3.class)
                        .map("field1")
                        .map("field2")
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        captureConstructor();
        Pojo3 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42,\"field3\":\"field3\"}", Pojo3.class);

        assertThat(pojo).isEqualToComparingFieldByField(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public static Pojo3 newPojo");
    }

    static class Pojo4 {

        private String field1;

        private Integer field2;

        private String field3;

        private Pojo4(String field1, Integer field2) {
            setConstructorCallIfEmpty("private Pojo4");
            this.field1 = field1;
            this.field2 = field2;
        }

        public static Pojo4 newFakePojo(Integer other, Integer field2) {
            setConstructorCallIfEmpty("public static Pojo4 newFakePojo");
            return null;
        }

        public static Pojo4 newPojo(String field1, Integer field2) {
            setConstructorCallIfEmpty("public static Pojo4 newPojo");
            final Pojo4 pojo = new Pojo4(field1, field2);
            return pojo;
        }
    }

    @Test
    public void find_a_static_factory_which_starts_same_fields_to_deserialize() throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Pojo4 expectedPojo = Pojo4.newPojo("field1", 42);
        configureMapping()
                .on(type(Pojo4.class)
                        .all()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo4 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo4.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public static Pojo4 newPojo");
    }

    static class Pojo5 {

        public String field1;

    }

    @Test
    public void find_default_constructor_to_deserialize() throws IOException {
        Pojo5 expectedPojo = new Pojo5();
        expectedPojo.field1 = "field1";
        configureMapping()
                .mapAllOn(type(Pojo5.class)
                        .all()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        captureConstructor();
        Pojo5 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42,\"field3\":\"field3\"}", Pojo5.class);

        assertThat(pojo).isEqualToComparingFieldByField(expectedPojo);
    }

    static class PojoParent {

        private String field1;

         PojoParent(String field1) {
            this.field1 = field1;
        }
    }

    static class PojoChild extends PojoParent {

        private String field2;

        public PojoChild(String field1, String field2) {
            super(field1);
            this.field2 = field2;
        }
    }

    @Test
    public void find_with_inheritance() throws IOException {
        PojoChild expectedPojo = new PojoChild("field1", "field2");
        configureMapping()
                .on(type(PojoChild.class)
                        .all()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        PojoChild pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":\"field2\"}", PojoChild.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
    }


    private void captureConstructor() {
        captureConstructor = true;
    }

    private static void setConstructorCallIfEmpty(String constructor) {
        if (firstConstructorCalled.equals("") && captureConstructor) {
            firstConstructorCalled = constructor;
        }
    }

}