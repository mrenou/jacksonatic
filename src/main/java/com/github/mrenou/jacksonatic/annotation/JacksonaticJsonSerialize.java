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
package com.github.mrenou.jacksonatic.annotation;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.Converter;

import java.lang.annotation.Annotation;

public class JacksonaticJsonSerialize implements JsonSerialize, JacksonaticAnnotation {

    private Class<? extends JsonSerializer<?>> using = JsonSerializer.None.class;

    private Class<? extends JsonSerializer<?>> contentUsing = JsonSerializer.None.class;

    private Class<? extends JsonSerializer<?>> keyUsing = JsonSerializer.None.class;

    private Class<? extends JsonSerializer<?>> nullsUsing = JsonSerializer.None.class;

    private Class<?> as = Void.class;

    private Class<?> keyAs = Void.class;

    private Class<?> contentAs = Void.class;

    private Typing typing = Typing.DEFAULT_TYPING;

    private Class<? extends Converter<?, ?>> converter = Converter.None.class;

    private Class<? extends Converter<?, ?>> contentConverter = Converter.None.class;

    private Inclusion include = Inclusion.DEFAULT_INCLUSION;

    @Override
    public Class<? extends JsonSerializer<?>> using() {
        return using;
    }

    @Override
    public Class<? extends JsonSerializer<?>> contentUsing() {
        return contentUsing;
    }

    @Override
    public Class<? extends JsonSerializer<?>> keyUsing() {
        return keyUsing;
    }

    @Override
    public Class<? extends JsonSerializer<?>> nullsUsing() {
        return nullsUsing;
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
    public Typing typing() {
        return typing;
    }

    @Override
    public Class<? extends Converter<?, ?>> converter() {
        return converter;
    }

    @Override
    public Class<? extends Converter<?, ?>> contentConverter() {
        return contentConverter;
    }

    @Deprecated
    @Override
    public Inclusion include() {
        return include;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonSerialize.class;
    }

    public static Builder jsonSerialize() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "@JsonSerialize{" +
                "using=" + using +
                ", contentUsing=" + contentUsing +
                ", keyUsing=" + keyUsing +
                ", nullsUsing=" + nullsUsing +
                ", as=" + as +
                ", keyAs=" + keyAs +
                ", contentAs=" + contentAs +
                ", typing=" + typing +
                ", converter=" + converter +
                ", contentConverter=" + contentConverter +
                ", include=" + include +
                '}';
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonSerialize jsonSerialize = new JacksonaticJsonSerialize();

        public Builder using(Class<? extends JsonSerializer<?>> using) {
            jsonSerialize.using = using;
            return this;
        }

        public Builder contentUsing(Class<? extends JsonSerializer<?>> contentUsing) {
            jsonSerialize.contentUsing = contentUsing;
            return this;
        }

        public Builder keyUsing(Class<? extends JsonSerializer<?>> keyUsing) {
            jsonSerialize.keyUsing = keyUsing;
            return this;
        }

        public Builder nullsUsing(Class<? extends JsonSerializer<?>> nullsUsing) {
            jsonSerialize.nullsUsing = nullsUsing;
            return this;
        }

        public Builder as(Class<?> as) {
            jsonSerialize.as = as;
            return this;
        }

        public Builder keyAs(Class<?> keyAs) {
            jsonSerialize.keyAs = keyAs;
            return this;
        }

        public Builder contentAs(Class<?> contentAs) {
            jsonSerialize.contentAs = contentAs;
            return this;
        }

        public Builder typing(Typing typing) {
            jsonSerialize.typing = typing;
            return this;
        }

        public Builder converter(Class<? extends Converter<?, ?>> converter) {
            jsonSerialize.converter = converter;
            return this;
        }

        public Builder contentConverter(Class<? extends Converter<?, ?>> contentConverter) {
            jsonSerialize.contentConverter = contentConverter;
            return this;
        }

        public Builder include(Inclusion include) {
            jsonSerialize.include = include;
            return this;
        }

        @Override
        public JsonSerialize build() {
            return jsonSerialize;
        }
    }
}
