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
package org.jacksonatic.internal.introspection;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BasicBeanDescription;
import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
import com.fasterxml.jackson.databind.introspect.POJOPropertiesCollector;
import org.jacksonatic.internal.MappingConfigurerInternal;

/**
 * Customized class introspector to use our own {@link com.fasterxml.jackson.databind.introspect.AnnotatedClass}
 * construction.
 */
public class JacksonaticClassIntrospector extends BasicClassIntrospector {

    private AnnotatedClassConstructor annotatedClassConstructor;

    public void register(MappingConfigurerInternal mappingConfigurer) {
        annotatedClassConstructor = new AnnotatedClassConstructor(mappingConfigurer);
    }

    @Override
    public BasicBeanDescription forClassAnnotations(MapperConfig<?> cfg, JavaType type, MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            desc = _cachedFCA.get(type);
            if (desc == null) {
                boolean useAnnotations = cfg.isAnnotationProcessingEnabled();
                AnnotatedClass ac = annotatedClassConstructor.constructForSerialization(type.getRawClass(),
                        (useAnnotations ? cfg.getAnnotationIntrospector() : null), r);
                desc = BasicBeanDescription.forOtherUse(cfg, type, ac);
                _cachedFCA.put(type, desc);
            }
        }
        return desc;
    }

    @Override
    public BasicBeanDescription forDirectClassAnnotations(MapperConfig<?> cfg, JavaType type, MixInResolver r) {
        BasicBeanDescription desc = _findStdTypeDesc(type);
        if (desc == null) {
            boolean useAnnotations = cfg.isAnnotationProcessingEnabled();
            AnnotationIntrospector ai = cfg.getAnnotationIntrospector();
            AnnotatedClass ac = annotatedClassConstructor.constructWithoutSuperTypes(type.getRawClass(),
                    (useAnnotations ? ai : null), r);
            desc = BasicBeanDescription.forOtherUse(cfg, type, ac);
        }
        return desc;
    }

    @Override
    protected POJOPropertiesCollector collectProperties(MapperConfig<?> config, JavaType type, MixInResolver r, boolean forSerialization, String mutatorPrefix) {
        boolean useAnnotations = config.isAnnotationProcessingEnabled();
        AnnotatedClass ac;
        if (forSerialization) {
             ac = annotatedClassConstructor.constructForSerialization(type.getRawClass(),
                    (useAnnotations ? config.getAnnotationIntrospector() : null), r);
        } else {
             ac = annotatedClassConstructor.constructForDeserialization(type.getRawClass(),
                    (useAnnotations ? config.getAnnotationIntrospector() : null), r);
        }
        POJOPropertiesCollector propertyCollector = constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix);
        POJOPropertiesCollector collect = propertyCollector.collect();
        return collect;
    }

    protected POJOPropertiesCollector collectPropertiesWithBuilder(MapperConfig<?> config, JavaType type, MixInResolver r, boolean forSerialization) {
        boolean useAnnotations = config.isAnnotationProcessingEnabled();
        AnnotationIntrospector ai = useAnnotations ? config.getAnnotationIntrospector() : null;
        AnnotatedClass ac;
        if (forSerialization) {
            ac = annotatedClassConstructor.constructForSerialization(type.getRawClass(), ai, r);
        } else {
            ac = annotatedClassConstructor.constructForDeserialization(type.getRawClass(), ai, r);
        }
        JsonPOJOBuilder.Value builderConfig = (ai == null) ? null : ai.findPOJOBuilderConfig(ac);
        String mutatorPrefix = (builderConfig == null) ? "with" : builderConfig.withPrefix;
        return constructPropertyCollector(config, ac, type, forSerialization, mutatorPrefix).collect();
    }

}
