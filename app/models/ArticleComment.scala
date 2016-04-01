package models

import java.time.LocalDateTime

import kipsigman.domain.entity.IdEntity
import kipsigman.domain.entity.Status
import kipsigman.play.auth.entity.User

/**
 * Article Comment. The following business rules apply:
 * - May be a top level comment or a reply in a thread (represented by parent_id)
 * - Comment is either from a User or if anonymous will have name defined
 */
case class ArticleComment(
  id: Option[Int] = None,
  articleId: Int,
  parentId: Option[Int],
  user: User,
  createDateTime: LocalDateTime = LocalDateTime.now(),
  body: String) extends IdEntity {
  
  lazy val displayUserName: String = user.firstName.get
}

case class ArticleCommentGroup(parent: ArticleComment, children: Seq[ArticleComment]) {
  lazy val parentId: Int = parent.id.get
}