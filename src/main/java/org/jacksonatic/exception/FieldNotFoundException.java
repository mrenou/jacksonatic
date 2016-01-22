package org.jacksonatic.exception;

public class FieldNotFoundException extends MappingException {

    public FieldNotFoundException(String fieldName, Class<?> type) {
        super(String.format("Field with name '%s' doesn't exist in class mapping %s", fieldName, type.getName()));
    }
}
