-- -----------------------------------------------------
-- Schema gift_certificates
-- -----------------------------------------------------
DROP DATABASE IF EXISTS gift_certificates;
CREATE DATABASE IF NOT EXISTS gift_certificates DEFAULT CHARACTER SET utf8;
USE gift_certificates;

-- -----------------------------------------------------
-- Table certificate
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS certificates
(
    PRIMARY KEY (id),
    id               INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    name             VARCHAR(32)   NOT NULL UNIQUE,
    description      VARCHAR(256)  NOT NULL,
    price            DECIMAL(9, 2) NOT NULL,
    duration         INT UNSIGNED  NOT NULL,
    create_date      DATETIME(3)   NOT NULL,
    last_update_date DATETIME(3)   NOT NULL
);

CREATE TRIGGER do_immutable_create_date
    BEFORE UPDATE
    ON certificates
    FOR EACH ROW
BEGIN
    SET NEW.create_date = OLD.create_date;
END;


-- -----------------------------------------------------
-- Table tag
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS tags
(
    PRIMARY KEY (id),
    id   INT UNSIGNED NOT NULL AUTO_INCREMENT,
    name VARCHAR(32)  NOT NULL UNIQUE
);


-- -----------------------------------------------------
-- Table certificates_tags
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS certificates_tags
(
    PRIMARY KEY (certificate_id, tag_id),
    certificate_id INT UNSIGNED NOT NULL,
    tag_id         INT UNSIGNED NOT NULL,
    FOREIGN KEY (certificate_id)
        REFERENCES certificates (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (tag_id)
        REFERENCES tags (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);


-- -----------------------------------------------------
-- Table users
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS users
(
    PRIMARY KEY (id),
    id       INT UNSIGNED                 NOT NULL AUTO_INCREMENT,
    login    VARCHAR(32) COLLATE utf8_bin NOT NULL UNIQUE,
    email    VARCHAR(255)                 NOT NULL UNIQUE,
    password VARCHAR(512)                 NOT NULL,
    role     VARCHAR(15)                  NOT NULL
);


CREATE TABLE IF NOT EXISTS orders
(
    PRIMARY KEY (id),
    id                  INT UNSIGNED AUTO_INCREMENT,
    cost                DECIMAL(9, 2) NOT NULL,
    create_date         DATETIME(3)   NOT NULL,
    user_id             INT UNSIGNED,
    gift_certificate_id INT UNSIGNED,
    FOREIGN KEY (gift_certificate_id)
        REFERENCES certificates (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
