package com.neurchi.advisor.common.domain.model;

import com.neurchi.advisor.common.AssertionConcern;

import java.io.Serializable;

public class IdentifiedDomainObject extends AssertionConcern implements Serializable {

    private long id;

    protected IdentifiedDomainObject() {
        this.id = -1;
    }

    protected long id() {
        return this.id;
    }
}
