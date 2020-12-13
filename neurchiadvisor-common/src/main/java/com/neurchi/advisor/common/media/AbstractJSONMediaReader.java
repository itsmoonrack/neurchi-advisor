package com.neurchi.advisor.common.media;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

    public ArrayNode array(final String... keys) {
        JsonNode node = this.navigateTo(this.representation(), keys);
        return node instanceof ArrayNode ? (ArrayNode) node : null;
    }

    public Boolean booleanValue(final String... keys) {
        String textValue = this.stringValue(keys);

        return textValue == null ? null : Boolean.parseBoolean(textValue);
    }

    public Instant instantValue(final String... keys) {
        String textValue = this.stringValue(keys);

        return textValue == null ? null : Instant.parse(textValue);
    }

    public Integer integerValue(final String... keys) {
        String textValue = this.stringValue(keys);

        return textValue == null ? null : Integer.valueOf(textValue);
    }

    public Long longValue(final String... keys) {
        String textValue = this.stringValue(keys);

        return textValue == null ? null : Long.valueOf(textValue);
    }

    public String stringValue(final String... keys) {
        return this.stringValue(this.representation(), keys);
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

    protected String stringValue(final JsonNode startingJsonNode, final String... keys) {
        String stringValue = this.navigateTo(startingJsonNode, keys).asText();
        return stringValue.length() == 0 || stringValue.equals(NullNode.instance.asText()) ? null : stringValue;
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
