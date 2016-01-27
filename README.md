# Jacksonatic

A fluent api to define [Jackson](https://github.com/FasterXML/jackson) mapping programmatically in order to avoid
polluting domain code with annotations.

## Goal

If you are concerned by the [DDD (Domain Driver Design)](https://en.wikipedia.org/wiki/Domain-driven_design), or just,
the isolation between the business code and technical code, or simply, the overuse of annotations, Jacksonatic can help
you.

[Jackson](https://github.com/FasterXML/jackson) offers you one way to separate the mapping annotations adn the mapped
class with the [mix-in](http://wiki.fasterxml.com/JacksonMixInAnnotations), but you to define one mix-in class by mapped
class.

With Jacksonatic, you can define all your application mapping in one shot with a fluent api. You can add jackson
annotations programmatically on your domain classes in a separated initialization code.

Moreover you have somme extra-feature to facilitate your mapping :
* Map or ignore a field or a method
* Map or ignore a method
* Map or ignore a getter or a setter
* Map a constructor or a static factory for the deserialization (respect encapsulation)
* Auto-detect a constructor or a static factory for the deserialization
* Use the inheritance to propagate the mapping
* Facilitate the polymorphism mapping

## Setup (Maven)

You can add Jacksonatic as a maven dependency of your project. **Jacksonatic requires java 8**.

```xml
<dependency>
  <groupId>org.jacksonatic</groupId>
  <artifactId>jacksonatic</artifactId>
  <version>0.3</version>
</dependency>
```

## Examples

### Add @JsonProperty on a field and @JsonIgnore on a method

```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.annotation.JacksonaticJsonIgnore.jsonIgnore;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;
import static org.jacksonatic.mapping.ClassMapping.type;
import static org.jacksonatic.mapping.FieldMapping.field;
import static org.jacksonatic.mapping.MethodMapping.method;

configureMapping()
    .on(type(Pojo.class)
         .on(field("field1").add(jsonProperty())))
         .on(method("field2").add(jsonIgnore())))
    .registerIn(objectMapper);
```

### Add JsonIgnoreProperties to ignore unknown properties on a class

```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.annotation.JacksonaticJsonIgnoreProperties.jsonIgnoreProperties;
import static org.jacksonatic.mapping.ClassMapping.type;

configureMapping()
    .on(type(Pojo.class)).add(jsonIgnoreProperties().ignoreUnknown())
    .registerIn(objectMapper);
```

### Map a field

```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;

configureMapping()
    .on(type(Pojo.class)
        .mapA("field1"))
    .registerIn(objectMapper);
```

Shortcut to add @JsonProperty on field named "field1".

### Map several types

```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;

configureMapping()
    .on(type(Pojo1.class)
        .mapAll())
    .on(type(Pojo2.class)
        .mapAll())
    .registerIn(objectMapper);
```

Shortcut to add @JsonProperty on all fields for types "Pojo1" and "Pojo2".

### Use inheritance to map several types
```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;

configureMapping()
    .on(type(Object.class)
        .mapAll())
    .registerIn(objectMapper);
```

As all types inherit Object type, so add @JsonProperty on all fields of all types.

### Map all field expect one

```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;

configureMapping()
    .on(type(Pojo.class)
        .mapAll()
        .ignore("field2"))
    .registerIn(objectMapper);
```

Shortcut to add @JsonProperty on all fields and @JsonIgnore on field named "field2".

### Map a getter and a setter

```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;

configureMapping()
    .on(type(Pojo.class)
        .mapGetter("field1")
        .mapSetter("field1"))
    .registerIn(objectMapper);
```

Shortcut to add @JsonProperty on methods "getField1" and "setField1".

### Map a constructor

```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;

configureMapping()
    .on(type(Pojo.class)
        .withConstructor(matchType(String.class), matchType(Integer.class)))
    .registerIn(objectMapper);
```

Shortcut to add @JsonCreator on tje constructor with parametric signature (String, Integer).

### Map a static factory

```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;
import static org.jacksonatic.mapping.ParameterCriteria.matchType;

configureMapping()
    .on(type(Pojo.class)
        .onStaticFactory("build" matchType(String.class), matchType(Integer.class)))
    .registerIn(objectMapper);
```

Shortcut to add @JsonCreator on the static method named "build" with parametric signature (String, Integer). Add also
@JsonProperty on parameters using class fields to set the json property names.

### Map constructor or static factory automatically
```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;

configureMapping()
    .on(type(Pojo.class)
        .withAConstructorOrStaticFactory())
    .registerIn(objectMapper);
```

Try to find a constructor with a parametric signature having same types (or less) than the types of class fields,
ignoring static fields. If no constructor is found with all field types, try to find a static factory with the same
algorithm. The constructor is used if a constructor and a static factory match same field types.

### Map polymorphism
```java
import static org.jacksonatic.Jacksonatic.configureMapping;
import static org.jacksonatic.mapping.ClassMapping.type;

configureMapping()
    .on(type(PojoParent.class)
        .fieldForTypeName("type")
            .addNamedSubType(PojoChild1.class, "CHILD1")
            .addNamedSubType(PojoChild2.class, "CHILD2")
            .withAConstructorOrStaticFactory())
    .registerIn(objectMapper);
```

Shortcut to add @JsonTypeInfo and @JsonSubTypes on parent class, and also @JsonTypeName on children classes.


