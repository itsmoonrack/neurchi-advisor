DROP DATABASE IF EXISTS neurchiadvisor_iam;
CREATE DATABASE neurchiadvisor_iam;
USE neurchiadvisor_iam;
SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE `table_group`
(
    `id`                  bigint       NOT NULL auto_increment,
    `description`         varchar(250) NOT NULL,
    `name`                varchar(100) NOT NULL,
    `tenant_id`           varchar(36)  NOT NULL,
    `concurrency_version` int          NOT NULL,
    KEY `k_tenant_id` (`tenant_id`),
    UNIQUE KEY `k_tenant_id_name` (`name`, `tenant_id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `table_group_member`
(
    `id`        bigint       NOT NULL auto_increment,
    `name`      varchar(100) NOT NULL,
    `tenant_id` varchar(36)  NOT NULL,
    `type`      varchar(5)   NOT NULL,
    `group_id`  bigint       NOT NULL,
    KEY `k_group_id` (`group_id`),
    UNIQUE KEY `k_group_member` (`name`, `tenant_id`, `type`, `group_id`),
    CONSTRAINT `fk_table_group_member_table_group` FOREIGN KEY (`group_id`) REFERENCES `table_group` (`id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `table_person`
(
    -- primary key is my parent's pk, which is table 'table_user'
    `id`                                bigint      NOT NULL,
    `contact_information_email_address` varchar(100),
    `name_first_name`                   varchar(50) NOT NULL,
    `name_last_name`                    varchar(50) NOT NULL,
    `tenant_id`                         varchar(36) NOT NULL,
    `concurrency_version`               int         NOT NULL,
    KEY `k_tenant_id` (`tenant_id`),
    CONSTRAINT `fk_table_person_table_user` FOREIGN KEY (`id`) REFERENCES `table_user` (`id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `table_registration_invitation`
(
    `id`                  bigint       NOT NULL auto_increment,
    `description`         varchar(100) NOT NULL,
    `invitation_id`       varchar(36)  NOT NULL,
    `starting_on`         datetime,
    `tenant_id`           varchar(36)  NOT NULL,
    `until`               datetime,
    `tenant`              bigint       NOT NULL,
    `concurrency_version` int          NOT NULL,
    KEY `k_tenant` (`tenant`),
    KEY `k_tenant_id` (`tenant_id`),
    UNIQUE KEY `k_invitation_id` (`invitation_id`),
    CONSTRAINT `fk_registration_invitation_tenant` FOREIGN KEY (`tenant`) REFERENCES `table_tenant` (`id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `table_role`
(
    `id`                  bigint       NOT NULL auto_increment,
    `description`         varchar(250) NOT NULL,
    `group_id`            bigint       NOT NULL,
    `name`                varchar(100) NOT NULL,
    `supports_nesting`    tinyint      NOT NULL,
    `tenant_id`           varchar(36)  NOT NULL,
    `concurrency_version` int          NOT NULL,
    KEY `k_tenant_id` (`tenant_id`),
    UNIQUE KEY `k_tenant_id_name` (`name`, `tenant_id`),
    CONSTRAINT `fk_table_role_table_group` FOREIGN KEY (`group_id`) REFERENCES `table_group` (`id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `table_tenant`
(
    `id`                  bigint       NOT NULL auto_increment,
    `active`              tinyint      NOT NULL,
    `description`         varchar(100),
    `name`                varchar(100) NOT NULL,
    `secret`              varchar(100) NOT NULL,
    `tenant_id`           varchar(36)  NOT NULL,
    `concurrency_version` int          NOT NULL,
    UNIQUE KEY `k_name` (`name`),
    UNIQUE KEY `k_tenant_id` (`tenant_id`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `table_user`
(
    `id`                   bigint       NOT NULL auto_increment,
    `access_token`         text         NOT NULL,
    `access_token_type`    varchar(36)  NOT NULL,
    `access_token_expires` datetime     NOT NULL,
    `tenant_id`            varchar(36)  NOT NULL,
    `username`             varchar(250) NOT NULL,
    `concurrency_version`  int          NOT NULL,
    KEY `k_tenant_id` (`tenant_id`),
    UNIQUE KEY `k_tenant_id_username` (`tenant_id`, `username`),
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

CREATE TABLE `table_published_notification_tracker`
(
    `published_notification_tracker_id`     bigint       NOT NULL auto_increment,
    `most_recent_published_notification_id` bigint       NOT NULL,
    `type_name`                             varchar(100) NOT NULL,
    `concurrency_version`                   int          NOT NULL,
    PRIMARY KEY (`published_notification_tracker_id`)
) ENGINE = InnoDB;

CREATE TABLE `table_stored_event`
(
    `event_id`    bigint       NOT NULL auto_increment,
    `event_body`  TEXT         NOT NULL,
    `occurred_on` datetime     NOT NULL,
    `type_name`   varchar(200) NOT NULL,
    PRIMARY KEY (`event_id`)
) ENGINE = InnoDB;

