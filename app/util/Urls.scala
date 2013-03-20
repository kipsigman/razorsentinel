package util

import scala.util.matching.Regex
import java.io.InputStream
import java.net.{CookieHandler,CookieManager,URL,URLConnection}
import play.api.mvc.{AnyContent,Request}

object Urls {
  
  val HttpProtocol = "http://"
  // Regex
  val UrlRegex: Regex = """(https?|ftp|file|mailto)://[-A-Za-z0-9+&@#/%?=~_|!:,.;\{\}]*[-A-Za-z0-9+&@#/%=~_|\{\}]""".r
  val UrlMatchRegex: Regex = ("^" + UrlRegex.toString + "$").r
  
  def absoluteUrl(request: Request[AnyContent], relativeUrl: String): String = {
    HttpProtocol + request.host + relativeUrl
  }
  
  // On init set up Cookie handling
  CookieHandler.setDefault(new CookieManager(null, java.net.CookiePolicy.ACCEPT_ALL));
  private val DefaultReadTimeout = 5000
  
  /**
   * Get the final real destination of this url after a redirect chain.
   * If no redirects, returns the original URL.
   * 
   * @param url
   * @return
   */
  def getFinalUrl(url: String): String = {
    
    try {
      val connection: URLConnection = new URL(url).openConnection();
      connection.setReadTimeout(DefaultReadTimeout)
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4")
      connection.connect()
      val inputStream: InputStream = connection.getInputStream();
      inputStream.close;
     
      // URL after getting input stream is final destination
      connection.getURL().toString()
    } catch {
      case _ : Throwable => url
    }
  }
  
  def isValidUrl(url: String): Boolean = {
    UrlMatchRegex.findFirstMatchIn(url).isDefined
  }

}