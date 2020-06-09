package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import com.neurchi.advisor.common.port.adapter.messaging.MessageException;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;

import java.io.IOException;

public class Queue extends BrokerChannel {

    /**
     * Creates a new instance of a Queue with a given name. The underlying
     * queue is non-durable, non-exclusive, and not auto-deleted.
     */
    public static Queue instance(
            final ConnectionSettings connectionSettings,
            final String name) {
        return new Queue(connectionSettings, name, false, false, false);
    }

    /**
     * Creates a new instance of a Queue with a given name. The underlying
     * queue durability, exclusivity, and deletion properties are specified by
     * explicit parameters.
     */
    public static Queue instance(
            final ConnectionSettings connectionSettings,
            final String name,
            final boolean isDurable,
            final boolean isExclusive,
            final boolean isAutoDeleted) {
        return new Queue(connectionSettings, name, isDurable, isExclusive, isAutoDeleted);
    }

    /**
     * Creates a new instance of a Queue with a given name. The underlying
     * queue is durable, is non-exclusive, and not auto-deleted.
     */
    public static Queue durableInstance(
            final ConnectionSettings connectionSettings,
            final String name) {
        return new Queue(connectionSettings, name, true, false, false);
    }

    /**
     * Creates a new instance of a Queue with a given name. The underlying
     * queue is durable, exclusive, and not auto-deleted.
     */
    public static Queue durableExclusiveInstance(
            final ConnectionSettings connectionSettings,
            final String name) {
        return new Queue(connectionSettings, name, true, true, false);
    }

    /**
     * Creates a new instance of a Queue that is bound to an exchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of the exchange are reused. The Queue is
     * uniquely named by the server, non-durable, exclusive, and auto-deleted.
     * This Queue style best works as a temporary fan-out subscriber.
     */
    public static Queue exchangeSubscriberInstance(final Exchange exchange) {

        final Queue queue = new Queue(exchange, "", false, true, true);

        try {
            queue.channel().queueBind(queue.name(), exchange.name(), "");
        } catch (IOException e) {
            throw new MessageException("Unable to bind the queue and exchange.", e);
        }

        return queue;
    }

    /**
     * Creates a new instance of a Queue that is bound to an exchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of the exchange are reused. The Queue is
     * uniquely named by the server, non-durable, exclusive, and auto-deleted.
     * The queue is bound to all routing keys in routingKeys. This Queue
     * style best works as a temporary direct or topic subscriber.
     */
    public static Queue exchangeSubscriberInstance(
            final Exchange exchange,
            final String[] routingKeys) {

        final Queue queue = new Queue(exchange, "", false, true, true);

        try {
            for (String routingKey : routingKeys) {
                queue.channel().queueBind(queue.name(), exchange.name(), routingKey);
            }
        } catch (IOException e) {
            throw new MessageException("Unable to bind the queue and exchange.", e);
        }

        return queue;
    }

    /**
     * Creates a new instance of a Queue that is bound to an exchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of the exchange are reused. The Queue is
     * named by a name, unless it is empty, in which case the name is generated
     * by the broker. The queue is bound to all routing keys in routingKeys,
     * or to no routing key if routingKeys is empty. The Queue has the
     * qualities specified by isDurable, isExclusive, isAutoDeleted. This
     * factory is provided for ultimate flexibility in case no other
     * exchange-queue binder factories fit the needs of the client.
     */
    public static Queue exchangeSubscriberInstance(
            final Exchange exchange,
            final String name,
            final String[] routingKeys,
            final boolean isDurable,
            final boolean isExclusive,
            final boolean isAutoDeleted) {

        final Queue queue = new Queue(exchange, name, isDurable, isExclusive, isAutoDeleted);

        try {
            if (routingKeys.length == 0) {
                queue.channel().queueBind(queue.name(), exchange.name(), "");
            } else {
                for (String routingKey : routingKeys) {
                    queue.channel().queueBind(queue.name(), exchange.name(), routingKey);
                }
            }
        } catch (IOException e) {
            throw new MessageException("Unable to bind the queue and exchange.", e);
        }

        return queue;
    }

    /**
     * Creates a new instance of a Queue that is bound to an exchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of the exchange are reused. The Queue is
     * named by name, which must be provided and should be unique to the
     * individual subscriber. The Queue is durable, non-exclusive, and
     * is not auto-deleted. This Queue style best works as a durable
     * fan-out exchange subscriber.
     */
    public static Queue individualExchangeSubscriberInstance(
            final Exchange exchange,
            final String name) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("An individual subscriber must be named.");
        }

        final Queue queue = new Queue(exchange, name, true, false, false);

        try {
            queue.channel().queueBind(queue.name(), exchange.name(), "");
        } catch (IOException e) {
            throw new MessageException("Unable to bind the queue and exchange.", e);
        }

        return queue;
    }

    /**
     * Creates a new instance of a Queue that is bound to an exchange, and
     * is ready to participate as an exchange subscriber (pub/sub). The
     * connection and channel of the exchange are reused. The Queue is
     * named by name, which must be provided and should be unique to the
     * individual subscriber. The queue is bound to all routing keys in
     * routingKeys. The Queue is durable, non-exclusive, and is not
     * auto-deleted. This Queue style best works as a durable direct or
     * topic exchange subscriber.
     */
    public static Queue individualExchangeSubscriberInstance(
            final Exchange exchange,
            final String name,
            final String[] routingKeys) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("An individual subscriber must be named.");
        }

        final Queue queue = new Queue(exchange, name, true, false, false);

        try {
            for (String routingKey : routingKeys) {
                queue.channel().queueBind(queue.name(), exchange.name(), routingKey);
            }
        } catch (IOException e) {
            throw new MessageException("Unable to bind the queue and exchange.", e);
        }

        return queue;
    }

    protected Queue(
            final ConnectionSettings connectionSettings,
            final String name,
            final boolean isDurable,
            final boolean isExclusive,
            final boolean isAutoDeleted) {

        super(connectionSettings);

        this.setDurable(isDurable);

        try {
            final DeclareOk result =
                    this.channel().queueDeclare(
                            name,
                            isDurable,
                            isExclusive,
                            isAutoDeleted,
                            null);

            this.setName(result.getQueue());

        } catch (IOException e) {
            throw new MessageException("Unable to create/open the queue.", e);
        }
    }

    protected Queue(
            final BrokerChannel brokerChannel,
            final String name,
            final boolean isDurable,
            final boolean isExclusive,
            final boolean isAutoDeleted) {

        super(brokerChannel);

        this.setDurable(isDurable);

        try {
            final DeclareOk result =
                    this.channel().queueDeclare(
                            name,
                            isDurable,
                            isExclusive,
                            isAutoDeleted,
                            null);

            this.setName(result.getQueue());

        } catch (IOException e) {
            throw new MessageException("Unable to create/open the queue.", e);
        }
    }

    @Override
    protected boolean isQueue() {
        return true;
    }
}
