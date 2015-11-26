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
package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jacksonatic.annotation.Annotations;

import java.util.Optional;

/**
 * Allowing to define jackson field mapping in a programmatic way.
 */
public class FieldMapping implements HasAnnotations<FieldMapping>, PropertyMapper<FieldMapping> {

    private String name;

    private Annotations annotations;

    /**
     * Start a field mapping for the given field name
     *
     * @param fieldName
     * @return
     */
    public static FieldMapping field(String fieldName) {
        return new FieldMapping(fieldName);
    }

    private FieldMapping(String name) {
        this(name, new Annotations());
    }

    private FieldMapping(String name, Annotations annotations) {
        this.name = name;
        this.annotations = annotations;
    }

    public String getName() {
        return name;
    }

    public String getMappedName() {
        String s = Optional.ofNullable(annotations.get(JsonProperty.class))
                .map(annotation -> ((JsonProperty) annotation).value())
                .filter(name -> name != null && !name.isEmpty())
                .orElse(name);
        return s;
    }

    public boolean isMapped() {
        return annotations.containsKey(JsonProperty.class) && !annotations.containsKey(JsonIgnore.class);
    }

    public boolean isIgnored() {
        return annotations.containsKey(JsonIgnore.class);
    }

    public Annotations getAnnotations() {
        return annotations;
    }

    @Override
    public FieldMapping builder() {
        return this;
    }

    FieldMapping copy() {
        return new FieldMapping(name, this.annotations.copy());
    }

    FieldMapping copyWithParentMapping(FieldMapping parentMapping) {
        return new FieldMapping(name, this.annotations.size() == 0 ? parentMapping.annotations.copy() : annotations.copy());
    }
}
