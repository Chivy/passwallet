alter table "user"
    add column deleted boolean not null default false;
alter table "passwords"
    add column deleted boolean not null default false;