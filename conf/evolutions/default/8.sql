# --- !Ups

ALTER TABLE organisation ADD COLUMN logo VARCHAR(255);

# --- !Downs

ALTER TABLE organisation DROP COLUMN logo;
