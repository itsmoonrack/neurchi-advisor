create table view_group
(
    tenant_id varchar(36) not null,
    group_id varchar(36) not null,
    created_on date not null,
    name varchar(70) not null,
    description text null,
    cover varchar(2048) null,
    member_count int not null,
    verified boolean not null
);

