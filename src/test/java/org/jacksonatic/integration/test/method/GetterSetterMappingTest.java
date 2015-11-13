/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic.integration.test.method;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;

public class GetterSetterMappingTest {

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
    public void map_one_getter_to_serialize() throws IOException {
        Pojo pojo = new Pojo("field1", 42);

        configureMapping()
                .on(type(Pojo.class)
                        .mapGetter("field1"))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(pojo);

        assertThat(json).isEqualTo("{\"field1\":\"field1\"}");
    }

    @Test
    public void map_one_setter_ignoring_param_types_to_deserialize() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .mapSetter("field1"))
                .registerIn(objectMapper);

        Pojo pojo2 = objectMapper.readValue("{\"field1\":\"field1\"}", Pojo.class);
        assertThat(pojo2).isEqualToIgnoringGivenFields(new Pojo("field1", null));
    }

    @Test
    public void map_one_setter_to_deserialize() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .mapSetter("field1", String.class))
                .registerIn(objectMapper);

        Pojo pojo2 = objectMapper.readValue("{\"field1\":\"field1\"}", Pojo.class);
        assertThat(pojo2).isEqualToIgnoringGivenFields(new Pojo("field1", null));
    }

}