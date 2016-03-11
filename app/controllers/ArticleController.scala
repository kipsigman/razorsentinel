package controllers

import java.time.LocalDate

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.api.exceptions.NotAuthorizedException
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import javax.inject.Inject
import javax.inject.Singleton
import kipsigman.domain.entity.Category
import kipsigman.domain.entity.Role
import kipsigman.play.auth.entity.User
import kipsigman.play.data.forms.JavaTimeFormatters
import kipsigman.play.mvc.AjaxHelper
import kipsigman.play.service.ImageService
import play.api.data.Form
import play.api.data.Forms.boolean
import play.api.data.Forms.mapping
import play.api.data.Forms.nonEmptyText
import play.api.data.Forms.number
import play.api.data.Forms.optional
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.mvc.BodyParsers
import play.api.mvc.RequestHeader
import play.api.mvc.Result

import models.Article
import models.ArticleTemplate
import models.ModelRepository
import models.TagReplacement
import services.ContentAuthorizationService

@Singleton
class ArticleController @Inject() (
  messagesApi: MessagesApi,
  env: Environment[User, CookieAuthenticator],
  modelRepository: ModelRepository,
  contentAuthorizationService: ContentAuthorizationService,
  imageService: ImageService)
  (implicit ec: ExecutionContext) 
  extends ArticleContentController(messagesApi, env, modelRepository, contentAuthorizationService, imageService) {
  
  import ArticleForms._
  
  def create(articleTemplateId: Int) = UserAwareAction.async { implicit request =>
    modelRepository.findArticleTemplate(articleTemplateId) flatMap {
      case Some(articleTemplate) => {
        imageService.findContentImages(ArticleTemplate.contentClass, articleTemplate.id.get).map(contentImages => {
          val article = Article(userId = request.identity.flatMap(_.id), articleTemplate = articleTemplate)
          val theForm = createArticleDataForm(article)
          Ok(views.html.article.create(article, contentImages, theForm))    
        })
      }
      case None => Future.successful(notFound)
    }
  }
  
  def createPost(articleTemplateId: Int) = UserAwareAction.async(BodyParsers.parse.urlFormEncoded) {implicit request =>
    modelRepository.findArticleTemplate(articleTemplateId) flatMap {
      case Some(articleTemplate) => {
        val bareArticle = Article(userId = request.identity.flatMap(_.id), articleTemplate = articleTemplate)
        val updatedTagsArticle = bindTags(bareArticle, articleTemplate, request.body)
        
        articleDataForm.bindFromRequest.fold(
          formWithErrors => {
            imageService.findContentImages(ArticleTemplate.contentClass, articleTemplate.id.get).map(contentImages => {
              BadRequest(views.html.article.create(updatedTagsArticle, contentImages, formWithErrors))    
            })
          },
          articleData => {
            val preparedArticle = bindSpecialFields(updatedTagsArticle, articleData)
            modelRepository.saveArticle(preparedArticle).map(savedArticle =>
              Redirect(controllers.routes.ArticleController.edit(savedArticle.id.get)).flashing(FlashKey.success -> Messages("article.create.success"))
            )
          }
        )
      }
      case None => Future.successful(notFound)
    }
  }
  
  def delete(id: Int) = saveStatus(id, kipsigman.domain.entity.Status.Deleted)
  
  def edit(id: Int) = UserAwareAction.async { implicit request =>
    authorizeEdit(id) flatMap {
      case Some(article) => {
        imageService.findContentImages(ArticleTemplate.contentClass, article.articleTemplate.id.get).map(contentImages => {
          val theForm = createArticleDataForm(article)
          Ok(views.html.article.edit(article, contentImages, theForm))    
        })
      }
      case None => Future.successful(notFound)
    }
  }
  
  def editPost(id: Int) = UserAwareAction.async(BodyParsers.parse.urlFormEncoded) {implicit request =>
    authorizeEdit(id) flatMap {
      case Some(article) => {
        articleDataForm.bindFromRequest.fold(
          formWithErrors => {
            logger.debug("Form error: " + formWithErrors.toString())
            Future.successful(AjaxHelper.entitySaveErrorResult(article, formWithErrors))
          },
          articleData => {
            logger.debug("Form success: " + articleData)
            val preparedArticle = bind(article, article.articleTemplate, articleData, request.body)
            modelRepository.saveArticle(preparedArticle).map(savedArticle =>
              AjaxHelper.entitySaveSuccessResult(savedArticle)
            )
          }
        )
      }
      case None => {
        Future.successful(AjaxHelper.entityNotFoundResult(Article.getClass, id))
      }
    }
  }
  
  def list = SecuredAction(WithRole(Role.Member)).async { implicit request =>
    modelRepository.findArticlesByUser(request.identity).flatMap(articles =>
      articlesWithImages(articles).map(awis => {
        Ok(views.html.article.list(awis))
      })
    )
  }
  
  def own(id: Int) = SecuredAction(WithRole(Role.Member)).async { implicit request =>
    authorizeEdit(id) flatMap {
      case Some(article) => {
        if (article.userIdOption.isEmpty) {
          val user = request.identity
          val updatedArticle = article.copy(userId = user.id)
          modelRepository.saveArticle(updatedArticle).map(savedArticle =>
            Redirect(controllers.routes.ArticleController.list())
          )
        } else {
          throw new NotAuthorizedException(s"User ${request.identity} cannot own article with an existing owner")
        }
      }
      case None => Future.successful(notFound)
    }
  }
  
  def preview(id: Int) = UserAwareAction.async { implicit request =>
    modelRepository.findArticle(id) map {
      case Some(article) => {
        Redirect(controllers.routes.ArticleController.view(article.category, article.seoAlias))
      }
      case None => notFound
    }
  }

  def saveStatus(id: Int, status: kipsigman.domain.entity.Status) = UserAwareAction.async { implicit request =>
    authorizeEdit(id).flatMap(articleOption =>
      for {
        savedArticle <- modelRepository.updateArticleStatus(id, status)
      } yield {
        if(savedArticle.isDeleted) {
          Redirect(controllers.routes.ArticleController.list).flashing(FlashKey.success -> Messages("content.status.save.success"))
        } else {
          Redirect(controllers.routes.ArticleController.edit(id)).flashing(FlashKey.success -> Messages("content.status.save.success"))
        }
      }
    )
  }
  
  def saveTag = UserAwareAction.async { implicit request =>
    tagDataForm.bindFromRequest.fold(
      formWithErrors => Future(BadRequest("Bad data")),
      tagData => {
        authorizeEdit(tagData.articleId).flatMap {
          case Some(article) => {
            for {
              savedArticle <- modelRepository.addTagReplacement(article.id.get, TagReplacement(tagData.name, tagData.value))
            } yield {
              AjaxHelper.entitySaveSuccessResult(savedArticle)
            }  
          }
          case None => Future.successful(AjaxHelper.entityNotFoundResult(Article.getClass, tagData.articleId))
        }
      }
    )
  }
  
  private def viewById(category: Category, id: Int)(implicit request: RequestHeader, userOption: Option[User]): Future[Result] = {
    modelRepository.findArticle(id) flatMap {
      case Some(article) => {
        imageService.findContentImages(ArticleTemplate.contentClass, article.articleTemplate.id.get).map(contentImages => {
          Ok(views.html.article.view(category, article, contentImages, contentAuthorizationService))
        })
      }
      case None => Future.successful(notFound)
    }
  }
  
  /**
   * seoAlias will be in this format:
   * /category/<category>/url-friendly-headline-<id>
   * /category/<category>/<id>
   * ex: /category/entertainment/tourist-attacks-mickey-mouse-10
   */
  def view(category: Category, seoAlias: String) = UserAwareAction.async { implicit request =>
    val lastDash = seoAlias.lastIndexOf('-')
    try {
      lastDash match {
        case -1 => viewById(category, seoAlias.toInt)
        case x if(x > 0) => viewById(category, seoAlias.substring(lastDash + 1).toInt)
        case x => Future.successful(notFound)
      }
    } catch {
      case t : Throwable => Future.successful(notFound)
    }
  }
}

object ArticleForms {
  case class ArticleData(
    articleId: Option[Int],
    articleTemplateId: Int,
    publishDate: LocalDate,
    publishDateFixed: Boolean)

  val articleDataForm = Form[ArticleData](
    mapping(
      "articleId" -> optional(number),
      "articleTemplateId" -> number,
      "publishDate" -> JavaTimeFormatters.localDateMapping,
      "publishDateFixed" -> boolean
    )(ArticleData.apply)(ArticleData.unapply)
  )
  
  case class TagData(
    articleId: Int,
    name: String,
    value: String)

  val tagDataForm = Form[TagData](
    mapping(
      "pk" -> number,
      "name" -> nonEmptyText,
      "value" -> nonEmptyText
    )(TagData.apply)(TagData.unapply)
  )
  
  def publishDateFixedOptions(implicit messages: Messages): Seq[(String, String)] = {
    Seq("false"-> messages("article.publishDate.fixed.option.false"), "true"-> messages("article.publishDate.fixed.option.true"))
  }
  
  def createArticleData(article: Article): ArticleData = {
    ArticleData(article.id, article.articleTemplate.id.get, article.publishDate.map(_.toLocalDate).getOrElse(LocalDate.now()), article.publishDate.isDefined)
  }
  
  def createArticleDataForm(article: Article): Form[ArticleData] = {
    val articleData = createArticleData(article)
    articleDataForm.fill(articleData)
  }
  
  def bindSpecialFields(article: Article, articleData: ArticleData): Article = {
    val newPublishDate = 
      if(articleData.publishDateFixed) {
        Option(articleData.publishDate.atStartOfDay())
      } else {
        None
      }
    
    // Save Article and redirect to edit page
    article.copy(publishDate = newPublishDate)
  }
  
  def bindTags(article: Article, articleTemplate: ArticleTemplate, formData: Map[String, Seq[String]]): Article = {
    val updatedTagsArticle = formData.foldLeft(article)((article, kv) => {
      if (articleTemplate.tags.contains(kv._1)) {
        val tagReplacement = TagReplacement(kv._1, kv._2.head)
        article.addTagReplacement(tagReplacement)
      } else {
        article
      }
    })
    
    updatedTagsArticle
  }
  
  def bind(article: Article, articleTemplate: ArticleTemplate, articleData: ArticleData, formData: Map[String, Seq[String]]): Article = {
     bindTags(
       bindSpecialFields(article, articleData),
       articleTemplate,
       formData)
  }
}