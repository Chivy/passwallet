create sequence data_change_seq;
create table data_change
(
    id                       bigint    not null primary key default nextval('data_change_seq'),
    user_id                  bigint    not null references "user",
    modified_record_id       bigint    not null unique,
    previous_value_of_record text      not null,
    creation_time            timestamp not null             default now(),
    action_type              text      not null,
    table_name               text      not null
);