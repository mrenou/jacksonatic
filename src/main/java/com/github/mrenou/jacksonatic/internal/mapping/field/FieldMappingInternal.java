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
package com.github.mrenou.jacksonatic.internal.mapping.field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.mrenou.jacksonatic.internal.annotations.Annotations;
import com.github.mrenou.jacksonatic.internal.mapping.PropertyMapperInternal;
import com.github.mrenou.jacksonatic.internal.util.Copyable;
import com.github.mrenou.jacksonatic.internal.util.Mergeable;
import com.github.mrenou.jacksonatic.mapping.FieldMapping;

import java.util.Optional;

/**
 * Define annotations mapping for a field
 */
public class FieldMappingInternal implements FieldMapping, PropertyMapperInternal<FieldMapping>, Copyable<FieldMappingInternal>, Mergeable<FieldMappingInternal> {

    private String name;

    private Annotations annotations;

    public FieldMappingInternal(String name) {
        this(name, new Annotations());
    }

    private FieldMappingInternal(String name, Annotations annotations) {
        this.name = name;
        this.annotations = annotations;
    }

    public String getName() {
        return name;
    }

    public String getMappedName() {
        return Optional.ofNullable(annotations.get(JsonProperty.class))
                .map(annotation -> ((JsonProperty) annotation).value())
                .filter(name1 -> name1 != null && !name1.isEmpty())
                .orElse(name);
    }

    public boolean isIgnored() {
        return annotations.containsKey(JsonIgnore.class);
    }

    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    @Override
    public FieldMappingInternal copy() {
        return new FieldMappingInternal(name, this.annotations.copy());
    }

    @Override
    public FieldMappingInternal mergeWith(FieldMappingInternal parentMapping) {
        return new FieldMappingInternal(name, this.annotations.size() == 0 ? parentMapping.annotations.copy() : annotations.copy());
    }

}
