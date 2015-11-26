package org.jacksonatic.mapping;

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
    default T mapTo(String mappedName) {
        add(jsonProperty(mappedName));
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

}
