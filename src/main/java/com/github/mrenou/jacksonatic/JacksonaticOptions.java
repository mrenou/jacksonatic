package com.github.mrenou.jacksonatic;

/**
 * Created by morgan on 14/04/16.
 */
public class JacksonaticOptions {

    private boolean typeChecking = true;

    public static Builder options() {
        return new Builder();
    }

    public boolean typeChecking() {
        return typeChecking;
    }

    public static class Builder {

        private JacksonaticOptions jacksonaticOptions = new JacksonaticOptions();

        public Builder disableTypeChecking() {
            jacksonaticOptions.typeChecking = false;
            return this;
        }

        public JacksonaticOptions build() {
            return jacksonaticOptions;
        }
    }
}
