package com.neurchi.advisor.subscription.port.adapter.messaging;

import com.neurchi.advisor.common.notification.NotificationReader;
import com.neurchi.advisor.common.port.adapter.messaging.Exchanges;
import com.neurchi.advisor.common.port.adapter.messaging.rabbitmq.ExchangeListener;
import com.neurchi.advisor.subscription.application.group.GroupApplicationService;
import org.springframework.stereotype.Component;

import java.time.Instant;

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

        final String tenantId = reader.eventStringValue("tenantId");
        final String exclusiveOwnerId = reader.eventStringValue("exclusiveOwnerId");
        final String subscriberId = reader.eventStringValue("subscriberId");
        final String groupName = reader.eventStringValue("groupName");
        final Integer groupMemberCount = reader.eventIntegerValue("groupMemberCount");
        final String groupDescription = reader.eventStringValue("groupDescription");
        final Instant groupCreatedOn = reader.eventInstantValue("groupCreatedOn");
        final String groupCoverPhoto = reader.eventStringValue("groupCoverPhoto");

        groupApplicationService
                .provisionGroupWithSubscription(
                        tenantId,
                        exclusiveOwnerId,
                        subscriberId,
                        groupCreatedOn,
                        groupName,
                        groupDescription,
                        groupCoverPhoto);
    }

    @Override
    protected String[] listensTo() {
        return new String[] { "com.neurchi.advisor.subscription.group.CreateExclusiveSubscription" };
    }
}
