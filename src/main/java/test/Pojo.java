package test;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by morgan on 22/06/15.
 */
public class Pojo {

    private String field1;

    private Integer field2;

    public Pojo(String field1, Integer field2) {
        this.field1 = field1;
        this.field2 = field2;
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

        if (!field1.equals(pojo.field1)) return false;
        if (!field2.equals(pojo.field2)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = field1.hashCode();
        result = 31 * result + field2.hashCode();
        return result;
    }
}
