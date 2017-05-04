# noinspection SqlNoDataSourceInspectionForFile
# --- !Ups

ALTER TABLE Project ADD COLUMN price INT UNSIGNED;

# --- !Downs

ALTER TABLE Project DROP COLUMN price;
