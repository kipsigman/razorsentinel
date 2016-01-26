package models

case class ArticleTemplate(
    id: Option[Int] = None,
    headline: String = "",
    body: String = "") extends IdEntity {

  def tags: Set[String] = {
    collection.SortedSet.empty[String] ++ (TagContent.TagRegex.findAllIn(headline).toList ::: TagContent.TagRegex.findAllIn(body).toList)
  }

}