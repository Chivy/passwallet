create sequence login_attempt_seq;
create table login_attempt
(
    id            bigint    not null primary key default nextval('login_attempt_seq'),
    date_created  timestamp not null             default now(),
    user_id       bigint    not null references "user",
    is_successful boolean   not null             default false,
    blocked_until timestamp not null
)