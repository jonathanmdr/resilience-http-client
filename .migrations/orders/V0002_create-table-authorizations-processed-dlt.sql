CREATE TABLE IF NOT EXISTS authorizations_processed_dlt(
    id VARCHAR(36) NOT NULL,
    payload TEXT NOT NULL,
    error TEXT NOT NULL,
    CONSTRAINT pk_authorizations_processed_dlt PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;