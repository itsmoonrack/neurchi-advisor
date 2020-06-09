USE neurchiadvisor_common_test;

CREATE TABLE `testable_time_constrained_process` (
    `id` bigint NOT NULL auto_increment,
    `allowable_duration` bigint NOT NULL,
    `confirm1` tinyint NOT NULL,
    `confirm2` tinyint NOT NULL,
    `description` varchar(200),
    `process_id_id` varchar(36) NOT NULL,
    `process_completion_type` varchar(50) NOT NULL,
    `start_time` timestamp NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    `timed_out_date` timestamp,
    `total_retries_permitted` bigint NOT NULL,
    `concurrency_version` int NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;
