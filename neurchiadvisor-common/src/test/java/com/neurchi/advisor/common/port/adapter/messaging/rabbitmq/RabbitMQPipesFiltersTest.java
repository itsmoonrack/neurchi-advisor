package com.neurchi.advisor.common.port.adapter.messaging.rabbitmq;

import com.neurchi.advisor.common.notification.Notification;
import com.neurchi.advisor.common.notification.NotificationReader;
import com.neurchi.advisor.common.notification.NotificationSerializer;
import com.neurchi.advisor.common.port.adapter.messaging.AllPhoneNumbersCounted;
import com.neurchi.advisor.common.port.adapter.messaging.AllPhoneNumbersListed;
import com.neurchi.advisor.common.port.adapter.messaging.MatchedPhoneNumbersCounted;
import com.neurchi.advisor.common.port.adapter.messaging.PhoneNumbersMatched;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RabbitMQPipesFiltersTest {

    private final Logger logger = LoggerFactory.getLogger(RabbitMQPipesFiltersTest.class);

    private ExchangeListener matchedPhoneNumberCounter;
    private PhoneNumberExecutive phoneNumberExecutive;
    private ExchangeListener phoneNumberFinder;
    private ExchangeListener totalPhoneNumbersCounter;

    private static final String[] phoneNumbers = new String[] {
            "303-555-1212   John",
            "212-555-1212   Joe",
            "718-555-1212   Zoe",
            "720-555-1212   Manny",
            "312-555-1212   Jerry",
            "303-555-9999   Sally"
    };

    @Test
    public void TestPhoneNumbersCounter() throws Exception {
        final String processId = this.phoneNumberExecutive.start(phoneNumbers);

        Thread.sleep(500);

        final PhoneNumberProcess process =
                this.phoneNumberExecutive
                        .processOfId(processId);

        assertNotNull(process);
        assertEquals(2, process.matchedPhoneNumbers());
        assertEquals(6, process.totalPhoneNumbers());
    }

    @BeforeEach
    protected void setUp() throws Exception {

        phoneNumberExecutive = new PhoneNumberExecutive();
        phoneNumberFinder = new PhoneNumberFinder();
        matchedPhoneNumberCounter = new MatchedPhoneNumbersCounter();
        totalPhoneNumbersCounter = new TotalPhoneNumbersCounter();

    }

    @AfterEach
    protected void tearDown() throws Exception {

        phoneNumberExecutive.close();
        phoneNumberFinder.close();
        matchedPhoneNumberCounter.close();
        totalPhoneNumbersCounter.close();

    }

    private MessageProducer messageProducer() {
        final Exchange exchange =
                Exchange.fanOutInstance(
                        ConnectionSettings.instance(),
                        "PhoneNumberExchange",
                        true);

        return MessageProducer.instance(exchange);
    }

    private void send(final Notification notification) {

        final MessageParameters messageParameters =
                MessageParameters.durableTextParameters(
                        notification.typeName(),
                        Long.toString(notification.notificationId()),
                        notification.occurredOn());

        final String serializedNotification =
                NotificationSerializer.instance().serialize(notification);

        this.messageProducer().send(serializedNotification, messageParameters);
    }

    private static class PhoneNumberProcess {

        private final String id;
        private int matchedPhoneNumbers;
        private int totalPhoneNumbers;

        public PhoneNumberProcess() {

            this.id = UUID.randomUUID().toString().toUpperCase();
            this.matchedPhoneNumbers = -1;
            this.totalPhoneNumbers = -1;
        }

        public boolean isComplete() {
            return this.matchedPhoneNumbers() >= 0 && this.totalPhoneNumbers() >= 0;
        }

        public String id() {
            return this.id;
        }

        public int matchedPhoneNumbers() {
            return this.matchedPhoneNumbers;
        }

        public void setMatchedPhoneNumbers(final int matchedPhoneNumbers) {
            this.matchedPhoneNumbers = matchedPhoneNumbers;
        }

        public int totalPhoneNumbers() {
            return this.totalPhoneNumbers;
        }

        public void setTotalPhoneNumbers(final int totalPhoneNumbers) {
            this.totalPhoneNumbers = totalPhoneNumbers;
        }
    }

    private class PhoneNumberExecutive extends ExchangeListener {

        private final Map<String, PhoneNumberProcess> processes;

        public PhoneNumberExecutive() {
            this.processes = new HashMap<>();
        }

        public PhoneNumberProcess processOfId(final String processId) {
            return this.processes.get(processId);
        }

        public String start(final String[] phoneNumbers) {

            final PhoneNumberProcess process = new PhoneNumberProcess();

            synchronized (this.processes) {
                this.processes.put(process.id, process);
            }

            final String allPhoneNumbers = String.join("\n", phoneNumbers);

            final Notification notification =
                    new Notification(
                            1L,
                            new AllPhoneNumbersListed(
                                    process.id(),
                                    allPhoneNumbers));

            logger.info("Started: " + process.id());

            send(notification);

            return process.id();
        }

        @Override
        protected String exchangeName() {
            return "PhoneNumberExchange";
        }

        @Override
        protected void filteredDispatch(final String type, final String textMessage) {

            final NotificationReader reader = new NotificationReader(textMessage);

            final String processId = reader.eventTextValue("processId");

            final PhoneNumberProcess process = this.processes.get(processId);

            if (reader.typeName().contains("AllPhoneNumbersCounted")) {
                process.setTotalPhoneNumbers(reader.eventIntegerValue("totalPhoneNumbers"));
                logger.info("AllPhoneNumbersCounted...");
            } else if (reader.typeName().contains("MatchedPhoneNumbersCounted")) {
                process.setMatchedPhoneNumbers(reader.eventIntegerValue("matchedPhoneNumbers"));
                logger.info("MatchedPhoneNumbersCounted...");
            }

            if (process.isComplete()) {
                logger.info("Process: " + process.id() + ": " + process.matchedPhoneNumbers() + " of " + process.totalPhoneNumbers() + " phone numbers found.");
            }
        }

        @Override
        protected String[] listensTo() {
            return new String[] {
                    "com.neurchi.advisor.common.port.adapter.messaging.AllPhoneNumbersCounted",
                    "com.neurchi.advisor.common.port.adapter.messaging.MatchedPhoneNumbersCounted",
            };
        }
    }

    private class PhoneNumberFinder extends ExchangeListener {

        @Override
        protected String exchangeName() {
            return "PhoneNumberExchange";
        }

        @Override
        protected void filteredDispatch(final String type, final String textMessage) {

            logger.info("AllPhoneNumbersListed (to match)...");

            final NotificationReader reader = new NotificationReader(textMessage);

            final String allPhoneNumbers = reader.eventTextValue("allPhoneNumbers");

            final String[] allPhoneNumbersToSearch = allPhoneNumbers.split("\n");

            final String foundPhoneNumbers =
                    Stream.of(allPhoneNumbersToSearch)
                            .filter(phoneNumber -> phoneNumber.contains("303-"))
                            .collect(joining("\n"));

            final Notification notification =
                    new Notification(
                            1L,
                            new PhoneNumbersMatched(
                                    reader.eventTextValue("processId"),
                                    foundPhoneNumbers));

            send(notification);
        }

        @Override
        protected String[] listensTo() {
            return new String[] { "com.neurchi.advisor.common.port.adapter.messaging.AllPhoneNumbersListed" };
        }
    }

    private class MatchedPhoneNumbersCounter extends ExchangeListener {

        @Override
        protected String exchangeName() {
            return "PhoneNumberExchange";
        }

        @Override
        protected void filteredDispatch(final String type, final String textMessage) {

            logger.info("PhoneNumbersMatched (to count)...");

            final NotificationReader reader = new NotificationReader(textMessage);

            final String allMatchedPhoneNumbers = reader.eventTextValue("matchedPhoneNumbers");

            final String[] allPhoneNumbersToCount = allMatchedPhoneNumbers.split("\n");

            final Notification notification =
                    new Notification(
                            1L,
                            new MatchedPhoneNumbersCounted(
                                    reader.eventTextValue("processId"),
                                    allPhoneNumbersToCount.length));

            send(notification);
        }

        @Override
        protected String[] listensTo() {
            return new String[] { "com.neurchi.advisor.common.port.adapter.messaging.PhoneNumbersMatched" };
        }
    }

    private class TotalPhoneNumbersCounter extends ExchangeListener {

        @Override
        protected String exchangeName() {
            return "PhoneNumberExchange";
        }

        @Override
        protected void filteredDispatch(final String type, final String textMessage) {

            logger.info("AllPhoneNumbersListed (to total)...");

            final NotificationReader reader = new NotificationReader(textMessage);

            final String allPhoneNumbers = reader.eventTextValue("allPhoneNumbers");

            final String[] allPhoneNumbersToCount = allPhoneNumbers.split("\n");

            final Notification notification =
                    new Notification(
                            1L,
                            new AllPhoneNumbersCounted(
                                    reader.eventTextValue("processId"),
                                    allPhoneNumbersToCount.length));

            send(notification);
        }

        @Override
        protected String[] listensTo() {
            return new String[] { "com.neurchi.advisor.common.port.adapter.messaging.AllPhoneNumbersListed" };
        }
    }
}
