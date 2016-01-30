# --- !Ups

CREATE TABLE user (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
	email VARCHAR(255) NOT NULL,
	avatar_url VARCHAR(255),
	roles VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE login_info (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    provider_id VARCHAR(255) NOT NULL,
    provider_key VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE user_login_info (
    user_id INT UNSIGNED NOT NULL,
    login_info_id INT UNSIGNED NOT NULL,
	CONSTRAINT fk_uli_user_id FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE,
	CONSTRAINT fk_uli_login_info_id FOREIGN KEY(login_info_id) REFERENCES login_info(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE password_info (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    login_info_id INT UNSIGNED NOT NULL,
    hasher VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    salt VARCHAR(255),
    PRIMARY KEY (id),
	CONSTRAINT fk_pi_login_info_id FOREIGN KEY(login_info_id) REFERENCES login_info(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# --- !Downs

DROP TABLE IF EXISTS password_info;
DROP TABLE IF EXISTS user_login_info;
DROP TABLE IF EXISTS login_info;
DROP TABLE IF EXISTS user;