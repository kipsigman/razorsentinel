package models

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.CategorizedEntity
import kipsigman.domain.entity.Content

trait ArticleContent[T <: ArticleContent[T]] extends Content[T] with CategorizedEntity {
  def headline: String
  def body: String
  
  def categoriesSorted: Seq[Category] = Category.sort(categories)
  
  def bodyTeaser(maxLength: Int = 300): String = {
    // Truncate to max length
    val truncatedBody = if (body.length() > maxLength) {
      body.substring(0, maxLength)
    } else {
      body
    }
    
    // Make sure body starts with <p>
    val paragraphOpenBody = if(truncatedBody.startsWith("<p>")) {
      truncatedBody
    } else {
      s"<p>${truncatedBody}"
    }
    
    // Trim to last full paragraph, or force one
    val lastClosingP = paragraphOpenBody.lastIndexOf("</p>")
    if (lastClosingP == -1) {
      // Trim to last sentence
      val sentenceCompleteBody = paragraphOpenBody.substring(0, paragraphOpenBody.lastIndexOf(".") + 1)
      s"${sentenceCompleteBody}</p>"
    } else {
      paragraphOpenBody.substring(0, lastClosingP + 4)
    }
  }
}