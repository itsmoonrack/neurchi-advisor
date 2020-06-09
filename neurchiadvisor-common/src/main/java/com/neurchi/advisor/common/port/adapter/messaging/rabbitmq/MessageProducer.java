package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import com.neurchi.advisor.common.port.adapter.messaging.MessageException;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;

/**
 * A message producer, which facilitates sending messages to a BrokerChannel.
 * A BrokerChanel may be either an Exchange or a Queue.
 */
public class MessageProducer implements AutoCloseable {

    private BrokerChannel brokerChannel;

    /**
     * Creates a new instance of a MessageProducer.
     * @param brokerChannel the BrokerChannel where messages are to be sent
     */
    public static MessageProducer instance(final BrokerChannel brokerChannel) {
        return new MessageProducer(brokerChannel);
    }

    /**
     * Closes the MessageProducer, which closes its broker channel.
     */
    @Override
    public void close() {
        this.brokerChannel().close();
    }

    /**
     * Returns this producer after sending a text message to its channel.
     * This is a producer ignorance way to use either an exchange or
     * a queue channel without requiring it to pass specific parameters.
     * @param textMessage the text message to send
     */
    public MessageProducer send(final String textMessage) {
        try {
            this.brokerChannel().channel().basicPublish(
                    this.brokerChannel().exchangeName(),
                    this.brokerChannel().queueName(),
                    this.textDurability(),
                    textMessage.getBytes());

        } catch (IOException e) {
            throw new MessageException("Unable to send message to channel.", e);
        }
        return this;
    }

    /**
     * Returns this producer after sending a text message to its channel
     * with messageParameters as the message basic properties.
     * This is a producer ignorance way to use either an exchange or
     * a queue channel without requiring it to pass specific parameters.
     * @param textMessage the text message to send
     * @param messageParameters the MessageParameters
     */
    public MessageProducer send(
            final String textMessage,
            final MessageParameters messageParameters) {

        this.check(messageParameters);

        try {
            this.brokerChannel().channel().basicPublish(
                    this.brokerChannel().exchangeName(),
                    this.brokerChannel().queueName(),
                    messageParameters.properties(),
                    textMessage.getBytes());

        } catch (IOException e) {
            throw new MessageException("Unable to send message to channel.", e);
        }
        return this;
    }

    /**
     * Returns this producer after sending a text message to its channel
     * with routingKey and messageParameters. This is a producer ignorance
     * way to use an exchange without requiring it to pass the exchange name.
     * @param routingKey the routing key
     * @param textMessage the text message to send
     * @param messageParameters the MessageParameters
     */
    public MessageProducer send(
            final String routingKey,
            final String textMessage,
            final MessageParameters messageParameters) {

        this.check(messageParameters);

        try {
            this.brokerChannel().channel().basicPublish(
                    this.brokerChannel().exchangeName(),
                    routingKey,
                    messageParameters.properties(),
                    textMessage.getBytes());

        } catch (IOException e) {
            throw new MessageException("Unable to send message to channel.", e);
        }
        return this;
    }

    /**
     * Returns this producer after sending a text message to its channel
     * with an exchange and a routingKey.
     * @param exchange the name of the exchange
     * @param routingKey the routing key
     * @param textMessage the text message to send
     * @param messageParameters the MessageParameters
     */
    public MessageProducer send(
            final String exchange,
            final String routingKey,
            final String textMessage,
            final MessageParameters messageParameters) {

        this.check(messageParameters);

        try {
            this.brokerChannel().channel().basicPublish(
                    exchange,
                    routingKey,
                    messageParameters.properties(),
                    textMessage.getBytes());

        } catch (IOException e) {
            throw new MessageException("Unable to send message to channel.", e);
        }
        return this;
    }

    /**
     * Returns this producer after sending a binary message to its channel.
     * This is a producer ignorance way to use either an exchange or
     * a queue channel without requiring it to pass specific parameters.
     * @param binaryMessage the binary message to send
     */
    public MessageProducer send(final byte[] binaryMessage) {
        try {
            this.brokerChannel().channel().basicPublish(
                    this.brokerChannel().exchangeName(),
                    this.brokerChannel().queueName(),
                    this.textDurability(),
                    binaryMessage);

        } catch (IOException e) {
            throw new MessageException("Unable to send message to channel.", e);
        }
        return this;
    }

    /**
     * Returns this producer after sending a binary message to its channel
     * with messageParameters as the message basic properties.
     * This is a producer ignorance way to use either an exchange or
     * a queue channel without requiring it to pass specific parameters.
     * @param binaryMessage the binary message to send
     * @param messageParameters the MessageParameters
     */
    public MessageProducer send(
            final byte[] binaryMessage,
            final MessageParameters messageParameters) {

        this.check(messageParameters);

        try {
            this.brokerChannel().channel().basicPublish(
                    this.brokerChannel().exchangeName(),
                    this.brokerChannel().queueName(),
                    messageParameters.properties(),
                    binaryMessage);

        } catch (IOException e) {
            throw new MessageException("Unable to send message to channel.", e);
        }
        return this;
    }

    /**
     * Returns this producer after sending a binary message to its channel
     * with routingKey and messageParameters. This is a producer ignorance
     * way to use an exchange without requiring it to pass the exchange name.
     * @param routingKey the routing key
     * @param binaryMessage the binary message to send
     * @param messageParameters the MessageParameters
     */
    public MessageProducer send(
            final String routingKey,
            final byte[] binaryMessage,
            final MessageParameters messageParameters) {

        this.check(messageParameters);

        try {
            this.brokerChannel().channel().basicPublish(
                    this.brokerChannel().exchangeName(),
                    routingKey,
                    messageParameters.properties(),
                    binaryMessage);

        } catch (IOException e) {
            throw new MessageException("Unable to send message to channel.", e);
        }
        return this;
    }

    /**
     * Returns this producer after sending a binary message to its channel
     * with an exchange and a routingKey.
     * @param exchange the name of the exchange
     * @param routingKey the routing key
     * @param binaryMessage the binary message to send
     * @param messageParameters the MessageParameters
     */
    public MessageProducer send(
            final String exchange,
            final String routingKey,
            final byte[] binaryMessage,
            final MessageParameters messageParameters) {

        this.check(messageParameters);

        try {
            this.brokerChannel().channel().basicPublish(
                    exchange,
                    routingKey,
                    messageParameters.properties(),
                    binaryMessage);

        } catch (IOException e) {
            throw new MessageException("Unable to send message to channel.", e);
        }
        return this;
    }

    protected MessageProducer(final BrokerChannel brokerChannel) {
        this.setBrokerChannel(brokerChannel);
    }

    protected BrokerChannel brokerChannel() {
        return this.brokerChannel;
    }

    private void setBrokerChannel(final BrokerChannel brokerChannel) {
        this.brokerChannel = brokerChannel;
    }

    /**
     * Checks aMessageParameters for validity.
     */
    private void check(final MessageParameters aMessageParameters) {
        if (this.brokerChannel().isDurable()) {
            if (!aMessageParameters.isDurable()) {
                throw new IllegalArgumentException("MessageParameters must be durable.");
            }
        } else {
            if (aMessageParameters.isDurable()) {
                throw new IllegalArgumentException("MessageParameters must not be durable.");
            }
        }
    }

    private BasicProperties binaryDurability() {
        BasicProperties durability = null;
        if (this.brokerChannel().isDurable()) {
            durability = MessageProperties.PERSISTENT_BASIC;
        }
        return durability;
    }

    private BasicProperties textDurability() {
        BasicProperties durability = null;
        if (this.brokerChannel().isDurable()) {
            durability = MessageProperties.PERSISTENT_TEXT_PLAIN;
        }
        return durability;
    }
}
