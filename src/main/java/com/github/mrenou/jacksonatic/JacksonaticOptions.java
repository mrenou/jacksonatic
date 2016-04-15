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
package com.github.mrenou.jacksonatic;

/**
 * Created by morgan on 14/04/16.
 */
public class JacksonaticOptions {

    private boolean typeChecking = true;

    public static Builder options() {
        return new Builder();
    }

    public boolean typeChecking() {
        return typeChecking;
    }

    public static class Builder {

        private JacksonaticOptions jacksonaticOptions = new JacksonaticOptions();

        public Builder disableTypeChecking() {
            jacksonaticOptions.typeChecking = false;
            return this;
        }

        public JacksonaticOptions build() {
            return jacksonaticOptions;
        }
    }
}
