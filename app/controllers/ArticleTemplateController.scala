package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.domain.entity.Category
import kipsigman.play.auth.entity.Role
import kipsigman.play.auth.entity.User
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.data.Forms.optional
import play.api.i18n.Messages
import play.api.i18n.MessagesApi

import models.ArticleTemplate
import models.NewsCategoryOptions
import models.NewsRepository
import services.ContentAuthorizationService

@Singleton
class ArticleTemplateController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  newsRepository: NewsRepository,
  protected val contentAuthorizationService: ContentAuthorizationService)(implicit ec: ExecutionContext)
  extends BaseController(messagesApi, env) with ContentAuthorizationController[ArticleTemplate] {
  
  protected def findContent(id: Int): Future[Option[ArticleTemplate]] = newsRepository.findArticleTemplate(id)
  
  private case class ArticleTemplateData(
    id: Option[Int] = None,
    category: Category = NewsCategoryOptions.National,
    headline: String = "",
    body: String = "") {
    
    def this(at: ArticleTemplate) = this(at.id, at.category, at.headline, at.body)
  }
    
  private val form = Form[ArticleTemplateData](
    mapping(
      "id" -> optional(number),
      "category" -> NewsCategoryOptions.formMapping,
      "headline" -> nonEmptyText,
      "body" -> nonEmptyText
    )(ArticleTemplateData.apply)(ArticleTemplateData.unapply)
  )
  
  private val categoryOptions: Seq[(String, String)] =
    NewsCategoryOptions.all.map(cat => cat.name -> Messages(s"category.name.${cat.name}")).toSeq.sortBy(_._2)

  def list = UserAwareAction.async { implicit request =>
    newsRepository.findArticleTemplates(None).map(articleTemplates =>
      Ok(views.html.articleTemplate.list(articleTemplates, None))
    )
  }
  
  def listByCategory(category: Category) = UserAwareAction.async { implicit request =>
    newsRepository.findArticleTemplates(Option(category)).map(articleTemplates =>
      Ok(views.html.articleTemplate.list(articleTemplates, Option(category)))
    )
  }

  def create = SecuredAction(WithRole(Role.Editor)) { implicit request =>
    val theForm = form.fill(ArticleTemplateData())
    Ok(views.html.articleTemplate.edit(theForm, categoryOptions))
  }

  def edit(id: Int) = SecuredAction(WithRole(Role.Editor)).async { implicit request =>
    authorizeEdit(id) map {
      case Some(articleTemplate) => {
        val theForm = form.fill(new ArticleTemplateData(articleTemplate))
        Ok(views.html.articleTemplate.edit(theForm, categoryOptions))
      }
      case None => NotFound
    }
  }

  def save = SecuredAction(WithRole(Role.Editor)).async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(views.html.articleTemplate.edit(formWithErrors, categoryOptions))),
      data => {
        if(data.id.isDefined) {
          authorizeEdit(data.id.get) flatMap {
            case Some(oldArticleTemplate) => {
              val articleTemplate = oldArticleTemplate.copy(category = data.category, headline = data.headline, body = data.body)
              newsRepository.saveArticleTemplate(articleTemplate).map(savedArticleTemplate =>
                Redirect(routes.ArticleTemplateController.list()).flashing(FlashKey.success -> Messages("action.save.success"))
              )
            }
            case None => Future.successful(notFound)
          }
        } else {
          val userId = request.identity.id.get
          val articleTemplate = ArticleTemplate(userId = userId, category = data.category, headline = data.headline, body = data.body)
          newsRepository.saveArticleTemplate(articleTemplate).map(savedArticleTemplate =>
            Redirect(routes.ArticleTemplateController.list()).flashing(FlashKey.success -> Messages("action.save.success"))
          )
        }
      }
    )
  }
  
  def view(category: Category, id: Int) = UserAwareAction.async { implicit request =>
    newsRepository.findArticleTemplate(id) map {
      case Some(articleTemplate) => {
        Ok(views.html.articleTemplate.view(articleTemplate, contentAuthorizationService))
      }
      case None => notFound
    }
  }
}
