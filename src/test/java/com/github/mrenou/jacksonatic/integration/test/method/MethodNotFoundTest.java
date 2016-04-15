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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mrenou.jacksonatic.exception.FieldNotFoundException;
import com.github.mrenou.jacksonatic.exception.MethodNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static com.github.mrenou.jacksonatic.Jacksonatic.configureMapping;
import static com.github.mrenou.jacksonatic.JacksonaticOptions.options;
import static com.github.mrenou.jacksonatic.mapping.ClassMapping.type;
import static com.github.mrenou.jacksonatic.mapping.FieldMapping.field;
import static com.github.mrenou.jacksonatic.mapping.MethodMapping.method;
import static org.assertj.core.api.Assertions.assertThat;

public class MethodNotFoundTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final Pojo POJO = new Pojo();

    private final ObjectMapper objectMapper = new ObjectMapper();

    static class Pojo {

        private String field1;

    }

    @Test(expected = MethodNotFoundException.class)
    public void unknown_method() throws JsonProcessingException {
        configureMapping()
                .on(type(Pojo.class)
                        .on(method("getField1")))
                .registerIn(objectMapper);
    }

    @Test
    public void unknown_method_when_type_checking_is_off() throws JsonProcessingException {
        configureMapping(options().disableTypeChecking())
                .on(type(Pojo.class)
                        .on(method("getField1")))
                .registerIn(objectMapper);
    }

}