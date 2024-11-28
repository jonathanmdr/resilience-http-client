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
    };

    public abstract OrderStatus confirm();
    public abstract OrderStatus reject();

}
