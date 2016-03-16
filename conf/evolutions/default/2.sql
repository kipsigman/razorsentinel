# --- !Ups
CREATE TABLE article_template (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id INT UNSIGNED NOT NULL,
  status VARCHAR(255) NOT NULL,
  categories VARCHAR(255) NOT NULL,
  headline VARCHAR(255) NOT NULL,
	body MEDIUMTEXT NOT NULL,
	author VARCHAR(255) NOT NULL,
	CONSTRAINT fk_article_template_user_id FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE article (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id INT UNSIGNED,
  article_template_id INT UNSIGNED NOT NULL,
  status VARCHAR(255) NOT NULL,
	tag_replacements TEXT,
	author VARCHAR(255),
	publish_date TIMESTAMP,
	PRIMARY KEY (id),
	CONSTRAINT fk_article_user_id FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE,
	CONSTRAINT fk_article_article_template_id FOREIGN KEY(article_template_id) REFERENCES article_template(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE image (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    mime_type VARCHAR(255) NOT NULL,
    width SMALLINT UNSIGNED NOT NULL,
    height SMALLINT UNSIGNED NOT NULL,
    caption VARCHAR(255),
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE content_image (
    content_class VARCHAR(255) NOT NULL,
    content_id INT UNSIGNED NOT NULL,
    image_id INT UNSIGNED NOT NULL,
    display_type VARCHAR(255) NOT NULL,
    display_position SMALLINT UNSIGNED NOT NULL,
    PRIMARY KEY(content_class, content_id, image_id),
	CONSTRAINT fk_ai_image_id FOREIGN KEY(image_id) REFERENCES image(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# --- !Downs
DROP TABLE IF EXISTS content_image;
DROP TABLE IF EXISTS image;
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS article_template; 