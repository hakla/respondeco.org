# noinspection SqlNoDataSourceInspectionForFile
# --- !Ups

CREATE TABLE organisation (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE project (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE account (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    email VARCHAR(255),
    password VARCHAR(255),
    role BIGINT(20),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE account;
DROP TABLE organization;
DROP TABLE project;
