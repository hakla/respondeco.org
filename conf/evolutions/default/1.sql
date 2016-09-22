# --- !Ups

CREATE TABLE Organization (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE Project (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE Account (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    email VARCHAR(255),
    password VARCHAR(255),
    role BIGINT(20),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE Account;
DROP TABLE Organization;
DROP TABLE Project;
