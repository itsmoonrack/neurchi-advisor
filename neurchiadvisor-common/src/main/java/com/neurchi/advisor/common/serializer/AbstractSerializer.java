package com.neurchi.advisor.common.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractSerializer extends Module {

    private final ObjectMapper objectMapper;

    protected AbstractSerializer(final boolean isCompact) {
        this(isCompact, false, false);
    }

    protected AbstractSerializer(final boolean isCompact, final boolean isPretty) {
        this(isCompact, isPretty, false);
    }

    protected AbstractSerializer(final boolean isCompact, final boolean isPretty, final boolean isXml) {

        final List<Module> modules = new LinkedList<>();
        modules.add(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        modules.add(new JavaTimeModule());
        modules.add(this);

        if (isXml) {
            this.objectMapper = new XmlMapper();
        } else {
            this.objectMapper = new ObjectMapper();
            this.objectMapper.setAnnotationIntrospector(new PropertiesModeAnnotationIntrospector());
        }

        this.objectMapper
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .setSerializationInclusion(isCompact ? JsonInclude.Include.NON_ABSENT : JsonInclude.Include.ALWAYS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, isPretty)
                .registerModules(modules);
    }

    protected ObjectMapper objectMapper() {
        return this.objectMapper;
    }

    @Override
    public String getModuleName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(final SetupContext context) {
        // no-op
    }

    private static class PropertiesModeAnnotationIntrospector extends JacksonAnnotationIntrospector {
        @Override
        public JsonCreator.Mode findCreatorAnnotation(final MapperConfig<?> config, final Annotated a) {
            return JsonCreator.Mode.PROPERTIES;
        }
    }
}
