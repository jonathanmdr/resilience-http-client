package com.resilience.domain.order;

public enum OrderStatus {

    CREATED {
        @Override
        public OrderStatus confirm() {
            return CONFIRMED;
        }

        @Override
        public OrderStatus reject() {
            return REJECTED;
        }

        @Override
        public boolean hasMoreTransitions() {
            return true;
        }
    },
    CONFIRMED {
        @Override
        public OrderStatus confirm() {
            return this;
        }

        @Override
        public OrderStatus reject() {
            return this;
        }

        @Override
        public boolean hasMoreTransitions() {
            return false;
        }
    },
    REJECTED {
        @Override
        public OrderStatus confirm() {
            return this;
        }

        @Override
        public OrderStatus reject() {
            return this;
        }

        @Override
        public boolean hasMoreTransitions() {
            return false;
        }
    };

    public abstract OrderStatus confirm();
    public abstract OrderStatus reject();
    public abstract boolean hasMoreTransitions();

}
