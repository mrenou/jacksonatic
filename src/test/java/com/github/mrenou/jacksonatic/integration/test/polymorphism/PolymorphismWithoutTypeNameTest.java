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
package com.github.mrenou.jacksonatic.integration.test.polymorphism;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.mrenou.jacksonatic.Jacksonatic.configureMapping;
import static com.github.mrenou.jacksonatic.mapping.ClassMapping.type;
import static org.assertj.core.api.Assertions.assertThat;

public class PolymorphismWithoutTypeNameTest {

    private static boolean captureConstructor = false;

    private static String firstConstructorCalled = "";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void before() {
        firstConstructorCalled = "";
        captureConstructor = false;
    }

    static class PojoParent {

        private String field1;

        PojoParent(String field1) {
            this.field1 = field1;
        }
    }

    static class PojoChild1 extends PojoParent {

        private String field21;

        public PojoChild1(String field1, String field21) {
            super(field1);
            setConstructorCallIfEmpty("public PojoChild1(String field1, String field21)");
            this.field21 = field21;
        }
    }

    static class PojoChild2 extends PojoParent {

        private String field22;

        public PojoChild2(String field1, String field22) {
            super(field1);
            setConstructorCallIfEmpty("public PojoChild2(String field1, String field22)");
            this.field22 = field22;
        }
    }

    @Test
    public void map_one_inherited_field_to_deserialize_child1() throws IOException {
        PojoChild1 expectedPojo = new PojoChild1("field1", "field21");

        configureMappingPolymorphism();

        captureConstructor();
        PojoChild1 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field21\":\"field21\",\"type\":\"CHILD1\"}", PojoChild1.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public PojoChild1(String field1, String field21)");
    }

    @Test
    public void map_one_inherited_field_to_serialize_child1() throws IOException {
        configureMappingPolymorphism();

        String json = objectMapper.writeValueAsString(new PojoChild1("field1", "field21"));

        assertThat(json).isEqualTo("{\"type\":\"CHILD1\",\"field1\":\"field1\",\"field21\":\"field21\"}");
    }

    @Test
    public void map_one_inherited_field_to_deserialize_child2() throws IOException {
        PojoChild2 expectedPojo = new PojoChild2("field1", "field22");

        configureMappingPolymorphism();

        captureConstructor();
        PojoChild2 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field22\":\"field22\",\"type\":\"CHILD2\"}", PojoChild2.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public PojoChild2(String field1, String field22)");
    }

    @Test
    public void map_one_inherited_field_to_serialize_child2() throws IOException {
        configureMappingPolymorphism();

        String json = objectMapper.writeValueAsString(new PojoChild2("field1", "field22"));

        assertThat(json).isEqualTo("{\"type\":\"CHILD2\",\"field1\":\"field1\",\"field22\":\"field22\"}");
    }

    private void configureMappingPolymorphism() {
        configureMapping()
                .on(type(PojoParent.class)
                                .mapAll()
                                .fieldForTypeName("type")
                                .addNamedSubType(PojoChild1.class, "CHILD1")
                                .addNamedSubType(PojoChild2.class, "CHILD2")
                                .withAConstructorOrStaticFactory()
                )
                .on(type(PojoChild1.class)
                                .typeName("CHILD1")
                )
                .on(type(PojoChild2.class)
                                .mapAll()
                                .typeName("CHILD2")
                )
                .registerIn(objectMapper);
    }

    @Test
    public void map_one_inherited_field_to_deserialize_child1_without_define_typename_in_children() throws IOException {
        PojoChild1 expectedPojo = new PojoChild1("field1", "field21");

        configureMappingPolymorphismWithoutChildrenTypeNameMapping();

        captureConstructor();
        PojoChild1 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field21\":\"field21\",\"type\":\"CHILD1\"}", PojoChild1.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public PojoChild1(String field1, String field21)");
    }

    @Test
    public void map_one_inherited_field_to_serialize_child1_without_define_typename_in_children() throws IOException {
        configureMappingPolymorphismWithoutChildrenTypeNameMapping();

        String json = objectMapper.writeValueAsString(new PojoChild1("field1", "field21"));

        assertThat(json).isEqualTo("{\"type\":\"CHILD1\",\"field1\":\"field1\",\"field21\":\"field21\"}");
    }

    @Test
    public void map_one_inherited_field_to_deserialize_child2_without_define_typename_in_children() throws IOException {
        PojoChild2 expectedPojo = new PojoChild2("field1", "field22");

        configureMappingPolymorphismWithoutChildrenTypeNameMapping();

        captureConstructor();
        PojoChild2 pojo = objectMapper.readValue("{\"field1\":\"field1\",\"field22\":\"field22\",\"type\":\"CHILD2\"}", PojoChild2.class);

        assertThat(pojo).isEqualToIgnoringGivenFields(expectedPojo);
        assertThat(firstConstructorCalled).isEqualTo("public PojoChild2(String field1, String field22)");
    }

    @Test
    public void map_one_inherited_field_to_serialize_child2_without_define_typename_in_children() throws IOException {
        configureMappingPolymorphismWithoutChildrenTypeNameMapping();

        String json = objectMapper.writeValueAsString(new PojoChild2("field1", "field22"));

        assertThat(json).isEqualTo("{\"type\":\"CHILD2\",\"field1\":\"field1\",\"field22\":\"field22\"}");
    }

    private void configureMappingPolymorphismWithoutChildrenTypeNameMapping() {
        configureMapping()
                .on(type(PojoParent.class)
                                .mapAll()
                                .fieldForTypeName("type")
                                .addNamedSubType(PojoChild1.class, "CHILD1")
                                .addNamedSubType(PojoChild2.class, "CHILD2")
                                .withAConstructorOrStaticFactory()
                )
                .on(type(PojoChild1.class))
                .on(type(PojoChild2.class).mapAll())
                .registerIn(objectMapper);
    }

    private void captureConstructor() {
        captureConstructor = true;
    }

    private static void setConstructorCallIfEmpty(String constructor) {
        if (firstConstructorCalled.equals("") && captureConstructor) {
            firstConstructorCalled = constructor;
        }
    }

}