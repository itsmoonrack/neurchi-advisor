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
import com.neurchi.advisor.identityaccess.domain.model.identity.FullName;
import com.neurchi.advisor.identityaccess.domain.model.identity.TenantId;
import com.neurchi.advisor.identityaccess.domain.model.identity.User;
import com.neurchi.advisor.identityaccess.domain.model.identity.UserRepository;

import java.util.*;
import java.util.stream.Stream;

public class InMemoryUserRepository implements UserRepository, CleanableStore {

    private Map<String, User> repository;

    public InMemoryUserRepository() {
        super();

        this.repository = new HashMap<>();
    }

    @Override
    public void add(final User user) {
        String key = this.keyOf(user);

        if (this.repository().containsKey(key)) {
            throw new IllegalStateException("Duplicate key.");
        }

        this.repository().put(key, user);
    }

    @Override
    public Stream<User> allSimilarlyNamedUsers(
            final TenantId tenantId,
            final String firstNamePrefix,
            final String lastNamePrefix) {

        Collection<User> users = new ArrayList<>();

        for (User user : this.repository().values()) {
            if (user.tenantId().equals(tenantId)) {
                FullName name = user.person().name();
                if (name.firstName().toLowerCase().startsWith(firstNamePrefix.toLowerCase())) {
                    if (name.lastName().toLowerCase().startsWith(lastNamePrefix.toLowerCase())) {
                        users.add(user);
                    }
                }
            }
        }

        return users.stream();
    }

    @Override
    public void remove(final User user) {
        String key = this.keyOf(user);

        this.repository().remove(key);
    }

    @Override
    public Optional<User> userWithUsername(final TenantId tenantId, final String username) {
        for (User user : this.repository().values()) {
            if (user.tenantId().equals(tenantId)) {
                if (user.username().equals(username)) {
                    return Optional.of(user);
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void clean() {
        this.repository().clear();
    }

    private String keyOf(final TenantId tenantId, final String username) {
        return tenantId.id() + "#" + username;
    }

    private String keyOf(final User user) {
        return this.keyOf(user.tenantId(), user.username());
    }

    private Map<String,User> repository() {
        return this.repository;
    }
}
