package com.neurchi.advisor.common.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.LinkedList;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public abstract class AbstractSerializer extends Module implements Jackson2ObjectMapperBuilderCustomizer {

    private final ObjectMapper objectMapper;
    private final boolean isPretty;
    private final boolean isCompact;

    protected AbstractSerializer(final boolean isCompact) {
        this(isCompact, false);
    }

    protected AbstractSerializer(final boolean isCompact, final boolean isPretty) {

        final List<Module> modules = new LinkedList<>();
        modules.add(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        modules.add(new JavaTimeModule());
        modules.add(this);

        this.isPretty = isPretty;
        this.isCompact = isCompact;

        final Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();
        customize(mapperBuilder);
        mapperBuilder.modules(modules);
//        this.objectMapper = mapperBuilder.build();
        this.objectMapper = new ObjectMapper()
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .setSerializationInclusion(isCompact ? JsonInclude.Include.NON_ABSENT : JsonInclude.Include.ALWAYS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, isPretty)
                .setAnnotationIntrospector(new PropertiesModeAnnotationIntrospector())
                .registerModules(modules);
    }

    protected ObjectMapper objectMapper() {
        return this.objectMapper;
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(ParameterNamesModule.class)
    static class ParameterNamesModuleConfiguration {

        @Bean
        @Primary
        ParameterNamesModule parameterNamesModule() {
            return new ParameterNamesModule(JsonCreator.Mode.PROPERTIES);
        }

    }

    @Override
    public void customize(final Jackson2ObjectMapperBuilder builder) {
        builder
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .visibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                .serializationInclusion(isCompact ? JsonInclude.Include.NON_ABSENT : JsonInclude.Include.ALWAYS)
                .featuresToEnable(MapperFeature.DEFAULT_VIEW_INCLUSION)
                .featuresToDisable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
                .featuresToDisable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToDisable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .annotationIntrospector(new PropertiesModeAnnotationIntrospector());

        if (isPretty) {
            builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);
        }
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
