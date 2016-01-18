/**
 * Copyright (C) 2015 Morgan Renou (mrenou@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jacksonatic.internal;

import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.stream.Collectors;

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
            logMethodAnnotations(annotatedClass, sb);
            LOGGER.debug("Annotations added for : {}", sb.toString());
        }
    }

    private static void logClassAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        sb.append(("Class " + annotatedClass.getAnnotated().getName() + " " + annotationsItToStr(annotatedClass.annotations())))
                .append(ln);
    }

    private static void logFieldAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        stream(annotatedClass.fields())
                .filter(annotatedField -> stream(annotatedField.annotations()).collect(Collectors.toList()).size() > 0)
                .forEach(annotatedField -> sb.append("> Field[" + annotatedField.getName() + "] : " + annotationsItToStr(annotatedField.annotations()))
                        .append(ln));
    }

    private static void logMethodAnnotations(AnnotatedClass annotatedClass, StringBuilder sb) {
        stream(annotatedClass.memberMethods())
                .filter(annotatedMethod -> stream(annotatedMethod.annotations()).collect(Collectors.toList()).size() > 0)
                .forEach(annotatedMethod -> sb.append("> Method[" + annotatedMethod.getName() + "] : " + annotationsItToStr(annotatedMethod.annotations()))
                        .append(ln));
    }

    private static String annotationsItToStr(Iterable<Annotation> annotations) {
        return stream(annotations)
                .map(Annotation::toString).collect(Collectors.joining(","));
    }

}
