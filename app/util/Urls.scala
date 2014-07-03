package util

import scala.concurrent.Future
import scala.util.matching.Regex
import java.io.InputStream
import java.net.{URLDecoder,URLEncoder}
import play.api.mvc.{AnyContent,Request}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.Play.current

object Urls {
  
  val HttpProtocol = "http://"

  // Regex
  val UrlRegex: Regex = """(https?|ftp|file|mailto)://[-A-Za-z0-9+&@#/%?=~_|!:,.;\{\}]*[-A-Za-z0-9+&@#/%=~_|\{\}]""".r
  val UrlMatchRegex: Regex = ("^" + UrlRegex.toString + "$").r
  
  def absoluteUrl(request: Request[AnyContent], relativeUrl: String): String = {
    HttpProtocol + request.host + relativeUrl
  }
  
  def isValidUrl(url: String): Boolean = {
    UrlMatchRegex.findFirstMatchIn(url).isDefined
  }
  
  /**
   * Shortens a URL using a third party URL shortener.
   * @param url
   * @return
   */
  def shortenUrl(url: String): Future[String] = {
    
    // Use Google URL shortner: https://developers.google.com/url-shortener/v1/getting_started#shorten
    val googleApiUrl = "https://www.googleapis.com/urlshortener/v1/url"
    val json = Json.obj("longUrl" -> url)
    val shortUrlFuture = WS.url(googleApiUrl).post(json).map(response => {
      (response.json \ "id").as[String]
    })
    shortUrlFuture
  }
  
  def urlDecode(url: String) = URLDecoder.decode(url, "UTF-8")
  
  def urlEncode(url: String) = URLEncoder.encode(url, "UTF-8")

}