package com.neurchi.advisor.common.media;

import com.fasterxml.jackson.databind.JsonNode;

public class RepresentationReader extends AbstractJSONMediaReader {

    public RepresentationReader(final String jsonRepresentation) {
        super(jsonRepresentation);
    }

    public RepresentationReader(final JsonNode representationObject) {
        super(representationObject);
    }
}
