package models

import java.time.LocalDateTime

import kipsigman.domain.entity.IdEntity
import kipsigman.play.auth.entity.User
import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
 * Article Comment. The following business rules apply:
 * - May be a top level comment or a reply in a thread (represented by parent_id)
 */
case class ArticleComment(
  id: Option[Int] = None,
  articleId: Int,
  parentId: Option[Int],
  user: User,
  createDateTime: LocalDateTime = LocalDateTime.now(),
  body: String) extends IdEntity {
  
  lazy val displayUserName: String = user.firstName.get
  
  lazy val replyId: Int = parentId.getOrElse(id.get)
}

object ArticleComment {
  implicit val writes: Writes[ArticleComment] = (
    (JsPath \ "id").writeNullable[Int] and
    (JsPath \ "articleId").write[Int] and
    (JsPath \ "parentId").writeNullable[Int] and
    (JsPath \ "user").write[User] and
    (JsPath \ "createDateTime").write[LocalDateTime] and
    (JsPath \ "body").write[String]
  )(unlift(ArticleComment.unapply))
}

case class ArticleCommentGroup(parent: ArticleComment, children: Seq[ArticleComment])

object ArticleCommentGroup {
  implicit val writes: Writes[ArticleCommentGroup] = (
    (JsPath \ "parent").write[ArticleComment] and
    (JsPath \ "children").write[Seq[ArticleComment]]
  )(unlift(ArticleCommentGroup.unapply))
}