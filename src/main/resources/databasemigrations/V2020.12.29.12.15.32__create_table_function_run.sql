create sequence function_run_seq;
create table function_run
(
    id            bigint    not null primary key default nextval('function_run_seq'),
    function_name text      not null,
    created_time  timestamp not null             default now(),
    user_id       bigint    not null references "user"
);
