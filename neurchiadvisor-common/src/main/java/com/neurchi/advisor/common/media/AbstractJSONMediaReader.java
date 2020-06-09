package com.neurchi.advisor.common.media;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.neurchi.advisor.common.serializer.AbstractSerializer;

import java.time.Instant;

public abstract class AbstractJSONMediaReader {

    private JsonNode representation;
    private JsonReader reader;

    public AbstractJSONMediaReader(final String jsonRepresentation) {
        this.setReader(new JsonReader(false, false));
        this.setRepresentation(this.reader().deserialize(jsonRepresentation));
    }

    public AbstractJSONMediaReader(final JsonNode representationObject) {
        this.setRepresentation(representationObject);
    }

    public Instant instantValue(final String... keys) {
        String textValue = this.textValue(keys);

        return textValue == null ? null : Instant.parse(textValue);
    }

    public Long longValue(final String... keys) {
        String textValue = this.textValue(keys);

        return textValue == null ? null : Long.valueOf(textValue);
    }

    public String textValue(final String... keys) {
        return this.textValue(this.representation(), keys);
    }

    protected JsonNode navigateTo(final JsonNode node, String... keys) {
        if (keys.length == 0) {
            throw new IllegalArgumentException("Must specify one or more keys.");
        } else if (keys.length == 1 && (keys[0].startsWith("/") || keys[0].contains("."))) {
            keys = this.parsePath(keys[0]);
        }

        int keyIndex = 1;

        JsonNode element = node.path(keys[0]);

        if (!element.isNull() && element.isObject()) {
            for ( ; !element.isNull() && element.isObject() && keyIndex < keys.length; ++keyIndex) {

                element = element.path(keys[keyIndex]);
            }
        }

        if (!element.isNull() && !element.isMissingNode()) {
            if (keyIndex != keys.length) {
                throw new IllegalArgumentException("Last name must reference a simple value.");
            }
        }

        return element;
    }

    protected JsonNode representation() {
        return this.representation;
    }

    protected String textValue(final JsonNode startingJsonNode, final String... keys) {
        String textValue = this.navigateTo(startingJsonNode, keys).asText();
        return textValue.length() == 0 || textValue.equals(NullNode.instance.asText()) ? null : textValue;
    }

    private void setRepresentation(final JsonNode representation) {
        this.representation = representation;
    }

    private String[] parsePath(final String propertiesPath) {
        boolean startsWithSlash = propertiesPath.startsWith("/");

        String[] propertyNames = null;

        if (startsWithSlash) {
            propertyNames = propertiesPath.substring(1).split("/");
        } else {
            propertyNames = propertiesPath.split("\\.");
        }

        return propertyNames;
    }

    private JsonReader reader() {
        return this.reader;
    }

    private void setReader(final JsonReader reader) {
        this.reader = reader;
    }

    private static class JsonReader extends AbstractSerializer {

        protected JsonReader(final boolean isCompact, final boolean isPretty) {
            super(isCompact, isPretty);
        }

        protected JsonNode deserialize(final String content) {
            try {
                return this.objectMapper().readTree(content);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
