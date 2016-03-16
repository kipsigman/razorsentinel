package controllers

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.domain.entity.Category
import kipsigman.domain.entity.ContentImage
import kipsigman.domain.entity.Role
import kipsigman.play.auth.entity.User
import kipsigman.play.service.ImageService
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.data.Forms.optional
import play.api.data.Forms.seq
import play.api.data.validation.Constraint
import play.api.data.validation.Invalid
import play.api.data.validation.Valid
import play.api.data.validation.ValidationError
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.mvc.RequestHeader

import models.ArticleTemplate
import models.ArticleTemplateWithImages
import models.ModelRepository
import models.NewsCategoryOptions
import services.AdService
import services.ContentAuthorizationService
import kipsigman.play.service.HtmlService
import kipsigman.play.service.HtmlValidationResult

@Singleton
class ArticleTemplateController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  modelRepository: ModelRepository,
  contentAuthorizationService: ContentAuthorizationService,
  htmlService: HtmlService,
  imageService: ImageService)
  (implicit ec: ExecutionContext, adService: AdService)
  extends ContentController[ArticleTemplate](messagesApi, env, modelRepository, contentAuthorizationService, imageService) {
  
  override protected def findContent(id: Int): Future[Option[ArticleTemplate]] = modelRepository.findArticleTemplate(id)
  
  private def articleTemplatesWithImages(articleTemplates: Seq[ArticleTemplate])
    (implicit request: RequestHeader, user: Option[User]): Future[Seq[ArticleTemplateWithImages]] = {
    
    val articleTemplateIds = articleTemplates.map(_.id.get).toSet
    for {
      contentImages <- imageService.findContentImages(ArticleTemplate.contentClass, articleTemplateIds)
    } yield {
      articleTemplates.map(articleTemplate => 
        ArticleTemplateWithImages(articleTemplate, contentImages.filter(_.contentId == articleTemplate.id.get))
      )
    }
  }
  
  private case class ArticleTemplateData(
    id: Option[Int] = None,
    categories: Seq[Category] = Seq(NewsCategoryOptions.National),
    headline: String = "",
    body: String = "",
    author: String = "") {
    
    def this(at: ArticleTemplate) = this(at.id, at.categories, at.headline, at.body, at.author)
  }
  
  val htmlBodyFragmentConstraint: Constraint[String] = 
    Constraint("constraints.htmlBodyFragment")(bodyFragment => {
      val validationResult = htmlService.validateBodyFragment(bodyFragment)
      val errors = validationResult match {
        case HtmlValidationResult(false, Some(errorMsg)) => Seq(ValidationError(errorMsg))
        case HtmlValidationResult(false, None) => Seq(ValidationError("articleTemplate.error.body.html"))
        case HtmlValidationResult(true, _) => Nil
      }
      if (errors.isEmpty) {
        Valid
      } else {
        Invalid(errors)
      }
  })
    
  private val form = Form[ArticleTemplateData](
    mapping(
      "id" -> optional(number),
      "categories" -> seq(NewsCategoryOptions.formMapping),
      "headline" -> nonEmptyText,
      "body" -> nonEmptyText.verifying(htmlBodyFragmentConstraint),
      "author" -> nonEmptyText
    )(ArticleTemplateData.apply)(ArticleTemplateData.unapply)
  )
  
  private val categoryOptions: Seq[(String, String)] =
    NewsCategoryOptions.all.map(cat => cat.name -> Messages(s"category.name.${cat.name}")).toSeq.sortBy(_._2)

  def list = UserAwareAction.async { implicit request =>
    modelRepository.findArticleTemplates(None).flatMap(articleTemplates =>
      articleTemplatesWithImages(articleTemplates).map(twis => {
        Ok(views.html.articleTemplate.list(twis, None))
      })
    )
  }
  
  def listByCategory(category: Category, pageIndex: Int = 0) = UserAwareAction.async { implicit request =>
    val categoryOption = Option(category)
    modelRepository.findArticleTemplates(categoryOption).flatMap(articleTemplates =>
      articleTemplatesWithImages(articleTemplates).map(twis => {
        Ok(views.html.articleTemplate.list(twis, categoryOption))
      })
    )
  }

  def create = SecuredAction(WithRole(Role.Editor)) { implicit request =>
    val theForm = form.fill(ArticleTemplateData())
    val contentImages = Seq()
    Ok(views.html.articleTemplate.edit(theForm, contentImages, None, categoryOptions, false))
  }

  def edit(id: Int) = SecuredAction(WithRole(Role.Editor)).async { implicit request =>
    authorizeEditWithImages(id) map {
      case Some(contentWithImages) => {
        val content = contentWithImages._1
        val contentImages = contentWithImages._2
        val theForm = form.fill(new ArticleTemplateData(content))
        val tidyBody = Option(htmlService.repairBodyFragment(content.body))
        Ok(views.html.articleTemplate.edit(theForm, contentImages, tidyBody, categoryOptions, true))
      }
      case None => NotFound
    }
  }

  def save = SecuredAction(WithRole(Role.Editor)).async { implicit request =>
    form.bindFromRequest.fold(
      formWithErrors => {
        val tidyBody = formWithErrors("body").value.map(body => htmlService.repairBodyFragment(body))
        val id = formWithErrors("id").value
        val contentImagesFuture = id match {
          case Some(definedId) => {
            imageService.findContentImages(ArticleTemplate.contentClass, definedId.toInt)    
          }
          case None => Future.successful(Seq())
        }
        
        contentImagesFuture.map(contentImages =>
          BadRequest(views.html.articleTemplate.edit(formWithErrors, contentImages, tidyBody, categoryOptions, false))
        )
      },
      data => {
        if(data.id.isDefined) {
          authorizeEdit(data.id.get) flatMap {
            case Some(oldArticleTemplate) => {
              val articleTemplate = oldArticleTemplate.copy(categories = data.categories, headline = data.headline, body = data.body, author = data.author)
              modelRepository.saveArticleTemplate(articleTemplate).map(savedArticleTemplate =>
                Redirect(controllers.routes.ArticleTemplateController.list()).flashing(FlashKey.success -> Messages("action.save.success"))
              )
            }
            case None => Future.successful(notFound)
          }
        } else {
          val userId = request.identity.id.get
          val articleTemplate = ArticleTemplate(userId = userId, categories = data.categories, headline = data.headline, body = data.body)
          modelRepository.saveArticleTemplate(articleTemplate).map(savedArticleTemplate =>
            Redirect(controllers.routes.ArticleTemplateController.list()).flashing(FlashKey.success -> Messages("action.save.success"))
          )
        }
      }
    )
  }
}
