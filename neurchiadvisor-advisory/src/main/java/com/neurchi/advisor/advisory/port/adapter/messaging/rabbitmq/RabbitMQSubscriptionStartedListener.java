package com.neurchi.advisor.advisory.port.adapter.messaging.rabbitmq;

import com.neurchi.advisor.advisory.application.group.GroupApplicationService;
import com.neurchi.advisor.advisory.application.group.InitiateSubscriptionCommand;
import com.neurchi.advisor.advisory.port.adapter.messaging.GroupSubscriptionExclusiveOwnerId;
import com.neurchi.advisor.common.notification.NotificationReader;
import com.neurchi.advisor.common.port.adapter.messaging.Exchanges;
import com.neurchi.advisor.common.port.adapter.messaging.rabbitmq.ExchangeListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSubscriptionStartedListener extends ExchangeListener {

    private final GroupApplicationService groupApplicationService;

    RabbitMQSubscriptionStartedListener(final GroupApplicationService groupApplicationService) {
        this.groupApplicationService = groupApplicationService;
    }

    @Override
    protected String exchangeName() {
        return Exchanges.SubscriptionExchangeName;
    }

    @Override
    protected void filteredDispatch(final String type, final String textMessage) {
        final NotificationReader reader = new NotificationReader(textMessage);

        final String ownerId = reader.eventStringValue("exclusiveOwner");

        GroupSubscriptionExclusiveOwnerId
                .fromEncodedId(ownerId)
                .ifPresent(exclusiveOwnerId -> {

                    final String tenantId = reader.eventStringValue("tenant.id");
                    final String subscriptionId = reader.eventStringValue("subscriptionId.id");

                    this.groupApplicationService()
                            .initiateSubscription(
                                    new InitiateSubscriptionCommand(
                                            tenantId,
                                            exclusiveOwnerId.id(),
                                            subscriptionId));
                });
    }

    @Override
    protected String[] listensTo() {
        return new String[] { "com.neurchi.advisor.subscription.domain.model.group.SubscriptionStarted" };
    }

    private GroupApplicationService groupApplicationService() {
        return this.groupApplicationService;
    }
}
