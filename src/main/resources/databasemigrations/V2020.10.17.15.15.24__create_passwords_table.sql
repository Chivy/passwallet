CREATE SEQUENCE password_seq;
CREATE TABLE "passwords"
(
    ID          BIGINT NOT NULL DEFAULT nextval('password_seq'),
    LOGIN       TEXT   NOT NULL,
    PASSWORD    TEXT   NOT NULL,
    WEB_ADDRESS TEXT   NOT NULL,
    DESCRIPTION TEXT   NOT NULL,
    USER_ID     BIGINT NOT NULL,
    PRIMARY KEY (ID),
    CONSTRAINT fk_user
        FOREIGN KEY (USER_ID)
            REFERENCES "user" (ID)
)