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
package org.jacksonatic.integration.test.configurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_EMPTY_BEANS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.mapping.ClassMapping.type;
import static org.jacksonatic.Jacksonatic.configureMapping;

public class DefaultMappingTest {

    private static String firstConstructorCalled = "";

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

        public String getField1() {
            return field1;
        }

        public Integer getField2() {
            return field2;
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Pojo POJO = new Pojo("field1", 42);

    @Test
    public void find_constructor_by_default() throws IOException {
        configureMapping()
                .on(type(Object.class)
                        .mapAll()
                        .withAConstructorOrStaticFactory())
                .registerIn(objectMapper);

        Pojo pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field2\":42}", Pojo.class);

        assertThat(pojo).isEqualToComparingFieldByField(POJO);
        assertThat(firstConstructorCalled).isEqualTo("public Pojo(String field1, Integer field2)");
    }

    private static void setConstructorCallIfEmpty(String constructor) {
        if (firstConstructorCalled.equals("")) {
            firstConstructorCalled = constructor;
        }
    }

    @Test
    public void map_all_by_default() throws IOException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .on(type(Object.class)
                        .mapAll())
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }

    @Test
    public void map_none_by_default() throws IOException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .on(type(Object.class))
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{}");
    }

    @Test
    public void map_no_default() throws IOException {
        objectMapper.disable(FAIL_ON_EMPTY_BEANS);
        configureMapping()
                .registerIn(objectMapper);

        String json = objectMapper.writeValueAsString(POJO);

        assertThat(json).isEqualTo("{\"field1\":\"field1\",\"field2\":42}");
    }
}