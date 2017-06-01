# noinspection SqlNoDataSourceInspectionForFile
# --- !Ups

CREATE TABLE `project_history` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `project` BIGINT(20),
    `organisation` BIGINT(20),
    `rating_owner` BIGINT(20),
    `rating_organisation` BIGINT(20),
    `date` TIMESTAMP
);

CREATE TABLE `rating` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `liked` BOOLEAN,
    `testimonial` VARCHAR(255)
);

ALTER TABLE `project_history` ADD CONSTRAINT `fk_project_history_project` FOREIGN KEY (`project`) REFERENCES `project`(`id`);
ALTER TABLE `project_history` ADD CONSTRAINT `fk_project_history_organisation` FOREIGN KEY (`organisation`) REFERENCES `organisation`(`id`);
ALTER TABLE `project_history` ADD CONSTRAINT `fk_project_history_rating_owner` FOREIGN KEY (`rating_owner`) REFERENCES `rating`(`id`);
ALTER TABLE `project_history` ADD CONSTRAINT `fk_project_history_rating_organisation` FOREIGN KEY (`rating_organisation`) REFERENCES `rating`(`id`);

# --- !Downs

DROP TABLE `rating`;
DROP TABLE `project_history`;
