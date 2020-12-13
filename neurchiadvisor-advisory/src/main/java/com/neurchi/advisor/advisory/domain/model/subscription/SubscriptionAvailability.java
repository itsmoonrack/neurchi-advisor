package com.neurchi.advisor.advisory.domain.model.subscription;

public enum SubscriptionAvailability {

    GroupAccessMemberInfoNotEnabled {
        @Override
        public boolean isGroupAccessMemberInfoNotEnabled() {
            return true;
        }
    },
    Failed {
        @Override
        public boolean isFailed() {
            return true;
        }
    },
    NotRequested {
        @Override
        public boolean isNotRequested() {
            return true;
        }
    },
    Requested {
        @Override
        public boolean isRequested() {
            return true;
        }
    },
    Ready {
        @Override
        public boolean isReady() {
            return true;
        }
    };

    public boolean isGroupAccessMemberInfoNotEnabled() {
        return false;
    }

    public boolean isFailed() {
        return false;
    }

    public boolean isNotRequested() {
        return false;
    }

    public boolean isReady() {
        return false;
    }

    public boolean isRequested() {
        return false;
    }
}
