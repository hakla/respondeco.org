# --- !Ups

CREATE TABLE Organization (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Organization;
