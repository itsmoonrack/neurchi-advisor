USE neurchiadvisor_advisory;

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
) ENGINE = InnoDB;

CREATE TABLE `table_group`
(
    `id` bigint NOT NULL auto_increment,
    `tenant_id` varchar(36) NOT NULL,
    `group_id` varchar(36) NOT NULL,
    `owner_id` varchar(36) NOT NULL,
    `created_on` datetime NOT NULL,
    `name` varchar(70) NOT NULL,
    `description` text null,
    `cover` varchar(2048) null,
    `member_count` int NOT NULL,
    `subscription_initiation_id` varchar(36) NULL,
    `subscription_availability` varchar(36) NOT NULL,
    `subscription_descriptor` varchar(36) NOT NULL,
    `concurrency_version` int NOT NULL,
    KEY `k_tenant_id` (`tenant_id`),
    KEY `k_group_id` (`group_id`),
    KEY `k_subscription_initiation_id` (`subscription_initiation_id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `table_member`
(
    `id` bigint NOT NULL auto_increment,
    `tenant_id` varchar(36) NOT NULL,
    `username` varchar(36) NOT NULL,
    `first_name` varchar(50) NOT NULL,
    `last_name` varchar(50) NOT NULL,
    `name` varchar(100) NOT NULL,
    `name_changed_on` timestamp NOT NULL,
    `email_address` varchar(100) NOT NULL,
    `email_address_changed_on` timestamp NOT NULL,
    `picture` varchar(2048) null,
    `picture_changed_on` timestamp NOT NULL,
    `enabled` tinyint NOT NULL,
    `enabled_on` timestamp NOT NULL,
    `concurrency_version` int NOT NULL,
    `type` varchar(1) NOT NULL,
    KEY `k_tenant_id` (`tenant_id`),
    UNIQUE KEY `k_username` (`username`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `table_team`
(
    `id` bigint NOT NULL auto_increment,
    `tenant_id` varchar(36) NOT NULL,
    `name` varchar(100) NOT NULL,
    `group_owner_id` bigint NULL,
    `concurrency_version` int NOT NULL,
    KEY `k_tenant_id` (`tenant_id`),
    UNIQUE KEY `k_name` (`name`),
    CONSTRAINT `fk_team_group_owner` FOREIGN KEY (`group_owner_id`) REFERENCES table_member (`id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE table_team_member
(
    `team_id` bigint NOT NULL,
    `member_id` bigint NOT NULL,
    CONSTRAINT `fk_team_id` FOREIGN KEY (`team_id`) REFERENCES table_team (`id`),
    CONSTRAINT `fk_member_id` FOREIGN KEY (`member_id`) REFERENCES table_member (`id`),
    PRIMARY KEY (`team_id`, `member_id`)
) ENGINE = InnoDB;

