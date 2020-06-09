package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import com.rabbitmq.client.AMQP.BasicProperties;

import java.time.Instant;
import java.util.Date;

public class MessageParameters {

    private BasicProperties properties;

    public static MessageParameters durableTextParameters(
            final String type,
            final String messageId,
            final Instant timestamp) {

        final BasicProperties properties =
                new BasicProperties(
                        "text/plain",
                        null,
                        null,
                        2, // persistent
                        0,
                        null,
                        null,
                        null,
                        messageId,
                        Date.from(timestamp),
                        type,
                        null,
                        null,
                        null);

        return new MessageParameters(properties);
    }

    public static MessageParameters textParameters(
            final String type,
            final String messageId,
            final Instant timestamp) {

        final BasicProperties properties =
                new BasicProperties(
                        "text/plain",
                        null,
                        null,
                        1, // non-persistent
                        0,
                        null,
                        null,
                        null,
                        messageId,
                        Date.from(timestamp),
                        type,
                        null,
                        null,
                        null);

        return new MessageParameters(properties);
    }

    public boolean isDurable() {
        final Integer deliveryMode = this.properties().getDeliveryMode();

        return deliveryMode != null && deliveryMode == 2;
    }

    protected BasicProperties properties() {
        return this.properties;
    }

    private MessageParameters(final BasicProperties properties) {
        this.setProperties(properties);
    }

    private void setProperties(final BasicProperties properties) {
        this.properties = properties;
    }
}
