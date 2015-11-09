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
 * Allowing to define jackson property mapping in a programmatic way.
 */
public class PropertyMapping implements HasAnnotations {

    private String fieldName;

    private Annotations annotations;

    /**
     * Start a property mapping for the given field name
     * @param fieldName
     * @return
     */
    public static PropertyMapping property(String fieldName) {
        return new PropertyMapping(fieldName);
    }

    private PropertyMapping(String fieldName) {
        this(fieldName, new Annotations());
    }

    private PropertyMapping(String fieldName, Annotations annotations) {
        this.fieldName = fieldName;
        this.annotations = annotations;
    }

    /**
     * Add an annotation
     * @param annotationBuilder
     * @return
     */
    public PropertyMapping add(AnnotationBuilder annotationBuilder) {
        annotations.add(annotationBuilder);
        return this;
    }

    /**
     * ignore the property
     * @return
     */
    public PropertyMapping ignore() {
        add(jsonIgnore());
        return this;
    }

    /**
     * map the property
     * @return
     */
    public PropertyMapping map() {
        mapTo(fieldName);
        return this;
    }

    /**
     * map the property with the given name
     * @return
     */
    public PropertyMapping mapTo(String mappedName) {
        add(jsonProperty(mappedName));
        return this;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMappedName() {
        return Optional.ofNullable(annotations.get(JsonProperty.class)).map(annotation -> ((JsonProperty)annotation).value()).orElse(fieldName);
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

    PropertyMapping copy() {
        return new PropertyMapping(fieldName, this.annotations.copy());
    }

    PropertyMapping copyWithParentMapping(PropertyMapping parentMapping) {
        return new PropertyMapping(fieldName, this.annotations.size() == 0 ?
                parentMapping.annotations.copy():
                annotations.copy());
    }
}
