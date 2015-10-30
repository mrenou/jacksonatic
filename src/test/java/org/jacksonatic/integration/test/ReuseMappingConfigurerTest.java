package org.jacksonatic.integration.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jacksonatic.MappingConfigurer;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.jacksonatic.ClassMappingConfigurer.type;

public class ReuseMappingConfigurerTest {

    static class Pojo {

        public final String field1;

        public final Integer field2;


        Pojo(String field1, Integer field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    private final MappingConfigurer baseMappingConfigurer = MappingConfigurer.configureMapping()
            .on(type(Pojo.class)
                    .map("field1", "toto1"));

    @Test
    public void copy_mapping_configurer_to_create_tow_new() throws IOException {
        Pojo expectedPojo = new Pojo("field1", 42);

        ObjectMapper objectMapper1 = new ObjectMapper();
        baseMappingConfigurer.copy()
                .on(type(Pojo.class)
                        .map("field2", "toto"))
                .registerIn(objectMapper1);

        ObjectMapper objectMapper2 = new ObjectMapper();
        baseMappingConfigurer.copy()
                .on(type(Pojo.class)
                        .map("field2", "titi"))
                .registerIn(objectMapper2);

        String result1 = objectMapper1.writeValueAsString(expectedPojo);
        String result2 = objectMapper2.writeValueAsString(expectedPojo);

        assertThat(result1).isEqualTo("{\"toto1\":\"field1\",\"toto\":42}");
        assertThat(result2).isEqualTo("{\"toto1\":\"field1\",\"titi\":42}");
    }
}
