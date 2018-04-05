# --- !Ups

ALTER TABLE comment ADD COLUMN pinned BOOLEAN DEFAULT FALSE;

# --- !Downs

ALTER TABLE comment DROP COLUMN pinned;
