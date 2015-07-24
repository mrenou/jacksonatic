package org.jacksonatic.mapping;

import java.util.ArrayList;
import java.util.List;

import static org.jacksonatic.mapping.ParametersMappingBuilder.buildParametersMapping;

public class ClassBuilderCriteria {

    private String methodName;

    private List<ParameterMapping> parameterCriteria;

    private boolean staticFactory = false;

    private boolean any = false;

    public static ClassBuilderCriteria mapConstructor(Class<?> classToBuild, List<ParameterCriteria> parameterCriteriaList) {
        return new ClassBuilderCriteria(classToBuild, null, parameterCriteriaList, false);
    }

    public static ClassBuilderCriteria mapStaticFactory(Class<?> classToBuild, List<ParameterCriteria> parameterCriteriaList) {
        return new ClassBuilderCriteria(classToBuild, null, parameterCriteriaList, true);
    }

    public static ClassBuilderCriteria mapStaticFactory(Class<?> classToBuild, String methodName, List<ParameterCriteria> parameterCriteriaList) {
        return new ClassBuilderCriteria(classToBuild, methodName, parameterCriteriaList, true);
    }

    public static ClassBuilderCriteria mapAConstructorOrStaticFactory() {
        return new ClassBuilderCriteria();
    }

    private ClassBuilderCriteria(Class<?> classToBuild, String methodName, List<ParameterCriteria> parameterCriterias, boolean staticFactory) {
        this.methodName = methodName;
        this.parameterCriteria = buildParametersMapping(classToBuild, parameterCriterias);
        this.staticFactory = staticFactory;
        this.any = false;
    }

    private ClassBuilderCriteria() {
        this.methodName = "";
        this.parameterCriteria = new ArrayList<>();
        this.staticFactory = false;
        this.any = true;
    }

    public boolean isAny() {
        return any;
    }

    public List<ParameterMapping> getParameterCriteria() {
        return parameterCriteria;
    }

    public String getMethodName() {
        return methodName;
    }

    public boolean isStaticFactory() {
        return staticFactory;
    }

}
