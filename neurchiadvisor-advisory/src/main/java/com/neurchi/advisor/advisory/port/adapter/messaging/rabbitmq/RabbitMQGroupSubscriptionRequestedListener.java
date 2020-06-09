package com.neurchi.advisor.advisory.port.adapter.messaging.rabbitmq;

import com.neurchi.advisor.advisory.application.group.GroupApplicationService;
import com.neurchi.advisor.advisory.application.group.StartSubscriptionInitiationCommand;
import com.neurchi.advisor.advisory.port.adapter.messaging.GroupSubscriptionExclusiveOwnerId;
import com.neurchi.advisor.common.notification.NotificationReader;
import com.neurchi.advisor.common.port.adapter.messaging.Exchanges;
import com.neurchi.advisor.common.port.adapter.messaging.rabbitmq.*;
import com.neurchi.advisor.common.serializer.PropertiesSerializer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Properties;

@Component
public class RabbitMQGroupSubscriptionRequestedListener extends ExchangeListener {

    private static final String COMMAND =
            "com.neurchi.advisor.subscription.group.CreateExclusiveSubscription";

    private final GroupApplicationService groupApplicationService;

    RabbitMQGroupSubscriptionRequestedListener(final GroupApplicationService groupApplicationService) {
        this.groupApplicationService = groupApplicationService;
    }

    @Override
    protected String exchangeName() {
        return Exchanges.AdvisoryExchangeName;
    }

    @Override
    protected void filteredDispatch(final String type, final String textMessage) {
        final NotificationReader reader = new NotificationReader(textMessage);

        if (!reader.eventBooleanValue("requestingSubscription")) {
            return;
        }

        final String tenantId = reader.eventTextValue("tenantId.id");
        final String groupId = reader.eventTextValue("group.id");

        this.groupApplicationService
                .startSubscriptionInitiation(
                        new StartSubscriptionInitiationCommand(
                                tenantId,
                                groupId));

        final Properties parameters = this.parametersFrom(reader);
        final PropertiesSerializer serializer = PropertiesSerializer.instance();
        final String serialization = serializer.serialize(parameters);
        final String commandId = this.commandIdFrom(parameters);

        try (final MessageProducer messageProducer = this.messageProducer()) {
            messageProducer.send(
                    serialization,
                    MessageParameters
                            .durableTextParameters(
                                    COMMAND,
                                    commandId,
                                    Instant.now()));
        }
    }

    @Override
    protected String[] listensTo() {
        return new String[] {
                "com.neurchi.advisor.advisory.domain.model.group.GroupCreated",
                "com.neurchi.advisor.advisory.domain.model.group.GroupSubscriptionRequested"
        };
    }

    private String commandIdFrom(final Properties properties) {
        return properties.getProperty("tenantId") + ":" + properties.getProperty("groupId");
    }

    private MessageProducer messageProducer() {
        final Exchange exchange =
                Exchange.fanOutInstance(
                        ConnectionSettings.instance(),
                        Exchanges.SubscriptionExchangeName,
                        true);

        return MessageProducer.instance(exchange);
    }

    private Properties parametersFrom(final NotificationReader reader) {
        final Properties properties = new Properties();

        properties.put("command", COMMAND);

        properties.put("tenantId", reader.eventTextValue("tenantId.id"));

        final GroupSubscriptionExclusiveOwnerId exclusiveOwnerId =
                new GroupSubscriptionExclusiveOwnerId(
                        reader.eventTextValue("groupId.id"));

        properties.put("exclusiveOwnerId", exclusiveOwnerId.encoded());

        properties.put("creatorId", ""); // TODO

        properties.put("administratorId", ""); // TODO

        return properties;
    }
}
