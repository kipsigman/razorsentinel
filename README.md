News application on Play 2.1.0
=====================================
Heroku info: http://stormy-taiga-9359.herokuapp.com
Old Procfile:
web: target/start -Dhttp.port=${PORT} -DapplyEvolutions.default=true -Ddb.default.driver=com.mysql.jdbc.Driver -Ddb.default.url=jdbc:mysql://us-cdbr-east-02.cleardb.com/heroku_7c0ab5bfc1e9701?reconnect=true -Ddb.default.user=bbc36f83ddbd62 -Ddb.default.password=ff5c1c02

Deploy
-----------
$ git add -A .
$ git commit -m "a message..."
$ git push origin master
$ git push heroku master

DB
------------
mysql --host us-cdbr-east-02.cleardb.com --port=3306 --user=bbc36f83ddbd62 --password=ff5c1c02 --database=heroku_7c0ab5bfc1e9701


Features
------------
Play 2.x Scala app
ORM: Squeryl
Authentication/Authorization: https://github.com/t2v/play2-auth
UI: Twitter Bootstrap, Inline editing: X-editable 1.5.0 http://vitalets.github.com/x-editable/


TODO:
---------------
- Polish README
- UI
  - Make article page no index for search engines (privacy concerns for subjects of satire)
  - New template for public pages (look like a news site)
  - Link to customize this article on article page
  - Versioning/compression of UI assets
- Switch datastore to Mongodb
- Add images to ArticleTemplate
- Test async functions
- Choose domain

