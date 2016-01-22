package org.jacksonatic.exception;

import org.jacksonatic.internal.mapping.method.MethodSignature;

public class MethodNotFoundException extends MappingException {

    public MethodNotFoundException(MethodSignature methodSignature, Class<?> type) {
        super(String.format("Method with signature '%s' doesn't exist in class mapping %s", methodSignature, type.getName()));
    }
}
