CREATE TABLE t_users(
    id VARCHAR(255) NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role BIGINT NOT NULL,
    status TINYINT(1) NOT NULL,
    PRIMARY KEY(id),
    CONSTRAINT fk_role
        FOREIGN KEY (role)
            REFERENCES auth_service.t_roles(id)
)