# --- !Ups

ALTER TABLE account ADD COLUMN organisation INT UNSIGNED;
ALTER TABLE account ADD CONSTRAINT fk_account_organisation FOREIGN KEY (organisation) REFERENCES organisation(id);

# --- !Downs

ALTER TABLE account DROP COLUMN organisation;
ALTER TABLE account DROP CONSTRAINT fk_account_organisation;
