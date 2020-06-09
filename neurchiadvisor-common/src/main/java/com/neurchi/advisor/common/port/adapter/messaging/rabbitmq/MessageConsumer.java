package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import com.neurchi.advisor.common.port.adapter.messaging.MessageException;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MessageConsumer {

    private boolean autoAcknowledged;
    private boolean closed;
    private Set<String> messageTypes;
    private Queue queue;
    private String tag;

    /**
     * Creates a new auto-acknowledged MessageConsumer, which means all
     * messages received are automatically considered acknowledged as
     * received from the broker.
     */
    public static MessageConsumer autoAcknowledgedInstance(final Queue queue) {
        return MessageConsumer.instance(queue, true);
    }

    /**
     * Creates a new MessageConsumer with manual acknowledgment.
     */
    public static MessageConsumer instance(final Queue queue) {
        return new MessageConsumer(queue, false);
    }

    /**
     * Creates a new MessageConsumer with acknowledgment managed per
     * isAutoAcknowledged.
     */
    public static MessageConsumer instance(
            final Queue queue,
            final boolean isAutoAcknowledged) {
        return new MessageConsumer(queue, isAutoAcknowledged);
    }

    /**
     * Closes this MessageConsumer, which closes the queue.
     */
    public void close() {
        this.setClosed(true);

        this.queue().close();
    }

    public boolean isClosed() {
        return this.closed;
    }

    /**
     * Ensure an equalization of message distribution
     * across all consumers of this queue.
     */
    public void equalizeMessageDistribution() {
        try {
            this.queue().channel().basicQos(1);
        } catch (IOException e) {
            throw new MessageException("Cannot equalize distribution.", e);
        }
    }

    /**
     * Receives all messages on a separate thread and dispatches
     * them to a messageListener until it is closed or until the
     * broker is shut down.
     * @param messageListener the MessageListener that handles messages
     */
    public void receiveAll(final MessageListener messageListener) {
        this.receiveFor(messageListener);
    }

    /**
     * Receives only messages of types included in messageTypes
     * on a separate thread and dispatches them to messageListener
     * until it is closed or until the broker is shut down. The type
     * must be included in the message's basic properties. If the
     * message's type is null, the message is filtered out.
     * @param messageTypes the String[] indicating filtered message types
     * @param messageListener the MessageListener that handles messages
     */
    public void receiveOnly(
            final String[] messageTypes,
            final MessageListener messageListener) {
        String[] filterOutAllBut = messageTypes;

        if (filterOutAllBut == null) {
            filterOutAllBut = new String[0];
        }
        this.setMessageTypes(new HashSet<>(Arrays.asList(filterOutAllBut)));

        this.receiveFor(messageListener);
    }

    /**
     * Returns the tag, which was produced by the broker.
     */
    public String tag() {
        return this.tag;
    }

    protected MessageConsumer(
            final Queue queue,
            final boolean isAutoAcknowledged) {

        this.setMessageTypes(new HashSet<>());

        this.setQueue(queue);

        this.setAutoAcknowledged(isAutoAcknowledged);
    }

    private boolean isAutoAcknowledged() {
        return this.autoAcknowledged;
    }

    private void setAutoAcknowledged(final boolean isAutoAcknowledged) {
        this.autoAcknowledged = isAutoAcknowledged;
    }

    private void setClosed(final boolean closed) {
        this.closed = closed;
    }

    protected Queue queue() {
        return this.queue;
    }

    private Set<String> messageTypes() {
        return this.messageTypes;
    }

    /**
     * Registers a messageListener with the channel indirectly using
     * a DispatchingConsumer.
     * @param messageListener the MessageListener
     */
    private void receiveFor(final MessageListener messageListener) {
        final Queue queue = this.queue();
        final Channel channel = queue.channel();

        try {
            final String tag =
                    channel.basicConsume(
                            queue.name(),
                            this.isAutoAcknowledged(),
                            new DispatchingConsumer(channel, messageListener));

            this.setTag(tag);

        } catch (IOException e) {
            throw new MessageException("Unable to initiate consumer.", e);
        }
    }

    private void setMessageTypes(Set<String> aMessageTypes) {
        this.messageTypes = aMessageTypes;
    }

    private void setQueue(final Queue queue) {
        this.queue = queue;
    }

    private void setTag(final String tag) {
        this.tag = tag;
    }

    private class DispatchingConsumer extends DefaultConsumer {

        private MessageListener messageListener;

        public DispatchingConsumer(final Channel channel, final MessageListener messageListener) {
            super(channel);

            this.setMessageListener(messageListener);
        }

        @Override
        public void handleDelivery(
                final String consumerTag,
                final Envelope envelope,
                final AMQP.BasicProperties properties,
                final byte[] body) throws IOException {

            if (!isClosed()) {
                handle(this.messageListener(), new Delivery(envelope, properties, body));
            }

            if (isClosed()) {
                queue().close();
            }
        }

        @Override
        public void handleShutdownSignal(
                final String consumerTag,
                final ShutdownSignalException signal) {

            close();
        }

        private void handle(
                final MessageListener messageListener,
                final Delivery delivery) {
            try {
                if (this.filteredMessageType(delivery)) {
                    ;
                } else if (messageListener.type().isBinaryListener()) {
                    messageListener
                            .handleMessage(
                                    delivery.getProperties().getType(),
                                    delivery.getProperties().getMessageId(),
                                    delivery.getProperties().getTimestamp().toInstant(),
                                    delivery.getBody(),
                                    delivery.getEnvelope().getDeliveryTag(),
                                    delivery.getEnvelope().isRedeliver());
                } else if (messageListener.type().isTextListener()) {
                    messageListener
                            .handleMessage(
                                    delivery.getProperties().getType(),
                                    delivery.getProperties().getMessageId(),
                                    delivery.getProperties().getTimestamp().toInstant(),
                                    new String(delivery.getBody()),
                                    delivery.getEnvelope().getDeliveryTag(),
                                    delivery.getEnvelope().isRedeliver());
                }

                this.ack(delivery);
            } catch (MessageException e) {
                this.nack(delivery, e.isRetry());
            } catch (Throwable e) {
                this.nack(delivery, false);
            }
        }

        private void ack(final Delivery delivery) {
            try {
                if (!isAutoAcknowledged()) {
                    this.getChannel().basicAck(
                            delivery.getEnvelope().getDeliveryTag(),
                            false);
                }
            } catch (IOException e) {
                // fall through
            }
        }

        private void nack(final Delivery delivery, final boolean isRetry) {
            try {
                if (!isAutoAcknowledged()) {
                    this.getChannel().basicNack(
                            delivery.getEnvelope().getDeliveryTag(),
                            false,
                            isRetry);
                }
            } catch (IOException e) {
                // fall through
            }
        }

        private boolean filteredMessageType(final Delivery delivery) {

            final Set<String> filteredMessageTypes = messageTypes();

            if (!filteredMessageTypes.isEmpty()) {
                final String messageType = delivery.getProperties().getType();

                if (messageType == null || !filteredMessageTypes.contains(messageType)) {
                    return true;
                }
            }

            return false;
        }

        private MessageListener messageListener() {
            return this.messageListener;
        }

        private void setMessageListener(final MessageListener messageListener) {
            this.messageListener = messageListener;
        }
    }
}
