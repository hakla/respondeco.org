# --- !Ups

CREATE TABLE comment (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    project_history BIGINT(20),
    author BIGINT(20),
    title VARCHAR(255),
    content TEXT,
    image VARCHAR(255),
    video VARCHAR(255),
    PRIMARY KEY (id)
);

# --- !Downs

DROP TABLE comment;
