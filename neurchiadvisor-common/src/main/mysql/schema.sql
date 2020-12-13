USE neurchiadvisor_group;

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

CREATE TABLE `table_stored_event` (
    `event_id` bigint NOT NULL auto_increment,
    `event_body` TEXT NOT NULL,
    `occurred_on` timestamp NOT NULL,
    `type_name` varchar(200) NOT NULL,
    PRIMARY KEY (`event_id`)
) ENGINE=InnoDB;

CREATE TABLE `table_time_constrained_process_tracker` (
    `time_constrained_process_tracker_id` bigint NOT NULL auto_increment,
    `allowable_duration` bigint NOT NULL,
    `completed` tinyint NOT NULL,
    `description` varchar(100) NOT NULL,
    `process_id_id` varchar(36) NOT NULL,
    `process_informed_of_timeout` tinyint NOT NULL,
    `process_timed_out_event_type` varchar(200) NOT NULL,
    `retry_count` int NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    `timeout_occurs_on` timestamp NOT NULL,
    `total_retries_permitted` bigint NOT NULL,
    `concurrency_version` int NOT NULL,
    KEY `k_process_id` (`process_id_id`),
    KEY `k_tenant_id` (`tenant_id`),
    KEY `k_timeout_occurs_on` (`timeout_occurs_on`),
    PRIMARY KEY (`time_constrained_process_tracker_id`)
) ENGINE=InnoDB;