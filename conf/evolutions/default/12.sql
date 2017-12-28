# --- !Ups

ALTER TABLE account ADD COLUMN image VARCHAR(255);
ALTER TABLE comment DROP COLUMN project_history;

CREATE TABLE comment_project_history (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    comment BIGINT(20),
    project_history BIGINT(20),
    PRIMARY KEY (id)
);

ALTER TABLE comment_project_history ADD CONSTRAINT fk_comment_project_history_comment FOREIGN KEY (comment) REFERENCES comment(id);
ALTER TABLE comment_project_history ADD CONSTRAINT fk_comment_project_history_project_history FOREIGN KEY (project_history) REFERENCES project_history(id);

CREATE TABLE comment_project (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    comment BIGINT(20),
    project BIGINT(20),
    PRIMARY KEY (id)
);

ALTER TABLE comment_project ADD CONSTRAINT fk_comment_project_comment FOREIGN KEY (comment) REFERENCES comment(id);
ALTER TABLE comment_project ADD CONSTRAINT fk_comment_project_project FOREIGN KEY (project) REFERENCES project(id);

# --- !Downs

ALTER TABLE account DROP COLUMN image;
ALTER TABLE comment ADD COLUMN project_history BIGINT(20);

DROP TABLE comment_project_history;
DROP TABLE comment_project;

