package test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;

import java.lang.annotation.Annotation;

public class PropertyProcess {

    public static AnnotatedField process(AnnotatedField annotatedField, ClassMapping classMapping) {
        AnnotationMap annotationMap = new AnnotationMap();
        PropertyMapping propertyMapping = classMapping.getPropertyMapping(annotatedField.getName());

        if ((!classMapping.allPropertiesAreMapped() && !propertyMapping.isMapped())
                || propertyMapping.isIgnored()) {
            annotationMap.addIfNotPresent(new JsonIgnore(){

                @Override
                public boolean value() {
                    return true;
                }

                @Override
                public Class<? extends Annotation> annotationType() {
                    return JsonIgnore.class;
                }
            });
        }

        if (propertyMapping.hasMappedName()) {
            annotationMap.addIfNotPresent(new JsonProperty(){

                @Override
                public Class<? extends Annotation> annotationType() {
                    return JsonProperty.class;
                }

                @Override
                public String value() {
                    return propertyMapping.getMappedName();
                }

                @Override
                public boolean required() {
                    return false;
                }

                @Override
                public int index() {
                    return INDEX_UNKNOWN;
                }

                @Override
                public String defaultValue() {
                    return "";
                }
            });
        }

        return annotatedField.withAnnotations(annotationMap);}
}
