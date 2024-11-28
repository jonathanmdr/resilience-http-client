package com.resilience.domain.authorization;

public enum AuthorizationStatus {

    PENDING {
        @Override
        public AuthorizationStatus approve() {
            return APPROVED;
        }

        @Override
        public AuthorizationStatus refuse() {
            return REFUSED;
        }
    },
    APPROVED {
        @Override
        public AuthorizationStatus approve() {
            return this;
        }

        @Override
        public AuthorizationStatus refuse() {
            return this;
        }
    },
    REFUSED {
        @Override
        public AuthorizationStatus approve() {
            return this;
        }

        @Override
        public AuthorizationStatus refuse() {
            return this;
        }
    };

    public abstract AuthorizationStatus approve();
    public abstract AuthorizationStatus refuse();

}
