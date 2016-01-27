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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class JacksonaticJsonIgnoreProperties implements JsonIgnoreProperties, JacksonaticAnnotation {

    private String[] value;

    private boolean ignoreUnknown = false;

    private JacksonaticJsonIgnoreProperties() {

    }

    @Override
    public String[] value() {
        return value;
    }

    @Override
    public boolean ignoreUnknown() {
        return ignoreUnknown;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonIgnoreProperties.class;
    }

    @Override
    public String toString() {
        return "@JsonIgnoreProperties{" +
                "value=" + Arrays.toString(value) +
                ", ignoreUnknown=" + ignoreUnknown +
                '}';
    }

    public static Builder jsonIgnoreProperties() {
        return new Builder();
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonIgnoreProperties jsonIgnoreProperties = new JacksonaticJsonIgnoreProperties();

        public Builder value(String[] value) {
            jsonIgnoreProperties.value = value;
            return this;
        }

        public Builder ignoreUnknown(boolean ignoreUnknown) {
            jsonIgnoreProperties.ignoreUnknown = ignoreUnknown;
            return this;
        }

        @Override
        public JsonIgnoreProperties build() {
            return jsonIgnoreProperties;
        }
    }
}
