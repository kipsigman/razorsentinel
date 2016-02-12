package services

import javax.inject.Inject
import javax.inject.Singleton

import kipsigman.play.auth.entity.User
import org.slf4j.LoggerFactory

import models.Article
import models.ArticleTemplate
import models.Content
import models.NewsRepository

@Singleton
class ContentAuthorizationService @Inject() (newsRepository: NewsRepository) {
  
  private val logger = LoggerFactory.getLogger(this.getClass)
  
  private[services] def canEditContent[T <: Content[T]](content: T)(implicit userOption: Option[User]): Boolean = 
    (content.userIdOption, userOption) match {
    case (_, Some(user)) if(user.isAdministrator) => true // Administrator can edit anything  
    case (None, _) => true // No owner, anyone can edit
    case (Some(userId), _) => content.isOwnedBy(userOption)
  }
  
  def canEdit[T <: Content[T]](content: T)(implicit userOption: Option[User]): Boolean = {
    logger.debug("canEdit content")
    canEditContent(content)
  }
  
  def canEdit(articleTemplate: ArticleTemplate)(implicit userOption: Option[User]): Boolean = {
    logger.debug("canEdit template")
    userOption.map(user =>
      user.isEditor && canEditContent(articleTemplate)
    ).getOrElse(false)
  }
  
  private[services] def canViewEditLinkDefault[T <: Content[T]](content: T)(implicit userOption: Option[User]): Boolean = userOption match {
    case Some(user) if(user.isAdministrator) => true
    case _ => content.isOwnedBy(userOption)
  }
  
  def canViewEditLink[T <: Content[T]](content: T)(implicit userOption: Option[User]): Boolean = {
    logger.debug("canViewEditLink content")
    canViewEditLinkDefault(content)
  }
  
  def canViewEditLink(articleTemplate: ArticleTemplate)(implicit userOption: Option[User]): Boolean = {
    logger.debug("canViewEditLink template")
    userOption.map(user =>
      user.isEditor && canViewEditLinkDefault(articleTemplate)
    ).getOrElse(false)
  }
  
  def canView[T <: Content[T]](content: T)(implicit userOption: Option[User]): Boolean = {
    content.isPublished || content.isOwnedBy(userOption)
  }
}