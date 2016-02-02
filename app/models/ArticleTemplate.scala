package models

case class ArticleTemplate(
    id: Option[Int] = None,
    categories: Set[Category] = Set(),
    headline: String = "",
    body: String = "") extends IdEntity with CategorizedEntity {

  lazy val tags: Set[String] = TagContent.tags(headline) ++ TagContent.tags(body)
  
  lazy val tagsSorted: Seq[String] = tags.toSeq.sorted
}