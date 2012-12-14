package models

trait TagContent {
  
  val TagRegex = """\{([\w]*)\}""".r
  
  def tags: Set[String] = {
    collection.SortedSet.empty[String] ++ (TagRegex.findAllIn(headline).toList ::: TagRegex.findAllIn(body).toList)
  }
  
  def hasTags: Boolean = {
    !tags.isEmpty
  }
  
  def headline: String
  def body: String
}

case class TagReplacement(tag: String, replacement: String) {
  
  val regex = tag.replace("{", "\\{").replace("}", "\\}").r
  
  def replace(str: String): String = {
    regex.replaceAllIn(str, replacement)
  }
  
  override def toString: String = {
    tag + "=" + replacement
  }
}