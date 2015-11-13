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

import java.util.Objects;

public class Pojo {

    private String field1;

    private Integer field2;

    public Pojo(String field1, Integer field2) {
        this.field1 = field1;
        this.field2 = field2;
    }

    public static Pojo newPojo(String field1, Integer field2) {
        return new Pojo(field1, field2);
    }

    public String getField1() {
        return field1;
    }

    public Integer getField2() {
        return field2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pojo pojo = (Pojo) o;
        return Objects.equals(field1, pojo.field1) &&
                Objects.equals(field2, pojo.field2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field1, field2);
    }
}
