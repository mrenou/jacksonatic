package org.jacksonatic;

import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AnnotatedClassLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedClassLogger.class);

    private static String ln = System.getProperty("line.separator");

    public AnnotatedClassLogger() {
        ln = ln == null || ln.isEmpty() ? "\n" : ln;
    }

    public static void log(AnnotatedClass annotatedClass) {
        if (LOGGER.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append(ln);
            logClassAnnotations(annotatedClass, sb);
            logFieldAnnotations(annotatedClass, sb);
            logMethodAnnotations(annotatedClass, sb);
            LOGGER.debug("Annotations added for : {}", sb.toString());
        }
    }

    private static void logClassAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        sb.append(("Class " + annotatedClass.getAnnotated().getName() + " " + annotationsItToStr(annotatedClass.annotations())));
        sb.append(ln);
    }

    private static void logFieldAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        StreamSupport.stream(annotatedClass.fields().spliterator(), false)
                .filter(annotatedField -> StreamSupport.stream(annotatedField.annotations().spliterator(), false).collect(Collectors.toList()).size() > 0)
                .forEach(annotatedField -> sb.append("> Field[" + annotatedField.getName() + "] : " + annotationsItToStr(annotatedField.annotations()))
                        .append(ln));
    }

    private static void logMethodAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        StreamSupport.stream(annotatedClass.memberMethods().spliterator(), false)
                .filter(annotatedMethod -> StreamSupport.stream(annotatedMethod.annotations().spliterator(), false).collect(Collectors.toList()).size() > 0)
                .forEach(annotatedMethod -> sb.append("> Method[" + annotatedMethod.getName() + "] : " + annotationsItToStr(annotatedMethod.annotations()))
                        .append(ln));
    }

    private static String annotationsItToStr(Iterable<Annotation> annotations) {
        return StreamSupport.stream(annotations.spliterator(), false)
                .map(annotation -> annotation.toString()).collect(Collectors.joining(","));
    }

}
