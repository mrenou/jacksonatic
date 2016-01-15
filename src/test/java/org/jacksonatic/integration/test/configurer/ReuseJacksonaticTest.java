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
import org.jacksonatic.Jacksonatic;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.jacksonatic.mapping.ClassMapping.type;

public class ReuseJacksonaticTest {

    static class Pojo {

        public final String field1;

        public final Integer field2;


        Pojo(String field1, Integer field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    private final Jacksonatic baseJacksonatic = Jacksonatic.configureMapping()
            .on(type(Pojo.class)
                    .map("field1", "toto1")
                    .map("field2", "tutu"));

    @Test
    public void copy_mapping_configurer_to_create_tow_new() throws IOException {
        Pojo expectedPojo = new Pojo("field1", 42);

        ObjectMapper objectMapper1 = new ObjectMapper();
        baseJacksonatic.copy()
                .on(type(Pojo.class)
                        .map("field2", "toto"))
                .registerIn(objectMapper1);

        ObjectMapper objectMapper2 = new ObjectMapper();
        baseJacksonatic.copy()
                .on(type(Pojo.class)
                        .map("field2", "titi"))
                .registerIn(objectMapper2);

        String result1 = objectMapper1.writeValueAsString(expectedPojo);
        String result2 = objectMapper2.writeValueAsString(expectedPojo);

        assertThat(result1).isEqualTo("{\"toto1\":\"field1\",\"toto\":42}");
        assertThat(result2).isEqualTo("{\"toto1\":\"field1\",\"titi\":42}");
    }
}
