package services

import java.io.InputStream
import java.net.{ URLDecoder, URLEncoder }

import javax.inject.{ Inject, Singleton }

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.matching.Regex

import play.api.mvc._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

@Singleton
class UrlService @Inject() (ws: WSClient) extends UrlUtils {
  
  /**
   * Shortens a URL using a third party URL shortener.
   */
  def shortenUrl(url: String)(implicit ec: ExecutionContext): Future[String] = {

    // Use Google URL shortner: https://developers.google.com/url-shortener/v1/getting_started#shorten
    val googleApiUrl = "https://www.googleapis.com/urlshortener/v1/url"
    val json = Json.obj("longUrl" -> url)
    val shortUrlFuture = ws.url(googleApiUrl).post(json).map(response => {
      (response.json \ "id").as[String]
    })
    shortUrlFuture
  }
}

trait UrlUtils {
  val HttpProtocol = "http://"

  // Regex
  val UrlRegex: Regex = """(https?|ftp|file|mailto)://[-A-Za-z0-9+&@#/%?=~_|!:,.;\{\}]*[-A-Za-z0-9+&@#/%=~_|\{\}]""".r
  val UrlMatchRegex: Regex = ("^" + UrlRegex.toString + "$").r

  def absoluteUrl(request: RequestHeader, relativeUrl: String): String = {
    HttpProtocol + request.host + relativeUrl
  }

  def isValidUrl(url: String): Boolean = {
    UrlMatchRegex.findFirstMatchIn(url).isDefined
  }
  
  def urlDecode(url: String): String = URLDecoder.decode(url, "UTF-8")

  def urlEncode(url: String): String = URLEncoder.encode(url, "UTF-8")
}

object UrlService extends UrlUtils