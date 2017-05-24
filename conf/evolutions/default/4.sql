# noinspection SqlNoDataSourceInspectionForFile
# --- !Ups

ALTER TABLE project ADD COLUMN organisation INT UNSIGNED;
ALTER TABLE project ADD CONSTRAINT fk_organisation FOREIGN KEY (organisation) REFERENCES organisation(id);

# --- !Downs

ALTER TABLE project DROP COLUMN organisation;
ALTER TABLE project DROP CONSTRAINT fk_organisation;
