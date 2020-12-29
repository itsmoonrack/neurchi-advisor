package com.neurchi.advisor.common.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.neurchi.advisor.common.serializer.AbstractSerializer;

import java.io.IOException;
import java.time.Instant;

public class NotificationSerializer extends AbstractSerializer {

    private static NotificationSerializer notificationSerializer;

    public static NotificationSerializer instance() {
        if (NotificationSerializer.notificationSerializer == null) {
            NotificationSerializer.notificationSerializer = new NotificationSerializer();
        }
        return NotificationSerializer.notificationSerializer;
    }

    public String serialize(final Notification notification) {
        try {
            return this.objectMapper().writeValueAsString(notification);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize notification.", e);
        }
    }

    private NotificationSerializer(final boolean isCompact, final boolean isPretty) {
        super(isCompact, isPretty);
    }

    private NotificationSerializer(final boolean isCompact) {
        super(isCompact);
    }

    private NotificationSerializer() {
        this(false, false);
    }

    private static class NotificationModule extends Module {

        @Override
        public String getModuleName() {
            return "NotificationSerializer";
        }

        @Override
        public Version version() {
            return Version.unknownVersion();
        }

        @Override
        public void setupModule(final SetupContext context) {
            SimpleSerializers simpleSerializers = new SimpleSerializers();
            simpleSerializers.addSerializer(new NotificationStdSerializer());
            context.addSerializers(simpleSerializers);

            SimpleDeserializers simpleDeserializers = new SimpleDeserializers();
            simpleDeserializers.addDeserializer(Notification.class, new NotificationStdDeserializer());
            context.addDeserializers(simpleDeserializers);

//            context.setMixInAnnotations(Identity.class, IdentityMixIn.class);
//            context.setMixInAnnotations(DomainEvent.class, DomainEventMixIn.class);
        }
    }

    private interface IdentityMixIn {
        @JsonValue
        String id();
    }

    private interface DomainEventMixIn {
        int eventVersion();
        @JsonIgnore
        Instant occurredOn();
    }

    private static class NotificationStdSerializer extends StdSerializer<Notification> {

        protected NotificationStdSerializer() {
            super(Notification.class);
        }

        @Override
        public void serialize(final Notification value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
            if (gen instanceof ToXmlGenerator generator) {
                generator.writeFieldName("notification");
                generator.writeStartObject();
                generator.setNextIsAttribute(true);
                generator.writeStringField("type", value.typeName());
                generator.writeObjectField("occurredOn", value.occurredOn());
                generator.writeNumberField("id", value.notificationId());
                {
                    generator.setNextIsAttribute(false);
                    generator.writeFieldName(value.typeName());
                    generator.writeStartObject();
                    generator.writeObject(value.event());
                    generator.writeEndObject();
                }
                generator.writeEndObject();
            } else {
                gen.writeObject(value);
            }
        }
    }

    private static class NotificationStdDeserializer extends StdDeserializer<Notification> {

        protected NotificationStdDeserializer() {
            super(Notification.class);
        }

        @Override
        public Notification deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return null;
        }
    }
}
