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
package org.jacksonatic.integration.test.method;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.exception.MethodNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;
import static org.jacksonatic.mapping.MethodMapping.method;

public class MethodNotFoundTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final Pojo POJO = new Pojo();

    private final ObjectMapper objectMapper = new ObjectMapper();

    static class Pojo {

        private String field1;

    }

    @Test(expected = MethodNotFoundException.class)
    public void unknown_field_on_serialization() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .on(method("getField1")))
                .registerIn(objectMapper);

        objectMapper.writeValueAsString(POJO);
    }

    @Test(expected = MethodNotFoundException.class)
    public void unknown_field_on_deserialization() throws IOException {
        Pojo expectedPojo = new Pojo();
        configureMapping()
                .on(type(Pojo.class)
                        .on(method("getField1")))
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\"}", Pojo.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
    }

}