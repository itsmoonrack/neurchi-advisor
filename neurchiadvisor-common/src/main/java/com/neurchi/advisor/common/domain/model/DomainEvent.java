package com.neurchi.advisor.common.domain.model;

import java.time.LocalDateTime;

public interface DomainEvent {

    int eventVersion();

    LocalDateTime occurredOn();

}
