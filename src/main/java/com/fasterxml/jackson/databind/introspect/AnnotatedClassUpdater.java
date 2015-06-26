package com.fasterxml.jackson.databind.introspect;


import java.lang.reflect.Field;
import java.util.List;

public class AnnotatedClassUpdater {

    public static void setConstructors(AnnotatedClass annotatedClass, List<AnnotatedConstructor> annotatedConstructors) {
        annotatedClass._constructors = annotatedConstructors;
    }

    public static void setFields(AnnotatedClass annotatedClass, List<AnnotatedField> annotatedFields) {
        annotatedClass._fields = annotatedFields;
    }
}
