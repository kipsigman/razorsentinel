package util

import play.api.mvc.{AnyContent,Request}

object Urls {
  
  val HttpProtocol = "http://"
  def absoluteUrl(request: Request[AnyContent], relativeUrl: String): String = {
    HttpProtocol + request.host + relativeUrl
  }

}