CREATE TABLE IF NOT EXISTS account (
    account_id bigserial primary key
);

CREATE TABLE IF NOT EXISTS discord_account (
    discord_account_id bigint primary key,
    account_id bigint not null references account(account_id),
    user_name varchar(32) not null,
    discriminator varchar(4) not null
);

CREATE TABLE IF NOT EXISTS account_connected_service (
    account_connected_service_id bigserial primary key,
    account_id bigint references account(account_id) not null,
    service_name varchar(32) not null
);

CREATE TABLE IF NOT EXISTS request_authentication_oauth1a (
    request_authentication_oauth1a_id bigserial primary key,
    account_id bigint not null references account(account_id),
    service_name varchar(32) not null,
    request_authentication_time timestamp not null,
    request_token varchar(256) not null,
    request_token_secret varchar(256) not null
);

CREATE TABLE IF NOT EXISTS account_token_oauth1a (
    account_connected_service_id bigint primary key references account_connected_service(account_connected_service_id),
    access_token varchar(256) not null,
    access_token_secret varchar(256) not null
);