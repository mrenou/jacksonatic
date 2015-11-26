package org.jacksonatic.mapping;

import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.jacksonatic.mapping.MethodSignature.methodSignature;
import static org.jacksonatic.util.ReflectionUtil.getFieldsWithInheritance;
import static org.jacksonatic.util.ReflectionUtil.getMethodsWithInheritance;

public class TypeChecker<T> {

    private final Class<T> type;

    private final Set<String> existingFieldNames;

    private final Set<MethodSignature> existingMethodSignatures;

    private final Set<String> existingMethodNames;

    public TypeChecker(Class<T> type) {
        this.type = type;
        this.existingFieldNames = getFieldsWithInheritance(type).map(field -> field.getName()).collect(toSet());
        this.existingMethodSignatures = getMethodsWithInheritance(type).map(method -> methodSignature(method.getName(), method.getParameterTypes())).collect(toSet());
        this.existingMethodNames = getMethodsWithInheritance(type).map(method -> method.getName()).collect(toSet());
    }

    public void checkFieldExists(String name) {
        if (!existingFieldNames.contains(name)) {
            throw new IllegalStateException(String.format("Field with name '%s' doesn't exist in class mapping %s", name, type.getName()));
        }
    }

    public void checkMethodExists(MethodSignature methodSignature) {
        if (!existingMethodSignatures.contains(methodSignature) && !existingMethodNames.contains(methodSignature.name)) {
            throw new IllegalStateException(String.format("Method with signature '%s' doesn't exist in class mapping %s", methodSignature, type.getName()));
        }
    }
}
