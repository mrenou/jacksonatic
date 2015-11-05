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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.annotation.Annotation;

public class JacksonaticJsonIgnore implements JsonIgnore {

    private boolean value = true;

    private JacksonaticJsonIgnore() {

    }

    @Override
    public boolean value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonIgnore.class;
    }

    public static Builder jsonIgnore() {
        return new Builder();
    }

    public static Builder jsonIgnore(boolean value) {
        return new Builder().value(value);
    }

    static class Builder implements AnnotationBuilder {

        private JacksonaticJsonIgnore jsonIgnore = new JacksonaticJsonIgnore();

        public  Builder value(boolean value) {
            jsonIgnore.value = value;
            return this;
        }

        @Override
        public JsonIgnore build() {
            return jsonIgnore;
        }
    }
}
