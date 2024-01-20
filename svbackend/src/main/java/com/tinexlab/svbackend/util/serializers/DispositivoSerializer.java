package com.tinexlab.svbackend.util.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.tinexlab.svbackend.model.entity.Dispositivo;

import java.io.IOException;

public class DispositivoSerializer extends JsonSerializer<Dispositivo> {
    @Override
    public void serialize(Dispositivo dispositivo, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", dispositivo.getId());
        jsonGenerator.writeStringField("nombre", dispositivo.getNombre());
        jsonGenerator.writeEndObject();
    }
}
