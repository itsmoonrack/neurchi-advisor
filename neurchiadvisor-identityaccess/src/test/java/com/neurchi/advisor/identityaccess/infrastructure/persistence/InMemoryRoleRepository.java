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
import com.neurchi.advisor.identityaccess.domain.model.access.Role;
import com.neurchi.advisor.identityaccess.domain.model.access.RoleRepository;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class InMemoryRoleRepository implements RoleRepository, CleanableStore {

    private Map<String, Role> repository;

    public InMemoryRoleRepository() {
        super();

        this.repository = new HashMap<>();
    }

    @Override
    public void add(final Role role) {
        String key = this.keyOf(role);

        if (this.repository().containsKey(key)) {
            throw new IllegalStateException("Duplicate key.");
        }

        this.repository().put(key, role);
    }

    @Override
    public Stream<Role> allRoles(final TenantId tenantId) {
        return this.repository().values().stream().filter(role -> role.tenantId().equals(tenantId));
    }

    @Override
    public void remove(final Role role) {
        String key = this.keyOf(role);

        this.repository().remove(key);
    }

    @Override
    public Optional<Role> roleNamed(final TenantId tenantId, final String roleName) {
        return Optional.ofNullable(this.repository().get(this.keyOf(tenantId, roleName)));
    }

    @Override
    public void clean() {
        this.repository().clear();
    }

    private String keyOf(final TenantId tenantId, final String roleName) {
        return tenantId.id() + "#" + roleName;
    }

    private String keyOf(Role aRole) {
        return this.keyOf(aRole.tenantId(), aRole.name());
    }

    private Map<String,Role> repository() {
        return this.repository;
    }
}
