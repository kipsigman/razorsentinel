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
UI Inline editing: X-editable 1.4.0 http://vitalets.github.com/x-editable/
ORM: Squeryl
Authentication/Authorization: https://github.com/t2v/play20-auth



TODO:
---------------
- UI for public site to look like news
- Add images to ArticleTemplate
- Switch datastore to Mongodb
- Test async functions
- Choose domain
- Versioning/compression of UI assets
