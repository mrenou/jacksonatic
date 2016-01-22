package org.jacksonatic.exception;

import org.jacksonatic.internal.mapping.builder.ClassBuilderCriteria;

public class ClassBuilderNotFoundException extends MappingException {

    public ClassBuilderNotFoundException(ClassBuilderCriteria classBuilderCriteria, Class<?> type) {
        super(String.format("Cannot find class builder for criteria '%s' in class mapping %s", classBuilderCriteria.mappingAsString(), type.getName()));
    }

    public ClassBuilderNotFoundException(ClassBuilderParameterMappingException e) {
        super("Cannot find class builder in class mapping %s, parameters doesn't match", e);
    }
}
