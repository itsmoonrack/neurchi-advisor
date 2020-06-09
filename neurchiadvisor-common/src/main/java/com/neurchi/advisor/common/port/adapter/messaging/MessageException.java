package com.neurchi.advisor.common.port.adapter.messaging;

public class MessageException extends RuntimeException {

    private boolean retry;

    public MessageException(final String message, final Throwable cause, final boolean isRetry) {
        super(message, cause);

        this.retry = isRetry;
    }

    public MessageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageException(final String message, final boolean isRetry) {
        super(message);

        this.retry = isRetry;
    }

    public boolean isRetry() {
        return this.retry;
    }
}
