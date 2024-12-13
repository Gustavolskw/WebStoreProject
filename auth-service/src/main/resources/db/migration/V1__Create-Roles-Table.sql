CREATE TABLE t_roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(45) NOT NULL,
    level INT NOT NULL,
    status BOOLEAN DEFAULT 1,
    PRIMARY KEY (id)
);

INSERT INTO t_roles (name, level) VALUES ('ADMIN', 100), ('MOD', 5),('WORKER', 2), ('USER', 1);