CREATE SEQUENCE user_seq;
CREATE TABLE "user"
(
    ID BIGINT NOT NULL DEFAULT nextval('user_seq'),
    LOGIN TEXT NOT NULL,
    PASSWORD_HASH TEXT NOT NULL,
    SALT TEXT NOT NULL,
    IS_PASSWORD_KEPT_AS_HASH BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (ID)
)