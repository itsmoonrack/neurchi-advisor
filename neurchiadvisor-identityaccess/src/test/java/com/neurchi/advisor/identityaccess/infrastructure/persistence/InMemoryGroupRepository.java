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
import com.neurchi.advisor.identityaccess.domain.model.identity.Group;
import com.neurchi.advisor.identityaccess.domain.model.identity.GroupRepository;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class InMemoryGroupRepository implements GroupRepository, CleanableStore {

    private Map<String, Group> repository;

    public InMemoryGroupRepository() {
        super();

        this.repository = new HashMap<>();
    }

    @Override
    public void add(final Group group) {
        String key = this.keyOf(group);

        if (this.repository().containsKey(key)) {
            throw new IllegalStateException("Duplicate key.");
        }

        this.repository().put(key, group);
    }

    @Override
    public Stream<Group> allGroups(final TenantId tenantId) {
        return this.repository().values().stream().filter(group -> group.tenantId().equals(tenantId));
    }

    @Override
    public Optional<Group> groupNamed(final TenantId tenantId, final String name) {
        if (name.startsWith(Group.ROLE_GROUP_PREFIX)) {
            throw new IllegalArgumentException("May not find internal groups.");
        }

        String key = this.keyOf(tenantId, name);

        return Optional.ofNullable(this.repository().get(key));
    }

    @Override
    public void remove(final Group group) {
        String key = this.keyOf(group);

        this.repository().remove(key);
    }

    @Override
    public void clean() {
        this.repository().clear();
    }

    private String keyOf(final TenantId tenantId, final String name) {
        return tenantId.id() + "#" + name;
    }

    private String keyOf(Group aGroup) {
        return this.keyOf(aGroup.tenantId(), aGroup.name());
    }

    private Map<String,Group> repository() {
        return this.repository;
    }
}
