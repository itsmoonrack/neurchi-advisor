//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.neurchi.advisor.identityaccess.infrastructure.persistence;

import com.neurchi.advisor.common.persistence.CleanableStore;
import com.neurchi.advisor.identityaccess.domain.model.identity.Tenant;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryTenantRepository implements TenantRepository, CleanableStore {

    private Map<String, Tenant> repository;

    public InMemoryTenantRepository() {
        super();

        this.repository = new HashMap<>();
    }

    @Override
    public void add(final Tenant tenant) {
        String key = this.keyOf(tenant);

        if (this.repository().containsKey(key)) {
            throw new IllegalStateException("Duplicate key.");
        }

        this.repository().put(key, tenant);
    }

    @Override
    public TenantId nextIdentity() {
        return new TenantId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public Optional<Tenant> tenantNamed(final String name) {
        for (Tenant tenant : this.repository().values()) {
            if (tenant.name().equals(name)) {
                return Optional.of(tenant);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Tenant> tenantOfId(TenantId aTenantId) {
        return Optional.ofNullable(this.repository().get(this.keyOf(aTenantId)));
    }

    @Override
    public void remove(Tenant aTenant) {
        String key = this.keyOf(aTenant);

        this.repository().remove(key);
    }

    @Override
    public void clean() {
        this.repository().clear();
    }

    private String keyOf(final TenantId tenantId) {
        return tenantId.id();
    }

    private String keyOf(final Tenant tenant) {
        return this.keyOf(tenant.tenantId());
    }

    private Map<String,Tenant> repository() {
        return this.repository;
    }
}
