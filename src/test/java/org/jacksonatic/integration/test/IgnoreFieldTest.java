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
package org.jacksonatic.integration.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;

public class IgnoreFieldTest {

    public static final Pojo POJO = new Pojo("field1", 42);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void ignore_one_field() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .mapAll()
                        .ignore("field1"))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field2\":42}");
    }

    @Test
    public void ignore_all_fields() throws JsonProcessingException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .on(type(Pojo.class)
                        .mapAll()
                        .ignore("field1")
                        .ignore("field2"))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{}");
    }

}