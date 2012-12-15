News application on Play 2.0
=====================================
Heroku info: http://stormy-taiga-9359.herokuapp.com
Old Procfile:
web: target/start -Dhttp.port=${PORT} -DapplyEvolutions.default=true -Ddb.default.driver=com.mysql.jdbc.Driver -Ddb.default.url=jdbc:mysql://us-cdbr-east-02.cleardb.com/heroku_7c0ab5bfc1e9701?reconnect=true -Ddb.default.user=bbc36f83ddbd62 -Ddb.default.password=ff5c1c02

Admin login: kip.sigman@gmail.com/savings123


TODO:
---------------
- Finish ArticleTest
- UI for public site
- Choose domain
- Add images to ArticleTemplate
