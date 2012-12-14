News application on Play 2.0
=====================================
Heroku info: http://stormy-taiga-9359.herokuapp.com
Old Procfile:
web: target/start -Dhttp.port=${PORT} -DapplyEvolutions.default=true -Ddb.default.driver=com.mysql.jdbc.Driver -Ddb.default.url=jdbc:mysql://us-cdbr-east-02.cleardb.com/heroku_7c0ab5bfc1e9701?reconnect=true -Ddb.default.user=bbc36f83ddbd62 -Ddb.default.password=ff5c1c02


Admin login: kip.sigman@gmail.com/savings123


<span class="field-editable" data-toggle="#category@deal.id" data-type="select" data-pk="@deal.id" data-name="categoryId" data-value="@deal.categoryId.getOrElse("")" data-source="@routes.CategoryController.selectOptions" data-prepend="-- Select --">@if(deal.categoryId.isDefined){@Category.nameById(deal.categoryId.get)} else {<i class="icon-warning-sign"></i>}</span>
<a href="#" id="category@deal.id" class="edit-toggle"><i class="icon-pencil"></i></a>


TODO:
News app
---------------
- Persist Article.publish
- Add images to ArticleTemplate
- Choose domain
- UI for public site
