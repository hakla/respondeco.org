# --- !Ups

ALTER TABLE organisation ADD COLUMN image VARCHAR(255);

# --- !Downs

ALTER TABLE organisation DROP COLUMN image;
