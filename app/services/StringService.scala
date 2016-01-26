package services

import javax.inject.{ Inject, Singleton }

/**
 * String utilities.
 */
object StringService {

  /**
   * If string is longer than maxLength, returns a substring followed by an ellipsis.
   * Otherwise returns original string.
   * @param str
   * @param maxLength
   * @return
   */
  def abbreviate(str: String, maxLength: Int): String = {

    if (str.length() > maxLength)
      str.substring(0, maxLength) + "..."
    else
      str
  }

  def formatSeo(str: String) = {
    str.trim().toLowerCase().replaceAll("[\\s-]+", "-").replaceAll("[^\\w-]+", "")
  }

}