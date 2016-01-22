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
package org.jacksonatic.integration.test.field;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.annotation.JacksonaticJsonIgnore.jsonIgnore;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;
import static org.jacksonatic.mapping.ClassMapping.type;
import static org.jacksonatic.mapping.FieldMapping.field;

public class FieldIgnoreTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static class Pojo {

        private String field1;

        private Integer field2;

        public Pojo(String field1, Integer field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    @Test
    public void ignore_one_field() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .mapAll()
                        .on(field("field1").add(jsonIgnore())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(new Pojo("field1", 42));

        assertThat(json).isEqualTo("{\"field2\":42}");
    }

    @Test
    public void ignore_all_fields() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .on(type(Pojo.class)
                        .mapAll()
                        .on(field("field1").add(jsonIgnore()))
                        .on(field("field2").add(jsonIgnore())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(new Pojo("field1", 42));

        assertThat(json).isEqualTo("{}");
    }

    @Test
    public void ignore_and_map_field() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .on(type(Pojo.class)
                        .on(field("field1")
                                .add(jsonProperty())
                                .add(jsonIgnore())))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(new Pojo("field1", 42));

        assertThat(json).isEqualTo("{}");
    }


}