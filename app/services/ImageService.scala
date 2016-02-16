package services

import javax.inject.Inject
import javax.inject.Singleton

import models._

@Singleton
class ImageService {
  
  private val baseUrl = "https://s3.amazonaws.com/razorsentinel/images" 
  private val articleFolder = "articles"
  private val templateFolder = "templates"
  
  def imageUrl(path: String): String = {
    s"${baseUrl}/${path}"
  }
  
  private def imageUrl(folder: String, imageFileName: Option[String]): Option[String] = {
    imageFileName.map(ifn => imageUrl(s"${folder}/$ifn"))
  }
  
  def imageUrl(article: Article): Option[String] = {
    imageUrl(articleFolder, article.imageFileName)
  }
  
  def imageUrl(articleTemplate: ArticleTemplate): Option[String] = {
    imageUrl(templateFolder, articleTemplate.imageFileName)
  }
  
  def imageUrl(article: ArticleContent[_]): Option[String] = {
    (article.imageSource) match {
      case ArticleContent.ImageSource.Article => imageUrl(articleFolder, article.imageFileName) 
      case ArticleContent.ImageSource.Template => imageUrl(templateFolder, article.imageFileName)
    }
  }
  
}