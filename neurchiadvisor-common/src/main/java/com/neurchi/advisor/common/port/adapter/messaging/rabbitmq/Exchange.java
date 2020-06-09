package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import com.neurchi.advisor.common.port.adapter.messaging.MessageException;

import java.io.IOException;

public class Exchange extends BrokerChannel {

    private String type;

    /**
     * Creates a new instance of a direct Exchange with a given name. The
     * underlying exchange has the isDurable quality, and is not auto-deleted.
     */
    public static Exchange directInstance(
            final ConnectionSettings connectionSettings,
            final String name,
            final boolean isDurable) {

        return new Exchange(connectionSettings, name, "direct", isDurable);
    }

    /**
     * Creates a new instance of a fan-out Exchange with a given name. The
     * underlying exchange has the isDurable quality, and is not auto-deleted.
     */
    public static Exchange fanOutInstance(
            final ConnectionSettings connectionSettings,
            final String name,
            final boolean isDurable) {

        return new Exchange(connectionSettings, name, "fanout", isDurable);
    }

    /**
     * Creates a new instance of a headers Exchange with a given name. The
     * underlying exchange has the isDurable quality, and is not auto-deleted.
     */
    public static Exchange headersInstance(
            final ConnectionSettings connectionSettings,
            final String name,
            final boolean isDurable) {

        return new Exchange(connectionSettings, name, "headers", isDurable);
    }

    /**
     * Creates a new instance of a topic Exchange with a given name. The
     * underlying exchange has the isDurable quality, and is not auto-deleted.
     */
    public static Exchange topicInstance(
            final ConnectionSettings connectionSettings,
            final String name,
            final boolean isDurable) {

        return new Exchange(connectionSettings, name, "topic", isDurable);
    }

    protected Exchange(
            final ConnectionSettings connectionSettings,
            final String name,
            final String type,
            final boolean isDurable) {

        super(connectionSettings, name);

        this.setDurable(isDurable);

        this.setType(type);

        try {
            this.channel().exchangeDeclare(name, type, isDurable);
        } catch (IOException e) {
            throw new MessageException("Unable to create/open the exchange.", e);
        }
    }

    @Override
    protected boolean isExchange() {
        return true;
    }

    protected String type() {
        return this.type;
    }

    private void setType(String type) {
        this.type = type;
    }
}
