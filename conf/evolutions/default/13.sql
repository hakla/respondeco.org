# --- !Ups

ALTER TABLE comment ADD COLUMN status BOOLEAN DEFAULT true;

# --- !Downs

ALTER TABLE comment DROP COLUMN status;
