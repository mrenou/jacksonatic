package org.jacksonatic;

public class ParameterMatcher {

    private Class<?> parameterClass;

    private String jsonProperty;

    private String fieldProperty;

    public static ParameterMatcher match(Class<?> parameterClass, String jsonProperty) {
        return new ParameterMatcher(parameterClass, jsonProperty, null);
    }

    public static ParameterMatcher match(String fieldProperty, String jsonProperty) {
        return new ParameterMatcher(null, jsonProperty, fieldProperty);
    }

    public static ParameterMatcher matchType(Class<?> parameterClass) {
        return new ParameterMatcher(parameterClass, null, null);
    }

    public static ParameterMatcher matchField(String fieldProperty) {
        return new ParameterMatcher(null, null, fieldProperty);
    }

    public ParameterMatcher mappedBy(String jsonProperty) {
        this.jsonProperty = jsonProperty;
        return this;
    }

    public ParameterMatcher(Class<?> parameterClass, String jsonProperty, String fieldProperty) {
        this.parameterClass = parameterClass;
        this.jsonProperty = jsonProperty;
        this.fieldProperty = fieldProperty;
    }

    public String getJsonProperty() {
        return jsonProperty;
    }

    public String getFieldProperty() {
        return fieldProperty;
    }

    public Class<?> getParameterClass() {
        return parameterClass;
    }

    @Override
    public String toString() {
        return "ParameterMatcher{" +
                "parameterClass=" + parameterClass +
                ", jsonProperty='" + jsonProperty + '\'' +
                ", fieldProperty='" + fieldProperty + '\'' +
                '}';
    }
}
