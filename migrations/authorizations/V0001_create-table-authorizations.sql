CREATE TABLE IF NOT EXISTS authorizations(
    id VARCHAR(36) NOT NULL,
    order_id VARCHAR(36) NOT NULL,
    customer_id VARCHAR(36) NOT NULL,
    amount DECIMAL(16, 2) NOT NULL,
    status VARCHAR(10) NOT NULL,
    CONSTRAINT pk_authorizations PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;