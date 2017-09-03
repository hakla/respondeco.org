# --- !Ups

ALTER TABLE organisation ADD COLUMN video VARCHAR(255);
ALTER TABLE project ADD COLUMN video VARCHAR(255);
ALTER TABLE project ADD COLUMN image VARCHAR(255);

# --- !Downs

ALTER TABLE organisation DROP COLUMN video;
ALTER TABLE project DROP COLUMN video;
ALTER TABLE project ADD COLUMN image VARCHAR(255);
