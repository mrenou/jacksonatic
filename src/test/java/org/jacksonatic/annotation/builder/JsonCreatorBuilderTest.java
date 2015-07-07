package org.jacksonatic.annotation.builder;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import org.assertj.core.api.StrictAssertions;
import org.jacksonatic.mapping.ConstructorMapping;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jacksonatic.mapping.ConstructorMapping.mapConstructor;
import static org.jacksonatic.mapping.ConstructorMapping.mapStaticFactory;
import static org.jacksonatic.mapping.ParameterMatcher.match;

public class JsonCreatorBuilderTest {

    private final List<AnnotatedConstructor> constructors = AnnotatedClass.construct(Pojo.class, null, null).getConstructors();

    private final List<AnnotatedMethod> staticMethods = AnnotatedClass.construct(Pojo.class, null, null).getStaticMethods();

    static class Pojo {

        public Pojo(String arg1, Integer arg2) {

        }

        public Pojo(String arg1) {

        }

        public static Pojo newPojo(String arg1, Integer arg2) {
            return null;
        }

        public static Pojo newPojo(String arg1) {
            return null;
        }

    }

    @Test
    public void should_match_constructor_with_one_param() {
        AnnotatedWithParams annotatedWithParams = constructors.get(0);
        ConstructorMapping constructorMapping = mapConstructor(Pojo.class, asList(match(String.class, "field1")));

        final boolean hasToBuild = new JsonCreatorBuilder().hasToBuild(annotatedWithParams, constructorMapping);

        assertThat(hasToBuild).isTrue();
    }

    @Test
    public void should_not_match_constructor_with_one_param() {
        AnnotatedWithParams annotatedWithParams = constructors.get(0);
        ConstructorMapping constructorMapping = mapConstructor(Pojo.class, asList(match(String.class, "field1"), match(Integer.class, "field2")));

        final boolean hasToBuild = new JsonCreatorBuilder().hasToBuild(annotatedWithParams, constructorMapping);

        assertThat(hasToBuild).isFalse();
    }

    @Test
    public void should_match_constructor_with_two_params() {
        AnnotatedWithParams annotatedWithParams = constructors.get(1);
        ConstructorMapping constructorMapping = mapConstructor(Pojo.class, asList(match(String.class, "field1"), match(Integer.class, "field2")));

        final boolean hasToBuild = new JsonCreatorBuilder().hasToBuild(annotatedWithParams, constructorMapping);

        assertThat(hasToBuild).isTrue();
    }

    @Test
    public void should_not_match_constructor_with_two_params() {
        AnnotatedWithParams annotatedWithParams = constructors.get(1);
        ConstructorMapping constructorMapping = mapConstructor(Pojo.class, asList(match(String.class, "field1")));

        final boolean hasToBuild = new JsonCreatorBuilder().hasToBuild(annotatedWithParams, constructorMapping);

        assertThat(hasToBuild).isFalse();
    }

    @Test
    public void should_match_unnamed_static_method_with_one_param() {
        AnnotatedWithParams annotatedWithParams = staticMethods.get(0);
        ConstructorMapping constructorMapping = mapStaticFactory(Pojo.class, asList(match(String.class, "field1")));

        final boolean hasToBuild = new JsonCreatorBuilder().hasToBuild(annotatedWithParams, constructorMapping);

        assertThat(hasToBuild).isTrue();
    }

    @Test
    public void should_not_match_unnamed_static_method_with_one_param() {
        AnnotatedWithParams annotatedWithParams = staticMethods.get(0);
        ConstructorMapping constructorMapping = mapStaticFactory(Pojo.class, asList(match(Integer.class, "field1")));

        final boolean hasToBuild = new JsonCreatorBuilder().hasToBuild(annotatedWithParams, constructorMapping);

        assertThat(hasToBuild).isFalse();
    }

    @Test
    public void should_match_named_static_method_with_one_param() {
        AnnotatedWithParams annotatedWithParams = staticMethods.get(0);
        ConstructorMapping constructorMapping = mapStaticFactory(Pojo.class, "newPojo", asList(match(String.class, "field1")));

        final boolean hasToBuild = new JsonCreatorBuilder().hasToBuild(annotatedWithParams, constructorMapping);

        assertThat(hasToBuild).isTrue();
    }

    @Test
    public void should_not_match_named_static_method_with_one_param() {
        AnnotatedWithParams annotatedWithParams = staticMethods.get(0);
        ConstructorMapping constructorMapping = mapStaticFactory(Pojo.class, "newPojos", asList(match(String.class, "field1")));

        final boolean hasToBuild = new JsonCreatorBuilder().hasToBuild(annotatedWithParams, constructorMapping);

        assertThat(hasToBuild).isFalse();
    }

    @Test
    public void should_build_method_annotation() {
        AnnotatedWithParams annotatedWithParams = constructors.get(0);
        ConstructorMapping constructorMapping = mapConstructor(Pojo.class, asList(match(String.class, "field1")));

        Annotation annotation = new JsonCreatorBuilder().buildMethodAnnotation(annotatedWithParams, constructorMapping);

        assertThat(annotation).isInstanceOf(JsonCreator.class);
        StrictAssertions.assertThat(((JsonCreator) annotation).mode()).isEqualTo(JsonCreator.Mode.DEFAULT);
    }

    @Test
    public void should_build_param_annotation() {
        ConstructorMapping constructorMapping = mapConstructor(Pojo.class, asList(match(String.class, "toto1"), match(Integer.class, "toto2")));

        Annotation annotationParam1 = new JsonCreatorBuilder().buildParamAnnotation(0, constructorMapping.getParameters().get(0));
        Annotation annotationParam2 = new JsonCreatorBuilder().buildParamAnnotation(1, constructorMapping.getParameters().get(1));

        assertThat(annotationParam1).isInstanceOf(JsonProperty.class);
        StrictAssertions.assertThat(((JsonProperty) annotationParam1).value()).isEqualTo("toto1");
        assertThat(annotationParam2).isInstanceOf(JsonProperty.class);
        StrictAssertions.assertThat(((JsonProperty) annotationParam2).value()).isEqualTo("toto2");
    }

}