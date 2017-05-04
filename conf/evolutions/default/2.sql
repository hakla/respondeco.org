# noinspection SqlNoDataSourceInspectionForFile
# --- !Ups

ALTER TABLE Project ADD COLUMN location VARCHAR(255);
ALTER TABLE Project ADD COLUMN description TEXT;
ALTER TABLE Project ADD COLUMN category VARCHAR(255);
ALTER TABLE Project ADD COLUMN subcategory VARCHAR(255);
ALTER TABLE Project ADD COLUMN start DATE;
ALTER TABLE Project ADD COLUMN end DATE;
ALTER TABLE Project ADD COLUMN benefits VARCHAR(255);

# --- !Downs

ALTER TABLE Project DROP COLUMN location;
ALTER TABLE Project DROP COLUMN description;
ALTER TABLE Project DROP COLUMN category;
ALTER TABLE Project DROP COLUMN subcategory;
ALTER TABLE Project DROP COLUMN start;
ALTER TABLE Project DROP COLUMN end;
ALTER TABLE Project DROP COLUMN benefits;
