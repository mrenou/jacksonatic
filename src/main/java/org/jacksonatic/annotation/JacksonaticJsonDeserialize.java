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

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.Converter;

import java.lang.annotation.Annotation;

public class JacksonaticJsonDeserialize implements JsonDeserialize {

    private Class<? extends JsonDeserializer<?>> using = JsonDeserializer.None.class;

    private Class<? extends JsonDeserializer<?>> contentUsing = JsonDeserializer.None.class;

    private Class<? extends KeyDeserializer> keyUsing = KeyDeserializer.None.class;

    private Class<?> builder = Void.class;

    private Class<? extends Converter<?, ?>> converter = Converter.None.class;

    private Class<? extends Converter<?, ?>> contentConverter = Converter.None.class;

    private Class<?> as = Void.class;

    private Class<?> keyAs = Void.class;

    private Class<?> contentAs = Void.class;

    @Override
    public Class<? extends JsonDeserializer<?>> using() {
        return using;
    }

    @Override
    public Class<? extends JsonDeserializer<?>> contentUsing() {
        return contentUsing;
    }

    @Override
    public Class<? extends KeyDeserializer> keyUsing() {
        return keyUsing;
    }

    @Override
    public Class<?> builder() {
        return builder;
    }

    @Override
    public Class<? extends Converter<?, ?>> converter() {
        return converter;
    }

    @Override
    public Class<? extends Converter<?, ?>> contentConverter() {
        return contentConverter;
    }

    @Override
    public Class<?> as() {
        return as;
    }

    @Override
    public Class<?> keyAs() {
        return keyAs;
    }

    @Override
    public Class<?> contentAs() {
        return contentAs;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonDeserialize.class;
    }

    public static Builder jsonDeserialize() {
        return new Builder();
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonDeserialize jsonDeserialize = new JacksonaticJsonDeserialize();

        public Builder using(Class<? extends JsonDeserializer<?>> using) {
            jsonDeserialize.using = using;
            return this;
        }

        public Builder contentUsing(Class<? extends JsonDeserializer<?>> contentUsing) {
            jsonDeserialize.contentUsing = contentUsing;
            return this;
        }

        public Builder keyUsing(Class<? extends KeyDeserializer> keyUsing) {
            jsonDeserialize.keyUsing = keyUsing;
            return this;
        }

        public Builder builder(Class<?> builder) {
            jsonDeserialize.builder = builder;
            return this;
        }

        public Builder converter(Class<? extends Converter<?, ?>> converter) {
            jsonDeserialize.converter = converter;
            return this;
        }

        public Builder contentConverter(Class<? extends Converter<?, ?>> contentConverter) {
            jsonDeserialize.contentConverter = contentConverter;
            return this;
        }

        public Builder as(Class<?> as) {
            jsonDeserialize.as = as;
            return this;
        }

        public Builder keyAs(Class<?> keyAs) {
            jsonDeserialize.keyAs = keyAs;
            return this;
        }

        public Builder contentAs(Class<?> contentAs) {
            jsonDeserialize.contentAs = contentAs;
            return this;
        }

        @Override
        public JsonDeserialize build() {
            return jsonDeserialize;
        }
    }
}
