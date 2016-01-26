package models

import scala.concurrent.Future

trait NewsRepository {

  def findArticleById(id: Int): Future[Option[Article]]

  def findArticleBySeoAlias(seoAlias: String): Future[Option[Article]]

  def findArticleInflatedById(id: Int): Future[Option[ArticleInflated]]

  def saveArticle(entity: Article): Future[Article]

  def addTagReplacement(articleId: Int, tagReplacement: TagReplacement): Future[ArticleInflated]

  def findArticleTemplateById(id: Int): Future[Option[ArticleTemplate]]

  def findArticleTemplates: Future[Seq[ArticleTemplate]]

  def saveArticleTemplate(entity: ArticleTemplate): Future[ArticleTemplate]
}

case class PageFilter(page: Int = 0, pageSize: Int = 20) {
  val offset = page * pageSize
}

case class Page[T](items: Seq[T], pageFilter: PageFilter, hasNext: Boolean) {
  lazy val prev = Option(pageFilter.page - 1).filter(_ >= 0)
  lazy val next = Option(pageFilter.page + 1).filter(_ => hasNext)
  lazy val from = pageFilter.offset + 1
  lazy val to = pageFilter.offset + items.size
}

