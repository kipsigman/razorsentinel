# --- !Ups

CREATE TABLE article_template (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	headline VARCHAR(255) NOT NULL,
	body MEDIUMTEXT NOT NULL,
	PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO article_template(headline, body) VALUES('Tourist Attacks Mickey', 'Blah blah blah...');
INSERT INTO article_template(headline, body) VALUES('{firstname:Kip} Declared Most Worthless Employee', 'Blahde blahde blahde...');


# --- !Downs

DROP TABLE article_template;