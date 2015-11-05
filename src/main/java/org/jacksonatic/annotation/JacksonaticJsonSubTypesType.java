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

public class JacksonaticJsonSubTypesType implements JsonSubTypes.Type {

    private String name = "";

    private Class<?> value;

    private JacksonaticJsonSubTypesType() {
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Class<?> value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonSubTypes.Type.class;
    }

    public static Builder type() {
        return new Builder();
    }

    public static Builder type(String name, Class<?> value) {
        return new Builder().name(name).value(value);
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonSubTypesType type = new JacksonaticJsonSubTypesType();

        public Builder name(String name) {
            type.name = name;
            return this;
        }

        public Builder value(Class<?> value) {
            type.value = value;
            return this;
        }

        @Override
        public  JsonSubTypes.Type build() {
            return type;
        }
    }
}
