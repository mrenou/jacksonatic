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


import org.jacksonatic.annotation.Annotations;
import org.jacksonatic.annotation.JacksonaticJsonCreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Define mapping for constructor or static factory
 */
public class ClassBuilderMapping implements HasAnnotations<ClassBuilderMapping> {

    private Constructor<?> constructor;

    private Method staticFactory;

    private Annotations annotations;

    private List<ParameterMapping> parametersMapping;

    ClassBuilderMapping(Constructor<?> constructor, Method staticFactory, Annotations annotations, List<ParameterMapping> parametersMapping) {
        this.constructor = constructor;
        this.staticFactory = staticFactory;
        this.annotations = annotations;
        this.parametersMapping = parametersMapping;
    }

    public ClassBuilderMapping(Constructor<?> constructor, List<ParameterMapping> parametersMapping) {
        this.constructor = constructor;
        this.annotations = new Annotations();
        this.parametersMapping = parametersMapping;
        putJsonCreator();
    }

    public ClassBuilderMapping(Method staticFactory, List<ParameterMapping> parametersMapping) {
        this.staticFactory = staticFactory;
        this.annotations = new Annotations();
        this.parametersMapping = parametersMapping;
        putJsonCreator();
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

    @Override
    public Annotations getAnnotations() {
        return annotations;
    }

    @Override
    public ClassBuilderMapping builder() {
        return this;
    }

    public List<ParameterMapping> getParametersMapping() {
        return parametersMapping;
    }

    ClassBuilderMapping copy() {
        return new ClassBuilderMapping(constructor,
                staticFactory,
                annotations.copy(),
                parametersMapping.stream().map(parameterMapping -> parameterMapping.copy()).collect(toList()));
    }

}
