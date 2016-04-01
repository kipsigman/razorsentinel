# --- !Ups
CREATE TABLE article_comment (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  article_id INT UNSIGNED NOT NULL,
  parent_id INT UNSIGNED,
  user_id INT UNSIGNED NOT NULL,
  create_date_time TIMESTAMP NOT NULL,
  body MEDIUMTEXT NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_article_comment_article_id FOREIGN KEY(article_id) REFERENCES article(id) ON DELETE CASCADE,
	CONSTRAINT fk_article_comment_parent_id FOREIGN KEY(parent_id) REFERENCES article_comment(id) ON DELETE CASCADE,
	CONSTRAINT fk_article_comment_user_id FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


# --- !Downs
DROP TABLE IF EXISTS article_comment;