package org.jacksonatic.mapping;


import com.fasterxml.jackson.annotation.JsonCreator;
import org.jacksonatic.annotation.JacksonaticJsonCreator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class ClassBuilderMapping {

    private Constructor<?> constructor;

    private Method staticFactory;

    private Map<Class<? extends Annotation>, Annotation> annotations;

    private List<ParameterMapping> parametersMapping;

    ClassBuilderMapping(Constructor<?> constructor, Method staticFactory, Map<Class<? extends Annotation>, Annotation> annotations, List<ParameterMapping> parametersMapping) {
        this.constructor = constructor;
        this.staticFactory = staticFactory;
        this.annotations = annotations;
        this.parametersMapping = parametersMapping;
    }

    public ClassBuilderMapping(Constructor<?> constructor, List<ParameterMapping> parametersMapping) {
        this.constructor = constructor;
        this.annotations = new HashMap<>();
        this.parametersMapping = parametersMapping;
        putJsonCreator();
    }

    public ClassBuilderMapping(Method staticFactory, List<ParameterMapping> parametersMapping) {
        this.staticFactory = staticFactory;
        this.annotations = new HashMap<>();
        this.parametersMapping = parametersMapping;
        putJsonCreator();
    }

    private void putJsonCreator() {
        annotations.put(JsonCreator.class, new JacksonaticJsonCreator());
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

    public Map<Class<? extends Annotation>, Annotation> getAnnotations() {
        return annotations;
    }

    public List<ParameterMapping> getParametersMapping() {
        return parametersMapping;
    }

    ClassBuilderMapping copy() {
        return new ClassBuilderMapping(constructor,
                staticFactory,
                annotations.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue())),
                parametersMapping.stream().map(parameterMapping -> parameterMapping.copy()).collect(toList()));
    }

}
