create sequence password_share_seq;
create table password_share
(
    id          bigint not null primary key default nextval('password_share_seq'),
    user_id     bigint not null references "user",
    password_id bigint not null references passwords
);

create unique index on password_share (user_id, password_id);