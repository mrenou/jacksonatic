/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic.integration.test.deserialization;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.mapping.ParameterCriteria;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;
import static org.jacksonatic.mapping.ParameterCriteria.matchField;
import static org.jacksonatic.mapping.ParameterCriteria.matchType;

public class DeserializationOnStaticFactoryest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static boolean captureConstructor = false;

    public static String firstConstructorCalled = "";

    @Before
    public void before() {
        firstConstructorCalled = "";
        captureConstructor = false;
    }

    public static class Pojo {

        private String field1;

        private Integer field2;

        public Pojo(String field1, Integer field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public static Pojo newPojo(String field1, Integer field2) {
            setConstructorCallIfEmpty("public static Pojo newPojo(String field1, Integer field2)");
            return new Pojo(field1, field2);
        }

    }

    @Test
    public void deserialize_on_static_factory_with_classes_and_json_properties() throws IOException {

        configureMapping()
                .on(type(Pojo.class)
                        .onStaticFactory(matchType(String.class).mappedBy("field1"), matchType(Integer.class).mappedBy("field2")))
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(new Pojo("field1", 42));
        assertThat(firstConstructorCalled).isEqualTo("public static Pojo newPojo(String field1, Integer field2)");
    }

    @Test
    public void deserialize_on_static_factory_with_classes() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .onStaticFactory(matchType(String.class), matchType(Integer.class)))
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(new Pojo("field1", 42));
        assertThat(firstConstructorCalled).isEqualTo("public static Pojo newPojo(String field1, Integer field2)");
    }

    @Test
    public void deserialize_on_static_factory_with_fields_and_json_properties() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .onStaticFactory(matchField("field1").mappedBy("toto"), matchField("field2").mappedBy("tata")))
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"toto\":\"field1\",\"tata\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(new Pojo("field1", 42));
        assertThat(firstConstructorCalled).isEqualTo("public static Pojo newPojo(String field1, Integer field2)");
    }

    @Test(expected = JsonMappingException.class)
    public void not_deserialize_on_named_static_factory() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .onStaticFactory("other", ParameterCriteria.match(String.class, "field1"), ParameterCriteria.match(Integer.class, "field2")))
                .registerIn(objectMapper);

        objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);
    }

    @Test
    public void deserialize_on_unnamed_static_factory() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .onStaticFactory(ParameterCriteria.match(String.class, "field1"), ParameterCriteria.match(Integer.class, "field2")))
                .registerIn(objectMapper);

        captureConstructor();
        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(new Pojo("field1", 42));
        assertThat(firstConstructorCalled).isEqualTo("public static Pojo newPojo(String field1, Integer field2)");
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
                        .mapAll()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo2 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo2.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public static Pojo2 newPojo");
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
                        .mapAll()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        captureConstructor();
        Pojo4 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo4.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public static Pojo4 newPojo");
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