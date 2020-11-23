alter table login_attempt
    alter column blocked_until drop not null;
alter table ip_audit
    alter column blocked_until drop not null;