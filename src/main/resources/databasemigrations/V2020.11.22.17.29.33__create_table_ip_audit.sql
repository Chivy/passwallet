create sequence ip_audit_seq;
create table ip_audit
(
    id            bigint      not null primary key default nextval('ip_audit_seq'),
    ip_address    varchar(39) not null,
    user_id       bigint      not null references "user",
    blocked_until timestamp   not null
)