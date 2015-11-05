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

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.lang.annotation.Annotation;

public class JacksonaticJsonTypeName implements JsonTypeName {

    private String value = "";

    private JacksonaticJsonTypeName() {
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonTypeName.class;
    }

    @Override
    public String value() {
        return value;
    }

    public static Builder jsonTypeName() {
        return new Builder();
    }

    public static Builder jsonTypeName(String value) {
        return new Builder().value(value);
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonTypeName jsonTypeName = new JacksonaticJsonTypeName();

        public Builder value(String value) {
            jsonTypeName.value = value;
            return this;
        }

        @Override
        public JsonTypeName build() {
            return jsonTypeName;
        }
    }
}
