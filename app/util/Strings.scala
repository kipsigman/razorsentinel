package util

/**
 * String utilities.
 * 
 * @author kip
 */
object Strings {
  
  def formatSeo(str: String) = {
    str.trim().toLowerCase().replaceAll("[\\s-]+", "-").replaceAll("[^\\w-]+", "")
  }
  
  def formatEditable(str: String) = {
    
  }

}