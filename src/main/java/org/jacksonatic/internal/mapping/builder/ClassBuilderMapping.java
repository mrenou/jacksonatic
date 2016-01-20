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
package org.jacksonatic.internal.mapping.builder;


import org.jacksonatic.annotation.JacksonaticJsonCreator;
import org.jacksonatic.internal.annotations.Annotations;
import org.jacksonatic.internal.mapping.builder.parameter.ParameterMapping;
import org.jacksonatic.internal.util.Copyable;
import org.jacksonatic.mapping.HasAnnotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Define mapping for constructor or static factory
 */
public class ClassBuilderMapping implements HasAnnotations<ClassBuilderMapping>, Copyable<ClassBuilderMapping> {

    private Constructor<?> constructor;

    private Method staticFactory;

    private Annotations annotations;

    private List<ParameterMapping> parametersMapping;

    public ClassBuilderMapping(Constructor<?> constructor, List<ParameterMapping> parametersMapping) {
        this(constructor, null, new Annotations(), parametersMapping);
        putJsonCreator();
    }

    public ClassBuilderMapping(Method staticFactory, List<ParameterMapping> parametersMapping) {
        this(null, staticFactory, new Annotations(), parametersMapping);
        putJsonCreator();
    }

    private ClassBuilderMapping(Constructor<?> constructor, Method staticFactory, Annotations annotations, List<ParameterMapping> parametersMapping) {
        this.constructor = constructor;
        this.staticFactory = staticFactory;
        this.annotations = annotations;
        this.parametersMapping = parametersMapping;
    }

    private void putJsonCreator() {
        annotations.add(JacksonaticJsonCreator.jsonCreator());
    }

    public boolean isStaticFactory() {
        return staticFactory != null;
    }

    public int getParameterCount() {
        if (constructor != null) {
            return constructor.getParameterCount();
        }
        return staticFactory.getParameterCount();
    }

    public String getName() {
        if (constructor != null) {
            return constructor.getName();
        }
        return staticFactory.getName();
    }

    public boolean isPublic() {
        if (constructor != null) {
            return Modifier.isPublic(constructor.getModifiers());
        }
        return Modifier.isPublic(staticFactory.getModifiers());
    }

    public Class<?>[] getParameterTypes() {
        if (constructor != null) {
            return constructor.getParameterTypes();
        }
        return staticFactory.getParameterTypes();
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public Method getStaticFactory() {
        return staticFactory;
    }

    public List<ParameterMapping> getParametersMapping() {
        return parametersMapping;
    }

    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    @Override
    public ClassBuilderMapping copy() {
        return new ClassBuilderMapping(constructor,
                staticFactory,
                annotations.copy(),
                Copyable.copy(parametersMapping));
    }

}
