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
package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jacksonatic.annotation.AnnotationBuilder;
import org.jacksonatic.annotation.Annotations;

import java.util.Optional;

import static org.jacksonatic.annotation.JacksonaticJsonIgnore.jsonIgnore;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;

/**
 * Allowing to define jackson field mapping in a programmatic way.
 */
public class FieldMapping implements HasAnnotations {

    private String name;

    private Annotations annotations;

    /**
     * Start a field mapping for the given field name
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

    /**
     * Add an annotation
     * @param annotationBuilder
     * @return
     */
    public FieldMapping add(AnnotationBuilder annotationBuilder) {
        annotations.add(annotationBuilder);
        return this;
    }

    /**
     * ignore the field
     * @return
     */
    public FieldMapping ignore() {
        add(jsonIgnore());
        return this;
    }

    /**
     * map the field
     * @return
     */
    public FieldMapping map() {
        mapTo(name);
        return this;
    }

    /**
     * map the field with the given name
     * @return
     */
    public FieldMapping mapTo(String mappedName) {
        add(jsonProperty(mappedName));
        return this;
    }

    public String getName() {
        return name;
    }

    public String getMappedName() {
        return Optional.ofNullable(annotations.get(JsonProperty.class)).map(annotation -> ((JsonProperty)annotation).value()).orElse(name);
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

    FieldMapping copy() {
        return new FieldMapping(name, this.annotations.copy());
    }

    FieldMapping copyWithParentMapping(FieldMapping parentMapping) {
        return new FieldMapping(name, this.annotations.size() == 0 ?
                parentMapping.annotations.copy():
                annotations.copy());
    }
}
