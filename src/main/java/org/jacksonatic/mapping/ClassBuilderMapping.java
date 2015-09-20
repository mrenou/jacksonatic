package org.jacksonatic.mapping;


import org.jacksonatic.annotation.Annotations;
import org.jacksonatic.annotation.JacksonaticJsonCreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ClassBuilderMapping {

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

    public Annotations getAnnotations() {
        return annotations;
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
