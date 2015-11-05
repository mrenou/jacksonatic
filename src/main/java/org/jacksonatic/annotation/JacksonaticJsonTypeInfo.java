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

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.lang.annotation.Annotation;

public class JacksonaticJsonTypeInfo implements JsonTypeInfo {

    private Id use;

    private As include = As.PROPERTY;

    private String property = "";

    private Class<?> defaultImpl = None.class;

    private boolean visible = false;

    private JacksonaticJsonTypeInfo() {

    }

    @Override
    public Id use() {
        return use;
    }

    @Override
    public As include() {
        return include;
    }

    @Override
    public String property() {
        return property;
    }

    @Override
    public Class<?> defaultImpl() {
        return defaultImpl;
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return JsonTypeInfo.class;
    }

    public static Builder jsonTypeInfo() {
        return new Builder();
    }

    public static class Builder implements AnnotationBuilder {

        private JacksonaticJsonTypeInfo jsonTypeInfo = new JacksonaticJsonTypeInfo();

        public Builder use(Id use) {
            jsonTypeInfo.use = use;
            return this;
        }

        public Builder include(As include) {
            jsonTypeInfo.include = include;
            return this;
        }

        public Builder property(String property) {
            jsonTypeInfo.property = property;
            return this;
        }

        public Builder defaultImpl(Class<?> defaultImpl) {
            jsonTypeInfo.defaultImpl = defaultImpl;
            return this;
        }

        public Builder use(boolean visible) {
            jsonTypeInfo.visible = visible;
            return this;
        }

        @Override
        public JsonTypeInfo build() {
            return jsonTypeInfo;
        }
    }

}
