CREATE TABLE oauth2_user (
    provider VARCHAR(32) NOT NULL,
    id_from_provider VARCHAR(128) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (provider, id_from_provider),
    FOREIGN KEY (user_id) REFERENCES "users"(id)
);
