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

import org.jacksonatic.exception.ClassBuilderNotFoundException;
import org.jacksonatic.exception.FieldNotFoundException;
import org.jacksonatic.exception.MethodNotFoundException;
import org.jacksonatic.internal.mapping.ClassMappingByProcessType;

/**
 * Allowing to define jackson class mapping in a programmatic way.
 */
public interface ClassMapping<T> {

    static <T> ClassMapping<T> type(Class<T> clazz) {
        return new ClassMappingByProcessType<>(clazz);
    }

    /**
     * Start a class mapping for the given type only for serialization
     *
     * @param clazz the class to configure on serialization
     * @return the current class mapping
     */
    static <T> ClassMapping<T> onSerializationOf(Class<T> clazz) {
        return type(clazz).onSerialization();
    }

    /**
     * Start a class mapping for the given type only for deserialization
     *
     * @param clazz the class to configure on deserialization
     * @return the current class mapping
     */
    static <T> ClassMapping<T> onDeserialisationOf(Class<T> clazz) {
        return type(clazz).onDeserialization();
    }

    /**
     * Next class mapping instructions will be only for serialization
     *
     * @return the current class mapping
     */
    ClassMapping<T> onSerialization();

    /**
     * Next class mapping instructions will be only for deserialization
     *
     * @return the current class mapping
     */
    ClassMapping<T> onDeserialization();

    /**
     * add a field mapping
     *
     * @param fieldMapping the field mapping to add
     * @return the current class mapping
     * @throws FieldNotFoundException if the field doesn't exist in the current class
     */
    ClassMapping<T> on(FieldMapping fieldMapping) throws FieldNotFoundException;

    /**
     * add a method mapping
     *
     * @param methodMapping the method mapping to add
     * @return the current class mapping
     * @throws MethodNotFoundException if the method doesn't exist in the current class
     */
    ClassMapping<T> on(MethodMapping methodMapping) throws MethodNotFoundException;

    /**
     * Map all fields in the current class mapping
     *
     * @return the current class mapping
     */
    ClassMapping<T> mapAll();

    /**
     * Map the named field in the current class mapping
     *
     * @param fieldName the name of the field
     * @return the current class mapping
     * @throws FieldNotFoundException if the field doesn't exist in the current class
     */
    ClassMapping<T> map(String fieldName) throws FieldNotFoundException;

    /**
     * Map the named field with another name
     *
     * @param fieldName    the name of the field
     * @param jsonProperty the new name
     * @return the current class mapping
     * @throws FieldNotFoundException if the field doesn't exist in the current class
     */
    ClassMapping<T> map(String fieldName, String jsonProperty) throws FieldNotFoundException;

    /**
     * Ignore the named field
     *
     * @param fieldName the name of the field
     * @return try to find a constructor or a static factory with the same signature described in
     * @throws FieldNotFoundException if the field doesn't exist in the current class
     */
    ClassMapping<T> ignore(String fieldName) throws FieldNotFoundException;

    /**
     * Map the named getter
     *
     * @param fieldName the name of the field used by the getter
     * @return the current class mapping
     * @throws MethodNotFoundException if the method doesn't exist in the current class
     */
    ClassMapping<T> mapGetter(String fieldName) throws MethodNotFoundException;

    /**
     * Map the named getter with another name
     *
     * @param fieldName    the name of the field used by the getter
     * @param jsonProperty the new name
     * @return the current class mapping
     * @throws MethodNotFoundException if the method doesn't exist in the current class
     */
    ClassMapping<T> mapGetter(String fieldName, String jsonProperty) throws MethodNotFoundException;

    /**
     * Map the named setter, ignoring the parametric signature
     *
     * @param fieldName the name of the field used by the setter
     * @return the current class mapping
     * @throws MethodNotFoundException if the method doesn't exist in the current class
     */
    ClassMapping<T> mapSetter(String fieldName) throws MethodNotFoundException;

    /**
     * Map the named setter with another name
     *
     * @param fieldName    the name of the field used by the setter
     * @param jsonProperty the new name
     * @return the current class mapping
     * @throws MethodNotFoundException if the method doesn't exist in the current class
     */
    ClassMapping<T> mapSetter(String fieldName, String jsonProperty) throws MethodNotFoundException;

    /**
     * Map the named setter with the parametric signature
     *
     * @param fieldName      the name of the field used by the setter
     * @param parameterTypes the parametric signature
     * @return the current class mapping
     */
    ClassMapping<T> mapSetter(String fieldName, Class<?>... parameterTypes) throws MethodNotFoundException;

    /**
     * Map the named setter, with the parametric signature, with another name
     *
     * @param fieldName      the name of the field used by the setter
     * @param jsonProperty   the new name
     * @param parameterTypes the parametric signature
     * @return the current class mapping
     * @throws MethodNotFoundException if the method doesn't exist in the current class
     */
    ClassMapping<T> mapSetter(String fieldName, String jsonProperty, Class<?>... parameterTypes);

    /**
     * ignore the named getter
     *
     * @param fieldName the name of the field used by the getter
     * @return the current class mapping
     * @throws MethodNotFoundException if the method doesn't exist in the current class
     */
    ClassMapping<T> ignoreGetter(String fieldName);

    /**
     * ignore the named setter, ignoring the parametric signature
     *
     * @param fieldName the name of the field used by the getter
     * @return the current class mapping
     * @throws MethodNotFoundException if the method doesn't exist in the current class
     */
    ClassMapping<T> ignoreSetter(String fieldName);

    /**
     * ignore the named setter, with the parametric signature
     *
     * @param fieldName the name of the field used by the setter
     * @param parameterTypes the parametric signature
     * @return the current class mapping
     * @throws MethodNotFoundException if the method doesn't exist in the current class
     */
    ClassMapping<T> ignoreSetter(String fieldName, Class<?>... parameterTypes);

    /**
     * Will try to guess a constructor or a static factory for the object creation
     * <p>
     * Try to find a constructor with a parametric signature having same types (or less) than the types
     * of class fields, ignoring static fields. If no constructor is found with all field types, try to find a static
     * factory with the same algorithm. The constructor is used if a constructor and a static factory match same field types
     *
     * @return the current class mapping
     * @throws ClassBuilderNotFoundException if neither a constructor nor a static factory is found
     */
    ClassMapping<T> withAConstructorOrStaticFactory() throws ClassBuilderNotFoundException;

    /**
     * Will try to map a constructor with these parameters for the object creation
     *
     * @return the current class mapping
     * @throws ClassBuilderNotFoundException if no constructor is found for the given parameters
     */
    ClassMapping<T> withConstructor(ParameterCriteria... parameterCriteriaList) throws ClassBuilderNotFoundException;

    /**
     * Will try to map the named static factory with these parameters for the object creation
     *
     * @return the current class mapping
     * @throws ClassBuilderNotFoundException if no static factory is found for the given name and parameters
     */
    ClassMapping<T> onStaticFactory(String methodName, ParameterCriteria... parameterCriteriaList) throws ClassBuilderNotFoundException;

    /**
     * Will try to map a static factory with these parameters for the object creation
     *
     * @return the current class mapping
     * @throws ClassBuilderNotFoundException if no static factory is found for the given parameters
     */
    ClassMapping<T> onStaticFactory(ParameterCriteria... parameterCriteriaList) throws ClassBuilderNotFoundException;

    /**
     * Define the field use to store the type name
     *
     * @param fieldName the name of the field
     * @return the current class mapping
     */
    ClassMapping<T> fieldForTypeName(String fieldName);

    /**
     * Define the type name
     *
     * @param name the name of the type
     * @return the current class mapping
     */
    ClassMapping<T> typeName(String name);

    /**
     * Define a subtype with the given type name
     *
     * @param name the name of the type
     * @return the current class mapping
     */
    ClassMapping<T> addNamedSubType(Class<? extends T> subType, String name);

}
