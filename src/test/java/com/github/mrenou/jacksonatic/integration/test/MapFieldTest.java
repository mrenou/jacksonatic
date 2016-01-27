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
package com.github.mrenou.jacksonatic.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static com.github.mrenou.jacksonatic.Jacksonatic.configureMapping;
import static com.github.mrenou.jacksonatic.mapping.ClassMapping.type;
import static org.assertj.core.api.Assertions.assertThat;

public class MapFieldTest {

    static class Pojo {

        private String field1;

        private Integer field2;

        public Pojo() {
        }

        public Pojo(String field1, Integer field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public Integer getField2() {
            return field2;
        }

        public void setField2(Integer field2) {
            this.field2 = field2;
        }

    }

    private static final Pojo POJO = new Pojo("field1", 42);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void map_two_basic_fields_to_serialize() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .map("field1")
                        .map("field2"))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_two_basic_fields_to_deserialize() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .map("field1")
                        .map("field2"))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(POJO);
    }

    @Test
    public void map_all_basic_fields_to_serialize() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .mapAll())
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_all_basic_fields_to_deserialize() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .mapAll())
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(POJO);
    }

    @Test
    public void map_with_another_name_to_serialize() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .map("field1", "toto")
                        .map("field2"))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field2\":42,\"toto\":\"field1\"}");
    }

    @Test
    public void map_with_another_name_to_deserialize() throws IOException {
        Pojo expectedPojo = new Pojo("field1", 42);
        configureMapping()
                .on(type(Pojo.class)
                        .map("field1", "toto")
                        .map("field2"))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"toto\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
    }

}