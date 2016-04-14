package com.github.mrenou.jacksonatic.integration.test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;

import java.io.IOException;

public class CustomStringSerializer extends StdSerializer<String> {

    StringSerializer stringSerializer = new StringSerializer();

    public CustomStringSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        stringSerializer.serialize(value + "_customized", jgen, provider);
    }
}
