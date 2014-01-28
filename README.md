News
=====================================
A fun application where you can customize humorous fake news stories by inserting names, cities, etc.
Current deployed version is on Heroku at http://stormy-taiga-9359.herokuapp.com.

Architecture
------------
* Scala on Play 2.x
* ORM: Squeryl
* Authentication/Authorization: https://github.com/t2v/play2-auth
* UI: Twitter Bootstrap, Inline editing: X-editable 1.5.0 http://vitalets.github.com/x-editable/
* Datasource: MySQL

TODO
---------------
- Switch datasource to Mongodb or another DB with an async driver
- Move TagContent.wrapTagForEdit into a view layer macro
- Add cleanup job for abandoned articles
- UI
  - Make article page no index for search engines (privacy concerns for subjects of satire)
  - New template for public pages (look like a news site)
  - Link to customize this article on article page
  - Versioning/compression of UI assets
- Add images to ArticleTemplate
- Add crowdsourced article templates
- Choose domain

Technical Notes
---------------
mysql --host us-cdbr-east-02.cleardb.com --port=3306 --user=bbc36f83ddbd62 --password=ff5c1c02 --database=heroku_7c0ab5bfc1e9701
Old Procfile:
web: target/start -Dhttp.port=${PORT} -DapplyEvolutions.default=true -Ddb.default.driver=com.mysql.jdbc.Driver -Ddb.default.url=jdbc:mysql://us-cdbr-east-02.cleardb.com/heroku_7c0ab5bfc1e9701?reconnect=true -Ddb.default.user=bbc36f83ddbd62 -Ddb.default.password=ff5c1c02
