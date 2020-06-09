package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import java.time.Instant;

/**
 * Abstract base class for exchange listeners.
 * Performs the basic set up according to the concrete subclass.
 */
public abstract class ExchangeListener implements AutoCloseable {

    private MessageConsumer messageConsumer;
    private Queue queue;

    public ExchangeListener() {

        this.attachToQueue();

        this.registerConsumer();
    }

    /**
     * Closes the queue.
     */
    @Override
    public void close() {
        this.queue().close();
    }

    /**
     * Returns the String name of the exchange this listens to.
     */
    protected abstract String exchangeName();

    /**
     * Filters out unwanted events and dispatches ones of interest.
     * @param type the message type
     * @param textMessage the raw text message being handled
     */
    protected abstract void filteredDispatch(final String type, final String textMessage);

    /**
     * Returns the kinds of messages this listens to.
     */
    protected abstract String[] listensTo();

    protected String queueName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Attaches to the queues this listen to for messages.
     */
    private void attachToQueue() {
        final Exchange exchange =
                Exchange.fanOutInstance(
                        ConnectionSettings.instance(),
                        this.exchangeName(),
                        true);

        this.queue =
                Queue.individualExchangeSubscriberInstance(
                        exchange,
                        this.exchangeName() + "." + this.queueName());
    }

    private Queue queue() {
        return this.queue;
    }

    /**
     * Registers as listener for queue messages and dispatching.
     */
    private void registerConsumer() {
        this.messageConsumer = MessageConsumer.instance(this.queue(), false);

        this.messageConsumer.receiveOnly(
                this.listensTo(),
                new MessageListener(MessageListener.Type.Text) {

                    @Override
                    public void handleMessage(
                            final String type,
                            final String messageId,
                            final Instant timestamp,
                            final String textMessage,
                            final long deliveryTag,
                            final boolean isRedelivery) throws Exception {
                        filteredDispatch(type, textMessage);
                    }
                }
        );
    }
}
