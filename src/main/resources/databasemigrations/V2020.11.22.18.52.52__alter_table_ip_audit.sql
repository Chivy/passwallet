alter table ip_audit
    add column date_created  timestamp not null default now(),
    add column is_successful boolean   not null default false