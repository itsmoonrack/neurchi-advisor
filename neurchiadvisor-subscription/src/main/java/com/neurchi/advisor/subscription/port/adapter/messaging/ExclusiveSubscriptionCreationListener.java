package com.neurchi.advisor.subscription.port.adapter.messaging;

import com.neurchi.advisor.common.notification.NotificationReader;
import com.neurchi.advisor.common.port.adapter.messaging.Exchanges;
import com.neurchi.advisor.common.port.adapter.messaging.rabbitmq.ExchangeListener;
import com.neurchi.advisor.subscription.application.group.GroupApplicationService;
import org.springframework.stereotype.Component;

@Component
public class ExclusiveSubscriptionCreationListener extends ExchangeListener {

    private final GroupApplicationService groupApplicationService;

    ExclusiveSubscriptionCreationListener(final GroupApplicationService groupApplicationService) {
        this.groupApplicationService = groupApplicationService;
    }

    @Override
    protected String exchangeName() {
        return Exchanges.SubscriptionExchangeName;
    }

    @Override
    protected void filteredDispatch(final String type, final String textMessage) {
        final NotificationReader reader = new NotificationReader(textMessage);

        final String tenantId = reader.eventTextValue("tenantId");
        final String exclusiveOwnerId = reader.eventTextValue("exclusiveOwnerId");
        final String creatorId = reader.eventTextValue("creatorId");
        final String administratorId = reader.eventTextValue("administratorId");

        groupApplicationService
                .startExclusiveSubscriptionWithGroup(
                        tenantId,
                        exclusiveOwnerId,
                        creatorId,
                        administratorId);
    }

    @Override
    protected String[] listensTo() {
        return new String[] { "com.neurchi.advisor.subscription.group.CreateExclusiveSubscription" };
    }
}
