package models

import anorm._
import anorm.SQL
import anorm.SqlParser.get
import anorm.sqlToSimple
import anorm.toParameterValue
import play.api.Play.current
import play.api.db.DB

case class ArticleTemplate(id: Pk[Long] = NotAssigned, headline: String, body: String)

object ArticleTemplate {

  /**
   * Parse from a ResultSet
   */
  val simple = {
    get[Pk[Long]]("id") ~
      get[String]("headline") ~
      get[String]("body") map {
        case id ~ headline ~ body => ArticleTemplate(id, headline, body)
      }
  }

  def delete(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("delete from article_template where id = {id}").on('id -> id).executeUpdate()
    }
  }

  def findById(id: Long): Option[ArticleTemplate] = {
    DB.withConnection { implicit connection =>
      SQL("select * from article_template where id = {id}").on('id -> id).as(ArticleTemplate.simple.singleOpt)
    }
  }

  def list(): List[ArticleTemplate] = {
    DB.withConnection { implicit connection =>
      SQL("select * from article_template order by id").as(ArticleTemplate.simple *)
    }
  }
  
  def insert(articleTemplate: ArticleTemplate): Pk[Long] = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into article_template(headline, body) values (
            {headline}, {body}
          )
        """).on(
          'headline -> articleTemplate.headline,
          'body -> articleTemplate.body).executeInsert()
    } match {
      case Some(long) => new Id[Long](long)
      case None => throw new Exception("SQL Error - Did not save")
    }
  }

  def update(id: Long, articleTemplate: ArticleTemplate) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update article_template
          set headline = {headline}, body = {body}
          where id = {id}
        """).on(
          'id -> id,
          'headline -> articleTemplate.headline,
          'body -> articleTemplate.body).executeUpdate()
    }
  }
  
  def save(articleTemplate: ArticleTemplate) = {
    articleTemplate.id match {
      case NotAssigned => insert(articleTemplate)
      case Id(id) => update(id, articleTemplate)
    }
  }

}
