package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import java.time.Instant;

public abstract class MessageListener {

    private Type type;

    public enum Type {
        Binary {
            @Override
            public boolean isBinaryListener() {
                return true;
            }
        },

        Text {
            @Override
            public boolean isTextListener() {
                return true;
            }
        };

        public boolean isBinaryListener() {
            return false;
        }

        public boolean isTextListener() {
            return false;
        }
    }

    public MessageListener(final Type type) {
        this.setType(type);
    }

    /**
     * Handles a binaryMessage. If any MessageException is thrown by
     * an implementor its isRetry() is examined and, if true, the
     * message being handled will be nack's and re-queued. Otherwise,
     * if its isRetry() is false the message will be rejected/failed
     * (not re-queued). If any other Exception is thrown the message
     * will be considered not handled and it rejected/failed.
     */
    public void handleMessage(
            final String type,
            final String messageId,
            final Instant timestamp,
            final byte[] binaryMessage,
            final long deliveryTag,
            final boolean isRedelivery) throws Exception {

        throw new UnsupportedOperationException("Must be implemented.");
    }

    /**
     * Handles a textMessage. If any MessageException is thrown by
     * an implementor its isRetry() is examined and, if true, the
     * message being handled will be nack's and re-queued. Otherwise,
     * if its isRetry() is false the message will be rejected/failed
     * (not re-queued). If any other Exception is thrown the message
     * will be considered not handled and it rejected/failed.
     */
    public void handleMessage(
            final String type,
            final String messageId,
            final Instant timestamp,
            final String textMessage,
            final long deliveryTag,
            final boolean isRedelivery) throws Exception {

        throw new UnsupportedOperationException("Must be implemented.");
    }

    public Type type() {
        return this.type;
    }

    private void setType(final Type type) {
        this.type = type;
    }
}
