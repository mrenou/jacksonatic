package org.jacksonatic.mapping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.jacksonatic.annotation.JacksonaticJsonIgnore.jsonIgnore;
import static org.jacksonatic.annotation.JacksonaticJsonProperty.jsonProperty;

public interface PropertyMapper<T> extends HasAnnotations<T> {

    /**
     * map the field
     *
     * @return
     */
    default T map() {
        add(jsonProperty());
        return builder();
    }

    /**
     * map the field with the given name
     *
     * @return
     */
    default T mapTo(String jsonProperty) {
        add(jsonProperty(jsonProperty));
        return builder();
    }

    /**
     * ignore the field
     *
     * @return
     */
    default T ignore() {
        add(jsonIgnore());
        return builder();
    }

    default boolean isMapped() {
        return getAnnotations().containsKey(JsonProperty.class) && !getAnnotations().containsKey(JsonIgnore.class);
    }

}
