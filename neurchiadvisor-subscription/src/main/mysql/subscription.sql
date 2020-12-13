USE neurchiadvisor_subscription;

CREATE TABLE `table_event_store` (
    `event_id`       bigint       NOT NULL auto_increment,
    `event_body`     TEXT         NOT NULL,
    `event_type`     varchar(250) NOT NULL,
    `stream_name`    varchar(250) NOT NULL,
    `stream_version` int          NOT NULL,
    KEY `k_stream_name` (`stream_name`),
    UNIQUE KEY `k_stream_name_stream_version` (`stream_name`, `stream_version`),
    PRIMARY KEY (`event_id`)
) ENGINE = InnoDB;

create table `view_group` (
    `tenant_id` varchar(36) NOT NULL,
    `group_id` varchar(36) NOT NULL,
    `name` varchar(70) NOT NULL,
    `status` varchar(10) NOT NULL
);

