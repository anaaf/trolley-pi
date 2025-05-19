create table tbl_user
(
    id            bigserial
        primary key,
    created_by    varchar(255),
    creation_date timestamp(6),
    enabled       boolean,
    last_updated  timestamp(6),
    updated_by    varchar(255),
    uuid          varchar(36),
    version       integer not null,
    email         varchar(255),
    password      varchar(255),
    phone_no      varchar(255),
    user_name     varchar(255)
);
