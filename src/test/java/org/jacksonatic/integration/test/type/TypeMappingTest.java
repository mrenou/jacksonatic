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
package org.jacksonatic.integration.test.type;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.annotation.JacksonaticJsonIgnoreProperties.jsonIgnoreProperties;
import static org.jacksonatic.mapping.ClassMapping.type;


public class TypeMappingTest {

    static class Pojo {

        private String field1;

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void add_type_annotation() throws IOException {
        configureMapping()
                .on(type(Pojo.class)
                        .mapAll()
                        .add(jsonIgnoreProperties().ignoreUnknown(true)))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"pouet\": \"boom\", \"field1\": \"field1\"}", Pojo.class);

        assertThat(pojo.getField1()).isEqualTo("field1");
    }

}