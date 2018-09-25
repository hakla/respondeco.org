# --- !Ups

ALTER TABLE account ADD CONSTRAINT account_unique_name UNIQUE (name);
ALTER TABLE organisation ADD CONSTRAINT organisation_unique_name UNIQUE (name);

# --- !Downs

ALTER TABLE account DROP CONSTRAINT account_unique_name;
ALTER TABLE organisation DROP CONSTRAINT organisation_unique_name;
