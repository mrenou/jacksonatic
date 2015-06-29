package org.jacksonatic;

import java.util.List;

public class ConstructorMapping {

    private List<TypedParameter<?>> parameters;

    public ConstructorMapping(List<TypedParameter<?>> parameters) {
        this.parameters = parameters;
    }

    public List<TypedParameter<?>> getParameters() {
        return parameters;
    }
}
