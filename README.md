News application on Play 2.0
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



Features
------------
UI Inline editing: X-editable 1.4.0 http://vitalets.github.com/x-editable/
ORM: Squeryl
Authentication/Authorization: https://github.com/t2v/play20-auth



TODO:
---------------
- x-editable to pop up edit windows to the right so they don't appear off screen
- Upgrade to Play 2.1/Scala 2.10
- Upgrade to new Play 20 Auth
- Use stateless sessions for Auth: https://github.com/t2v/play20-auth (search for stateless)
- Versioning of UI assets
- UI for public site to look like news
- Choose domain
- Add images to ArticleTemplate
