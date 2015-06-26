package test;


public class TypedParameter<T> {

    private Class<T> clazz;

    private String name;

    public TypedParameter(Class<T> clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    public Class<T> getType() {
        return clazz;
    }

    public String getName() {
        return name;
    }
}
