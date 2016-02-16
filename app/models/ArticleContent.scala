package models

import kipsigman.domain.entity.CategorizedEntity

trait ArticleContent[T <: ArticleContent[T]] extends Content[T] with CategorizedEntity {
  import ArticleContent._
  
  def headline: String
  def body: String
  def imageFileName: Option[String]
  def imageCaption: Option[String]
  def imageSource: ImageSource
}

object ArticleContent {
  trait ImageSource
  object ImageSource {
    case object Article extends ImageSource
    case object Template extends ImageSource
  }
}