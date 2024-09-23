CREATE DATABASE paper_trading;

CREATE TABLE users (
    id INT AUTO_INCREMENT,
    first_name VARCHAR(32) NOT NULL,
    last_name VARCHAR(32) NOT NULL,
    email VARCHAR(48) NOT NULL UNIQUE,
    password VARCHAR(80) NOT NULL,
    date_of_birth DATE NOT NULL,
    created_date DATE NOT NULL,
    locked BOOL NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE roles (
    id INT AUTO_INCREMENT,
    name VARCHAR(16) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_USER');

CREATE TABLE users_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
);