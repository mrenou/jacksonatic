/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic.internal;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import org.jacksonatic.annotation.JacksonaticAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.jacksonatic.internal.mapping.method.MethodSignature.methodSignature;
import static org.jacksonatic.internal.util.StreamUtil.stream;

public class AnnotatedClassLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotatedClassLogger.class);

    private static String ln = System.getProperty("line.separator");

    private AnnotatedClassLogger() {
        ln = ln == null || ln.isEmpty() ? "\n" : ln;
    }

    public static void log(AnnotatedClass annotatedClass) {
        if (LOGGER.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append(ln);
            logClassAnnotations(annotatedClass, sb);
            logFieldAnnotations(annotatedClass, sb);
            logConstructorAnnotations(annotatedClass, sb);
            logStaticFactoryAnnotations(annotatedClass, sb);
            logMethodAnnotations(annotatedClass, sb);
            String toLog = sb.toString();
            if (!toLog.isEmpty()) {
                LOGGER.debug("Annotations added for : {}", toLog);
            }
        }
    }

    private static void logClassAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        sb.append(("Class " + annotatedClass.getAnnotated().getName() + ": " + annotationsItToStr(annotatedClass.annotations())))
                .append(ln);
    }

    private static void logFieldAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        stream(annotatedClass.fields())
                .filter(annotatedField -> hasAnnotation(annotatedField))
                .forEach(annotatedField -> sb.append("> Field[" + annotatedField.getName() + "] : " + annotationsItToStr(annotatedField.annotations()))
                        .append(ln));
    }

    private static void logConstructorAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        stream(annotatedClass.getConstructors())
                .filter(annotatedConstructor -> hasAnnotationOrParameterAnnotation(annotatedConstructor))
                .forEach(annotatedConstructor -> {
                    List<Class<?>> parameterTypes = IntStream.range(0, annotatedConstructor.getParameterCount()).mapToObj(index -> annotatedConstructor.getRawParameterType(index)).collect(Collectors.toList());
                    sb.append("> Constructor[" + methodSignature(annotatedClass.getAnnotated().getSimpleName(), parameterTypes) + "] : " + annotationsItToStr(annotatedConstructor.annotations()))
                            .append(ln);
                    logParameters(sb, annotatedConstructor);
                });
    }

    private static void logStaticFactoryAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        stream(annotatedClass.getStaticMethods())
                .filter(annotatedMethod -> hasAnnotationOrParameterAnnotation(annotatedMethod))
                .forEach(annotatedMethod -> {
                    sb.append("> StaticFactory[" + methodSignature(annotatedMethod.getName(), annotatedMethod.getRawParameterTypes()) + "] : " + annotationsItToStr(annotatedMethod.annotations()))
                            .append(ln);
                    logParameters(sb, annotatedMethod);
                });
    }

    private static void logMethodAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        stream(annotatedClass.memberMethods())
                .filter(annotatedMethod -> hasAnnotationOrParameterAnnotation(annotatedMethod))
                .forEach(annotatedMethod -> {
                    sb.append("> Method[" + methodSignature(annotatedMethod.getName(), annotatedMethod.getRawParameterTypes()) + "] : " + annotationsItToStr(annotatedMethod.annotations()))
                            .append(ln);
                    logParameters(sb, annotatedMethod);
                });
    }

    private static boolean hasAnnotationOrParameterAnnotation(AnnotatedWithParams annotatedWithParams) {
        return hasAnnotation(annotatedWithParams.annotations()) ||
                IntStream.range(0, annotatedWithParams.getParameterCount())
                        .mapToObj(index -> annotatedWithParams.getParameterAnnotations(index))
                        .reduce(false,
                                (acc, annotationMap) -> acc | hasAnnotation(annotationMap),
                                (acc1, acc2) -> acc1 | acc2);

    }

    private static boolean hasAnnotation(Annotated annotated) {
        if (annotated != null) {
            return hasAnnotation(annotated.annotations());
        }
        return false;
    }

    private static boolean hasAnnotation(AnnotationMap annotationMap) {
        if (annotationMap != null) {
            return hasAnnotation(annotationMap.annotations());
        }
        return false;
    }

    private static boolean hasAnnotation(Iterable<Annotation> annotations) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (JacksonaticAnnotation.class.isInstance(annotation)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void logParameters(StringBuilder sb, AnnotatedWithParams annotatedWithParams) {
        IntStream.range(0, annotatedWithParams.getParameterCount())
                .filter(index -> hasAnnotation(annotatedWithParams.getParameterAnnotations(index)))
                .forEach(index -> sb.append(" > p" + index + ": " + annotationsItToStr(annotatedWithParams.getParameterAnnotations(index)))
                        .append(ln));
    }

    private static String annotationsItToStr(AnnotationMap annotationMap) {
        return annotationMap != null ? annotationsItToStr(annotationMap.annotations()) : "";
    }

    private static String annotationsItToStr(Iterable<Annotation> annotations) {
        return stream(annotations)
                .filter(annotation -> JacksonaticAnnotation.class.isInstance(annotation))
                .map(Annotation::toString).collect(Collectors.joining(","));
    }

}
