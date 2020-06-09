package com.neurchi.advisor.advisory.application.group;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GroupApplicationService {

    @Transactional
    public void initiateSubscription(final InitiateSubscriptionCommand command) {

    }

    @Transactional
    public void startSubscriptionInitiation(final StartSubscriptionInitiationCommand command) {

    }
}
