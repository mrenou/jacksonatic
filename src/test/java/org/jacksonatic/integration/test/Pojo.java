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
        return Objects.equals(field1, pojo.field1)
                && Objects.equals(field2, pojo.field2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field1, field2);
    }
}
