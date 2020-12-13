package com.neurchi.advisor.common.notification;

public record NotificationLogId(long low, long high) {

    public static String encoded(final NotificationLogId notificationLogId) {
        return notificationLogId == null ? null : notificationLogId.encoded();
    }

    public static NotificationLogId first(final int notificationsPerLog) {
        NotificationLogId id = new NotificationLogId(0, 0);

        return id.next(notificationsPerLog);
    }

    public NotificationLogId(final String notificationLogId) {
        this(notificationLogId.split(","));
    }

    private NotificationLogId(final String[] textIds) {
        this(Long.parseLong(textIds[0]), Long.parseLong(textIds[1]));
    }

    public String encoded() {
        return "" + this.low() + "," + this.high();
    }

    public NotificationLogId next(final int notificationsPerLog) {
        long nextLow = this.high() + 1;

        // ensures a minted id value even though there may
        // not be this many notifications at present
        long nextHigh = nextLow + notificationsPerLog - 1;

        NotificationLogId next = new NotificationLogId(nextLow, nextHigh);

        if (this.equals(next)) {
            next = null;
        }

        return next;
    }

    public NotificationLogId previous(final int notificationsPerLog) {
        long previousLow = Math.max(this.low() - notificationsPerLog, 1);

        long previousHigh = previousLow + notificationsPerLog - 1;

        NotificationLogId previous = new NotificationLogId(previousLow, previousHigh);

        if (this.equals(previous)) {
            previous = null;
        }

        return previous;
    }
}
