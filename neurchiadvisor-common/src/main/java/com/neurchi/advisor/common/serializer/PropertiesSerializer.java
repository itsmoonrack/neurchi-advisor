package com.neurchi.advisor.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Properties;

public final class PropertiesSerializer extends AbstractSerializer {

    private static PropertiesSerializer propertiesSerializer;

    public static synchronized PropertiesSerializer instance() {
        if (PropertiesSerializer.propertiesSerializer == null) {
            PropertiesSerializer.propertiesSerializer = new PropertiesSerializer(false);
        }

        return PropertiesSerializer.propertiesSerializer;
    }

    public PropertiesSerializer(boolean isCompact) {
        this(isCompact, false);
    }

    public PropertiesSerializer(boolean isCompact, boolean isPretty) {
        super(isCompact, isPretty);
    }

    public String serialize(final Properties properties) {
        ObjectNode object = this.objectMapper().createObjectNode();

        for (Object keyObj : properties.keySet()) {
            String key = keyObj.toString();
            String value = properties.getProperty(key);
            object.put(key, value);
        }

        try {
            return this.objectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
