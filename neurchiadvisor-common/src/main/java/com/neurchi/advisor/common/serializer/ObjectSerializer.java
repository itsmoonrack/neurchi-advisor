package com.neurchi.advisor.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

public final class ObjectSerializer extends AbstractSerializer {

    private static ObjectSerializer objectSerializer;

    public static synchronized ObjectSerializer instance() {
        if (ObjectSerializer.objectSerializer == null) {
            ObjectSerializer.objectSerializer = new ObjectSerializer();
        }

        return ObjectSerializer.objectSerializer;
    }

    public ObjectSerializer(final boolean isCompact) {
        super(isCompact);
    }

    public ObjectSerializer(final boolean isCompact, final boolean isPretty) {
        super(isCompact, isPretty);
    }

    public String serialize(final Object value) {
        try {
            return this.objectMapper().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize object.", e);
        }
    }

    public void serialize(final OutputStream outputStream, final Object value) {
        try {
            this.objectMapper().writeValue(outputStream, value);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to serialize object.", e);
        }
    }

    public <T> T deserialize(final InputStream source, final Class<T> type) {
        try {
            return this.objectMapper().readValue(source, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize object.", e);
        }
    }

    public <T> T deserialize(final Reader source, final Class<T> type) {
        try {
            return this.objectMapper().readValue(source, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize object.", e);
        }
    }

    public <T> T deserialize(final String content, final Class<T> type) {
        try {
            return this.objectMapper().readValue(content, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to deserialize object.", e);
        }
    }

    private ObjectSerializer() {
        this(false, false);
    }
}
