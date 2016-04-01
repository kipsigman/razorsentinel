package controllers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.ZoneId
import java.util.Locale

import kipsigman.domain.entity.Category
import kipsigman.domain.entity.ContentImage
import kipsigman.domain.entity.Image
import kipsigman.play.auth.entity.User
import kipsigman.play.mvc.AlertContext
import kipsigman.play.mvc.AlertContext._
import play.api.mvc.Call
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import org.slf4j.LoggerFactory

import models._

object ViewHelper {
  private val logger = LoggerFactory.getLogger(this.getClass)
  
  val sampleArticleImage = Image(Some(0), "image/jpeg", 890, 395)
  
  /**
   * Classes for alerts. Note that this uses bootstrap 3.
   */
  def alertClasses(alertContext: AlertContext): String = {
    val baseAlertClasses = "alert alert-dismissible"
    alertContext match {
      case Error => s"$baseAlertClasses alert-danger"
      case Info => s"$baseAlertClasses alert-info"
      case Success =>	s"$baseAlertClasses alert-success"
      case Warning => s"$baseAlertClasses alert-warning"
    }
  }

  private def iconCode(name: String): String = name match {
    case "phone"               => "&#128222;"
    case "mobile"              => "&#128241;"
    case "mouse"               => "&#59273;"
    case "address"             => "&#59171;"
    case "mail"                => "&#9993;"
    case "paper-plane"         => "&#128319;"
    case "pencil"              => "&#9998;"
    case "feather"             => "&#10002;"
    case "attach"              => "&#128206;"
    case "inbox"               => "&#59255;"
    case "reply"               => "&#59154;"
    case "reply-all"           => "&#59155;"
    case "forward"             => "&#10150;"
    case "user"                => "&#128100;"
    case "users"               => "&#128101;"
    case "add-user"            => "&#59136;"
    case "vcard"               => "&#59170;"
    case "export"              => "&#59157;"
    case "location"            => "&#59172;"
    case "map"                 => "&#59175;"
    case "compass"             => "&#59176;"
    case "direction"           => "&#10146;"
    case "hair-cross"          => "&#127919;"
    case "share"               => "&#59196;"
    case "shareable"           => "&#59198;"
    case "heart"               => "&hearts;"
    case "heart-empty"         => "&#9825;"
    case "star"                => "&#9733;"
    case "star-empty"          => "&#9734;"
    case "thumbs-up"           => "&#128077;"
    case "thumbs-down"         => "&#128078;"
    case "chat"                => "&#59168;"
    case "comment"             => "&#59160;"
    case "quote"               => "&#10078;"
    case "home"                => "&#8962;"
    case "popup"               => "&#59212;"
    case "search"              => "&#128269;"
    case "flashlight"          => "&#128294;"
    case "print"               => "&#59158;"
    case "bell"                => "&#128276;"
    case "link"                => "&#128279;"
    case "flag"                => "&#9873;"
    case "cog"                 => "&#9881;"
    case "tools"               => "&#9874;"
    case "trophy"              => "&#127942;"
    case "tag"                 => "&#59148;"
    case "camera"              => "&#128247;"
    case "megaphone"           => "&#128227;"
    case "moon"                => "&#9789;"
    case "palette"             => "&#127912;"
    case "leaf"                => "&#127810;"
    case "note"                => "&#9834;"
    case "beamed-note"         => "&#9835;"
    case "new"                 => "&#128165;"
    case "graduation-cap"      => "&#127891;"
    case "book"                => "&#128213;"
    case "newspaper"           => "&#128240;"
    case "bag"                 => "&#128092;"
    case "airplane"            => "&#9992;"
    case "lifebuoy"            => "&#59272;"
    case "eye"                 => "&#59146;"
    case "clock"               => "&#128340;"
    case "mic"                 => "&#127908;"
    case "calendar"            => "&#128197;"
    case "flash"               => "&#9889;"
    case "thunder-cloud"       => "&#9928;"
    case "droplet"             => "&#128167;"
    case "cd"                  => "&#128191;"
    case "briefcase"           => "&#128188;"
    case "air"                 => "&#128168;"
    case "hourglass"           => "&#9203;"
    case "gauge"               => "&#128711;"
    case "language"            => "&#127892;"
    case "network"             => "&#59254;"
    case "key"                 => "&#128273;"
    case "battery"             => "&#128267;"
    case "bucket"              => "&#128254;"
    case "magnet"              => "&#59297;"
    case "drive"               => "&#128253;"
    case "cup"                 => "&#9749;"
    case "rocket"              => "&#128640;"
    case "brush"               => "&#59290;"
    case "suitcase"            => "&#128710;"
    case "traffic-cone"        => "&#128712;"
    case "globe"               => "&#127758;"
    case "keyboard"            => "&#9000;"
    case "browser"             => "&#59214;"
    case "publish"             => "&#59213;"
    case "progress-3"          => "&#59243;"
    case "progress-2"          => "&#59242;"
    case "progress-1"          => "&#59241;"
    case "progress-0"          => "&#59240;"
    case "light-down"          => "&#128261;"
    case "light-up"            => "&#128262;"
    case "adjust"              => "&#9681;"
    case "code"                => "&#59156;"
    case "monitor"             => "&#128187;"
    case "infinity"            => "&infin;"
    case "light-bulb"          => "&#128161;"
    case "credit-card"         => "&#128179;"
    case "database"            => "&#128248;"
    case "voicemail"           => "&#9991;"
    case "clipboard"           => "&#128203;"
    case "cart"                => "&#59197;"
    case "box"                 => "&#128230;"
    case "ticket"              => "&#127915;"
    case "rss"                 => "&#59194;"
    case "signal"              => "&#128246;"
    case "thermometer"         => "&#128255;"
    case "water"               => "&#128166;"
    case "sweden"              => "&#62977;"
    case "line-graph"          => "&#128200;"
    case "pie-chart"           => "&#9716;"
    case "bar-graph"           => "&#128202;"
    case "area-graph"          => "&#128318;"
    case "lock"                => "&#128274;"
    case "lock-open"           => "&#128275;"
    case "logout"              => "&#59201;"
    case "login"               => "&#59200;"
    case "check"               => "&#10003;"
    case "cross"               => "&#10060;"
    case "squared-minus"       => "&#8863;"
    case "squared-plus"        => "&#8862;"
    case "squared-cross"       => "&#10062;"
    case "circled-minus"       => "&#8854;"
    case "circled-plus"        => "&oplus;"
    case "circled-cross"       => "&#10006;"
    case "minus"               => "&#10134;"
    case "plus"                => "&#10133;"
    case "erase"               => "&#9003;"
    case "block"               => "&#128683;"
    case "info"                => "&#8505;"
    case "circled-info"        => "&#59141;"
    case "help"                => "&#10067;"
    case "circled-help"        => "&#59140;"
    case "warning"             => "&#9888;"
    case "cycle"               => "&#128260;"
    case "cw"                  => "&#10227;"
    case "ccw"                 => "&#10226;"
    case "shuffle"             => "&#128256;"
    case "back"                => "&#128281;"
    case "level-down"          => "&#8627;"
    case "retweet"             => "&#59159;"
    case "loop"                => "&#128257;"
    case "back-in-time"        => "&#59249;"
    case "level-up"            => "&#8624;"
    case "switch"              => "&#8646;"
    case "numbered-list"       => "&#57349;"
    case "add-to-list"         => "&#57347;"
    case "layout"              => "&#9871;"
    case "list"                => "&#9776;"
    case "text-doc"            => "&#128196;"
    case "text-doc-inverted"   => "&#59185;"
    case "doc"                 => "&#59184;"
    case "docs"                => "&#59190;"
    case "landscape-doc"       => "&#59191;"
    case "picture"             => "&#127748;"
    case "video"               => "&#127916;"
    case "music"               => "&#127925;"
    case "folder"              => "&#128193;"
    case "archive"             => "&#59392;"
    case "trash"               => "&#59177;"
    case "upload"              => "&#128228;"
    case "download"            => "&#128229;"
    case "save"                => "&#128190;"
    case "install"             => "&#59256;"
    case "cloud"               => "&#9729;"
    case "upload-cloud"        => "&#59153;"
    case "bookmark"            => "&#128278;"
    case "bookmarks"           => "&#128209;"
    case "open-book"           => "&#128214;"
    case "play"                => "&#9654;"
    case "pause"                => "&#8214;"
    case "record"              => "&#9679;"
    case "stop"                => "&#9632;"
    case "ff"                  => "&#9193;"
    case "fb"                  => "&#9194;"
    case "to-start"            => "&#9198;"
    case "to-end"              => "&#9197;"
    case "resize-full"         => "&#59204;"
    case "resize-small"        => "&#59206;"
    case "volume"              => "&#9207;"
    case "sound"               => "&#128266;"
    case "mute"                => "&#128263;"
    case "flow-cascade"        => "&#128360;"
    case "flow-branch"         => "&#128361;"
    case "flow-tree"           => "&#128362;"
    case "flow-line"           => "&#128363;"
    case "flow-parallel"       => "&#128364;"
    case "left-bold"           => "&#58541;"
    case "down-bold"           => "&#58544;"
    case "up-bold"             => "&#58543;"
    case "right-bold"          => "&#58542;"
    case "left"                => "&#11013;"
    case "down"                => "&#11015;"
    case "up"                  => "&#11014;"
    case "right"               => "&#10145;"
    case "circled-left"        => "&#59225;"
    case "circled-down"        => "&#59224;"
    case "circled-up"          => "&#59227;"
    case "circled-right"       => "&#59226;"
    case "triangle-left"       => "&#9666;"
    case "triangle-down"       => "&#9662;"
    case "triangle-up"         => "&#9652;"
    case "triangle-right"      => "&#9656;"
    case "chevron-left"        => "&#59229;"
    case "chevron-down"        => "&#59228;"
    case "chevron-up"          => "&#59231;"
    case "chevron-right"       => "&#59230;"
    case "chevron-small-left"  => "&#59233;"
    case "chevron-small-down"  => "&#59232;"
    case "chevron-small-up"    => "&#59235;"
    case "chevron-small-right" => "&#59234;"
    case "chevron-thin-left"   => "&#59237;"
    case "chevron-thin-down"   => "&#59236;"
    case "chevron-thin-up"     => "&#59239;"
    case "chevron-thin-right"  => "&#59238;"
    case "left-thin"           => "&larr;"
    case "down-thin"           => "&darr;"
    case "up-thin"             => "&uarr;"
    case "right-thin"          => "&rarr;"
    case "arrow-combo"         => "&#59215;"
    case "three-dots"          => "&#9206;"
    case "two-dots"            => "&#9205;"
    case "dot"                 => "&#9204;"
    case "cc"                  => "&#128325;"
    case "cc-by"               => "&#128326;"
    case "cc-nc"               => "&#128327;"
    case "cc-nc-eu"            => "&#128328;"
    case "cc-nc-jp"            => "&#128329;"
    case "cc-sa"               => "&#128330;"
    case "cc-nd"               => "&#128331;"
    case "cc-pd"               => "&#128332;"
    case "cc-zero"             => "&#128333;"
    case "cc-share"            => "&#128334;"
    case "cc-remix"            => "&#128335;"
    case "db-logo"             => "&#128505;"
    case "db-shape"            => "&#128506;"
    case "github"              => "&#62208;"
    case "c-github"            => "&#62209;"
    case "flickr"              => "&#62211;"
    case "c-flickr"            => "&#62212;"
    case "vimeo"               => "&#62214;"
    case "c-vimeo"             => "&#62215;"
    case "twitter"             => "&#62217;"
    case "c-twitter"           => "&#62218;"
    case "facebook"            => "&#62220;"
    case "c-facebook"          => "&#62221;"
    case "s-facebook"          => "&#62222;"
    case "google+"             => "&#62223;"
    case "c-google+"           => "&#62224;"
    case "pinterest"           => "&#62226;"
    case "c-pinterest"         => "&#62227;"
    case "tumblr"              => "&#62229;"
    case "c-tumblr"            => "&#62230;"
    case "linkedin"            => "&#62232;"
    case "c-linkedin"          => "&#62233;"
    case "dribbble"            => "&#62235;"
    case "c-dribbble"          => "&#62236;"
    case "stumbleupon"         => "&#62238;"
    case "c-stumbleupon"       => "&#62239;"
    case "lastfm"              => "&#62241;"
    case "c-lastfm"            => "&#62242;"
    case "rdio"                => "&#62244;"
    case "c-rdio"              => "&#62245;"
    case "spotify"             => "&#62247;"
    case "c-spotify"           => "&#62248;"
    case "qq"                  => "&#62250;"
    case "instagram"           => "&#62253;"
    case "dropbox"             => "&#62256;"
    case "evernote"            => "&#62259;"
    case "flattr"              => "&#62262;"
    case "skype"               => "&#62265;"
    case "c-skype"             => "&#62266;"
    case "renren"              => "&#62268;"
    case "sina-weibo"          => "&#62271;"
    case "paypal"              => "&#62274;"
    case "picasa"              => "&#62277;"
    case "soundcloud"          => "&#62280;"
    case "mixi"                => "&#62283;"
    case "behance"             => "&#62286;"
    case "google-circles"      => "&#62289;"
    case "vk"                  => "&#62292;"
    case "smashing"            => "&#62295;"
  }
  
  def iconCodeHtml(name: String): Html = Html(iconCode(name))

  def iconSpan(name: String): Html = Html(s"""<span class="icon-text">${iconCode(name)}</span>""")
  
  private def dateFormatter(locale: Locale, zoneId: ZoneId) = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale).withZone(zoneId)
  private def dateTimeFormatter(locale: Locale, zoneId: ZoneId) = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(locale).withZone(zoneId)
  
  /**
   * TODO: Format in user's timezone
   */
  def displayDate(dateTimeOption: Option[LocalDateTime])(implicit request: RequestHeader, userOption: Option[User]): String = {
    val locale = request.acceptLanguages.head.toLocale
    val zoneId = ZoneId.systemDefault()
    logger.info(s"locale=$locale, zoneId=$zoneId")
    val dateTime = dateTimeOption.getOrElse(LocalDateTime.now())
    dateFormatter(locale, zoneId).format(dateTime)
  }
  
  /**
   * TODO: Format in user's timezone
   */
  def displayDateTime(dateTime: LocalDateTime)(implicit request: RequestHeader, userOption: Option[User]): String = {
    val locale = request.acceptLanguages.head.toLocale
    val zoneId = ZoneId.systemDefault()
    logger.info(s"locale=$locale, zoneId=$zoneId")
    dateTimeFormatter(locale, zoneId).format(dateTime)
  }
  
  def viewArticle(category: Category, article: Article)(implicit request: RequestHeader): Call =
    routes.ArticleController.view(category, article.seoAlias)
    
  def viewArticle(categoryOption: Option[Category], article: Article)(implicit request: RequestHeader): Call = {
    val category = categoryOption.getOrElse(article.category)
    viewArticle(category, article)
  }
  
  def viewArticleAbsoluteUrl(category: Category, article: Article)(implicit request: RequestHeader): String =
    viewArticle(category, article).absoluteURL
  
  def viewArticleAbsoluteUrl(categoryOption: Option[Category], article: Article)(implicit request: RequestHeader): String =
    viewArticle(categoryOption, article).absoluteURL
    
  def imageAbsoluteUrl(image: Image)(implicit request: RequestHeader): String =
    kipsigman.play.mvc.routes.S3Controller.image(image.filename.get).absoluteURL
    
  def contentImageAbsoluteUrl(contentImage: ContentImage)(implicit request: RequestHeader) =
    imageAbsoluteUrl(contentImage.image)
  
}