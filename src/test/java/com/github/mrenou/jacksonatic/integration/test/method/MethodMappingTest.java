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
package com.github.mrenou.jacksonatic.integration.test.method;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mrenou.jacksonatic.integration.test.CustomStringSerializer;
import org.junit.Test;

import java.io.IOException;

import static com.github.mrenou.jacksonatic.Jacksonatic.configureMapping;
import static com.github.mrenou.jacksonatic.annotation.JacksonaticJsonIgnore.jsonIgnore;
import static com.github.mrenou.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;
import static com.github.mrenou.jacksonatic.annotation.JacksonaticJsonSerialize.jsonSerialize;
import static com.github.mrenou.jacksonatic.mapping.ClassMapping.type;
import static com.github.mrenou.jacksonatic.mapping.FieldMapping.field;
import static com.github.mrenou.jacksonatic.mapping.MethodMapping.method;
import static org.assertj.core.api.Assertions.assertThat;

public class MethodMappingTest {

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void map_one_method_to_serialize() throws IOException {
        Pojo pojo = new Pojo("field1", 42);

        configureMapping()
                .on(type(Pojo.class).on(method("getField1").add(jsonProperty())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(pojo);

        assertThat(json).isEqualTo("{\"field1\":\"field1\"}");
    }

    @Test
    public void map_one_method_to_disable_deserialize() throws IOException {
        Pojo pojo = new Pojo("field1", 42);

        configureMapping()
                .on(type(Pojo.class)
                        .on(field("field1").add(jsonIgnore()))
                        .on(method("getField1").add(jsonProperty()))
                        .on(method("setField1", String.class).add(jsonIgnore())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(pojo);
        assertThat(json).isEqualTo("{\"field1\":\"field1\"}");

        Pojo pojo2 = objectMapper.readValue("{\"field1\":\"field1\"}", Pojo.class);
        assertThat(pojo2).isEqualToIgnoringGivenFields(new Pojo(null, null));
    }

    @Test
    public void map_tow_methods_to_serialize() throws IOException {
        Pojo pojo = new Pojo("field1", 42);

        configureMapping()
                .on(type(Pojo.class).on(method("getField1").add(jsonProperty())))
                .on(type(Pojo.class).on(method("getField2").add(jsonProperty())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(pojo);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_with_map_alias_and_on_method_annotation_adder() throws IOException {
        Pojo pojo = new Pojo("field1", 42);

        configureMapping()
                .on(type(Pojo.class)
                        .mapGetter("field1", "toto")
                        .on(method("getField1").add(jsonSerialize().using(CustomStringSerializer.class)))
                )
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(pojo);

        assertThat(json).isEqualTo("{\"toto\":\"field1_customized\"}");
    }
}