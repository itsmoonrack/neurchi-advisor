package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import com.neurchi.advisor.common.port.adapter.messaging.MessageException;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class BrokerChannel {

    private Channel channel;
    private Connection connection;
    private boolean durable;
    private String host;
    private String name;

    public String host() {
        return this.host;
    }

    public String name() {
        return this.name;
    }

    protected BrokerChannel(final ConnectionSettings connectionSettings) {
        this(connectionSettings, null);
    }

    protected BrokerChannel(final ConnectionSettings connectionSettings, final String name) {

        final ConnectionFactory factory =
                this.configureConnectionFactoryUsing(connectionSettings);

        this.setName(name);

        try {

            this.setConnection(factory.newConnection());

            this.setChannel(this.connection().createChannel());

        } catch (IOException | TimeoutException e) {
            throw new MessageException("Unable to create/open the queue.", e);
        }
    }

    protected BrokerChannel(final BrokerChannel brokerChannel) {
        this(brokerChannel, null);
    }

    protected BrokerChannel(final BrokerChannel brokerChannel, final String name) {

        this.setHost(brokerChannel.host());
        this.setName(name);
        this.setConnection(brokerChannel.connection());
        this.setChannel(brokerChannel.channel());
    }

    protected Channel channel() {
        return this.channel;
    }

    protected void close() {

        // RabbitMQ doesn't guarantee that if isOpen()
        // answers true that close() will work because
        // another client may be racing to close the
        // same process and/or components. so here just
        // attempt to close, catch and ignore, and move
        // on to next steps is the recommended approach.
        //
        // for the purpose here, the isOpen() checks prevent
        // closing a shared channel and connection that is
        // shared by a subscriber exchange and queue.

        try {
            if (this.channel() != null && this.channel().isOpen()) {
                this.channel().close();
            }
        } catch (Throwable e) {
            // fall through
        }

        this.setChannel(null);
        this.setConnection(null);
    }

    protected ConnectionFactory configureConnectionFactoryUsing(
            final ConnectionSettings connectionSettings) {

        final ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(connectionSettings.hostName());

        if (connectionSettings.hasPort()) {
            factory.setPort(connectionSettings.port());
        }

        factory.setVirtualHost(connectionSettings.virtualHost());

        if (connectionSettings.hasUserCredentials()) {
            factory.setUsername(connectionSettings.username());
            factory.setPassword(connectionSettings.password());
        }

        return factory;
    }

    protected boolean isDurable() {
        return this.durable;
    }

    protected void setDurable(final boolean durable) {
        this.durable = durable;
    }

    protected boolean isExchange() {
        return false;
    }

    protected String exchangeName() {
        return this.isExchange() ? this.name() : "";
    }

    protected boolean isQueue() {
        return false;
    }

    protected String queueName() {
        return this.isQueue() ? this.name() : "";
    }

    protected void setName(final String name) {
        this.name = name;
    }

    private void setChannel(final Channel channel) {
        this.channel = channel;
    }

    private Connection connection() {
        return this.connection;
    }

    private void setConnection(final Connection connection) {
        this.connection = connection;
    }

    private void setHost(final String host) {
        this.host = host;
    }
}
