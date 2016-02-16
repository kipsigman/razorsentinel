# --- !Ups
CREATE TABLE article_template (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id INT UNSIGNED NOT NULL,
    status VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    headline VARCHAR(255) NOT NULL,
	body MEDIUMTEXT NOT NULL,
	image_file_name VARCHAR(255),
	image_caption VARCHAR(255),
	CONSTRAINT fk_article_template_user_id FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE article (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id INT UNSIGNED,
    article_template_id INT UNSIGNED NOT NULL,
    status VARCHAR(255) NOT NULL,
	tag_replacements TEXT,
	image_file_name VARCHAR(255),
	PRIMARY KEY (id),
	CONSTRAINT fk_article_user_id FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE,
	CONSTRAINT fk_article_article_template_id FOREIGN KEY(article_template_id) REFERENCES article_template(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# --- !Downs
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS article_template;