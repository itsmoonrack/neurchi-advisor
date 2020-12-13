package com.neurchi.advisor.common.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public abstract class AbstractSerializer {

    private final ObjectMapper objectMapper;

    protected AbstractSerializer(final boolean isCompact) {
        this(isCompact, false);
    }

    protected AbstractSerializer(final boolean isCompact, final boolean isPretty) {
        this.objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .setSerializationInclusion(isCompact ? JsonInclude.Include.NON_ABSENT : JsonInclude.Include.ALWAYS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, isPretty)
                .setAnnotationIntrospector(new PropertiesModeAnnotationIntrospector())
                .deactivateDefaultTyping()
                .registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES))
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module());
    }

    protected ObjectMapper objectMapper() {
        return this.objectMapper;
    }

    private static class PropertiesModeAnnotationIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public JsonCreator.Mode findCreatorAnnotation(final MapperConfig<?> config, final Annotated a) {
            return JsonCreator.Mode.PROPERTIES;
        }
    }
}
