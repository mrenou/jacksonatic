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

import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;
import static org.jacksonatic.MappingConfigurer.configureMapping;

public class ConfigureMultipleClassesTest {

    static class Pojo1 {

        public String field1;

        public String field2;

        Pojo1() {
        }

        Pojo1(String field1, String field2) {
            this.field1 = field1;
            this.field2 = field2;
        }

    }

    static class Pojo2 {

        public Integer field3;

        public String field4;

        Pojo2() {
        }

        Pojo2(Integer field3, String field4) {
            this.field3 = field3;
            this.field4 = field4;
        }

    }

    static class Pojo3 {

        public boolean field5;

        public String field6;

        Pojo3() {
        }

        Pojo3(boolean field5, String field6) {
            this.field5 = field5;
            this.field6 = field6;
        }

    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void map_ignore_on_several_classes() throws JsonProcessingException {
        configureMapping()
                .mapAllOn(type(Pojo1.class)
                        .ignore("field2"))
                .on(type(Pojo2.class)
                        .map("field4"))
                .on(type(Pojo3.class)
                        .map("field5")
                        .map("field6"))
                .registerIn(objectMapper);

        String jsonPojo1 = objectMapper.writeValueAsString(new Pojo1("1", "2"));
        String jsonPojo2 = objectMapper.writeValueAsString(new Pojo2(3, "4"));
        String jsonPojo3 = objectMapper.writeValueAsString(new Pojo3(true, "6"));

        assertThat(jsonPojo1).isEqualTo("{\"field1\":\"1\"}");
        assertThat(jsonPojo2).isEqualTo("{\"field4\":\"4\"}");
        assertThat(jsonPojo3).isEqualTo("{\"field5\":true,\"field6\":\"6\"}");
    }

}