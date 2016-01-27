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
package org.jacksonatic.annotation;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class JacksonaticJsonSubTypes implements JsonSubTypes, JacksonaticAnnotation {

    private Type[] value;

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonSubTypes.class;
    }

    @Override
    public Type[] value() {
        return value;
    }

    public static Builder jsonSubTypes() {
        return new Builder();
    }

    public static Builder jsonSubTypes(Type[] value) {
        return new Builder().value(value);
    }

    @Override
    public String toString() {
        return "@JsonSubTypes{" +
                "value=" + Arrays.toString(value) +
                '}';
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonSubTypes jsonSubTypes = new JacksonaticJsonSubTypes();

        public Builder value(Type[] value) {
            jsonSubTypes.value = value;
            return this;
        }

        @Override
        public JsonSubTypes build() {
            return jsonSubTypes;
        }
    }
}
