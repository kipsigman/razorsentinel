# --- !Ups

CREATE TABLE user (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name varchar(255) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  permission INT UNSIGNED NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- kip
INSERT INTO user(name, email, password, permission)
VALUES('Kip', 'kip@savings.com', '$2a$10$sLvKkWVeU1AOBU2HxexONO4kWKf50rt58Fqpf0Dx86Md56li0W5u2', 1);

CREATE TABLE article_template (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id INT UNSIGNED NOT NULL,
	headline VARCHAR(255) NOT NULL,
	body MEDIUMTEXT NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT fk_article_template_user_id FOREIGN KEY(user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE article (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    article_template_id INT UNSIGNED NOT NULL,
	tag_replacements TEXT,
	publish BOOLEAN NOT NULL DEFAULT 0,
	PRIMARY KEY (id),
	CONSTRAINT fk_article_article_template_id FOREIGN KEY(article_template_id) REFERENCES article_template(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO article_template(user_id, headline, body) VALUES (1,'Local Company Declares {firstname} {lastname} \"Most Worthless Employee\"','<p>{city} (PX)--Every year, {company} scours its ranks to find the single employee who, above all others, has earned the title \"Most Worthless Employee.\" Competition is fierce, and prospective winners must have really distinguished themselves as dead weight throughout the previous year to have any chance of taking home the title.</p><p>\"It\'s not as simple as just not doing your work,\" said one of the company officials in charge of handing out the award, who spoke only on conditions of anonymity.  \"In the early-nineties that might have done it, but today to have any chance of being voted MWE, you must not only avoid doing your own work, but also impede the progress of those around you as well. {firstname} fits the bill perfectly.\"</p><p>\"{firstname} is well-known company-wide for being consistently late, dallying on the internet, and being generally uncooperative.  Internal records show that {firstname}\'s last full day of work was well over three months ago, with only brief spurts of activity before that.\"</p><p>Said a co-worker, \"The committee had things easy this year with {lastname} on the ballot. {firstname} is about as useful as a knife in a gunfight.\"</p><p>As \"Most Worthless Employee\", {firstname} {lastname} will have the opportunity to continue working indefinitely and may be offered a promotion.</p>');

# --- !Downs
DROP TABLE IF EXISTS article;
DROP TABLE IF EXISTS article_template;
DROP TABLE IF EXISTS user;