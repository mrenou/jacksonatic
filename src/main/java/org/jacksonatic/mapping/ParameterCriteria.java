package org.jacksonatic.mapping;

/**
 * Criteria to match a parameter by class or field name class
 */
public class ParameterCriteria {

    private Class<?> parameterClass;

    private String jsonProperty;

    private String fieldProperty;

    public static ParameterCriteria match(Class<?> parameterClass, String jsonProperty) {
        return new ParameterCriteria(parameterClass, jsonProperty, null);
    }

    public static ParameterCriteria match(String fieldProperty, String jsonProperty) {
        return new ParameterCriteria(null, jsonProperty, fieldProperty);
    }

    public static ParameterCriteria matchType(Class<?> parameterClass) {
        return new ParameterCriteria(parameterClass, null, null);
    }

    public static ParameterCriteria matchField(String fieldProperty) {
        return new ParameterCriteria(null, null, fieldProperty);
    }

    public ParameterCriteria mappedBy(String jsonProperty) {
        this.jsonProperty = jsonProperty;
        return this;
    }

    public ParameterCriteria(Class<?> parameterClass, String jsonProperty, String fieldProperty) {
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
        return "ParameterCriteria{" +
                "parameterClass=" + parameterClass +
                ", jsonProperty='" + jsonProperty + '\'' +
                ", fieldProperty='" + fieldProperty + '\'' +
                '}';
    }
}
