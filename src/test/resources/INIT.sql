CREATE TABLE IF NOT EXISTS roles (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  name varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN');
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER');