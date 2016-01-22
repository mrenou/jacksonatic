package org.jacksonatic.exception;

import org.jacksonatic.internal.mapping.builder.parameter.ParameterCriteriaInternal;

public class ClassBuilderParameterMappingException extends MappingException {

    public static ClassBuilderParameterMappingException parameterJsonPropertyNotFoundException(ParameterCriteriaInternal parameterCriteria, Class<?> type) {
        return new ClassBuilderParameterMappingException(String.format("Cannot find json property for criteria '%s' in class mapping %s", parameterCriteria, type.getName()));
    }

    public static ClassBuilderParameterMappingException parameterTypeNotFoundException(ParameterCriteriaInternal parameterCriteria, Class<?> type) {
        return new ClassBuilderParameterMappingException(String.format("Cannot find parameter type for criteria '%s' in class mapping %s", parameterCriteria, type.getName()));
    }

    private ClassBuilderParameterMappingException(String message) {
        super(message);
    }
}
