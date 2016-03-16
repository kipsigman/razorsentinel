package services

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import javax.inject.Inject
import javax.inject.Singleton

import com.typesafe.config.Config
import kipsigman.domain.entity.Location
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.ws.WSClient
import org.slf4j.LoggerFactory

import models.Weather

@Singleton
class WeatherService @Inject() (
  config: Config,
  ws: WSClient)(implicit ec: ExecutionContext) {
  
  private val logger = LoggerFactory.getLogger(getClass)
  
  private[services] lazy val weatherApiKey = config.getString("worldweatheronline.apiKey")
  
  private[services] val areaNamePath = (JsPath \ "areaName")(0)
  private[services] val regionPath = (JsPath \ "region")(0)
  private[services] val countryPath = (JsPath \ "country")(0)
  
  private[services] implicit val locationReads: Reads[Location] = (
    (areaNamePath \ "value").read[String] and
    (regionPath \ "value").read[String] and
    (JsPath \ "postalCode").readNullable[String] and
    (countryPath \ "value").read[String] and
    (JsPath \ "latitude").readNullable[String].map(_.map(_.toDouble)) and
    (JsPath \ "longitude").readNullable[String].map(_.map(_.toDouble))
  )(Location.apply _)
	
  private[services] val currentConditionPath = (JsPath \ "data" \ "current_condition")(0)
  private[services] val nearestAreaPath = (JsPath \ "data" \ "nearest_area")(0)
  private[services] implicit val weatherReads: Reads[Weather] = (
    (currentConditionPath \ "weatherCode").read[String].map(_.toInt) and
    ((currentConditionPath \ "weatherDesc")(0) \ "value").read[String] and
    (currentConditionPath \ "temp_C").read[String].map(_.toInt) and
    (currentConditionPath \ "temp_F").read[String].map(_.toInt) and
    (nearestAreaPath).read[Location]
  )(Weather.apply _)
  
  
  def getWeather(location: String): Future[Option[Weather]] = {
    val weatherServiceUrl = s"http://api.worldweatheronline.com/free/v2/weather.ashx?key=${weatherApiKey}&q=${location}&num_of_days=1&includeLocation=yes&date=today&format=json"
    
    ws.url(weatherServiceUrl).get().map(response => {
      val json = response.json
      logger.debug(s"json=$json")
      val error = json \ "data" \ "error"
      if(error.toOption.isDefined) {
        None
      } else {
        Option(json.as[Weather])  
      }
    })  
  }
  
  def getWeatherByLocation(location: Location): Future[Option[Weather]] = getWeather(location.urlEncoded)
  
  def getWeatherByIp(ipAddress: String): Future[Option[Weather]] = getWeather(ipAddress)
  
  def codeToImage(weatherCode: Int): String = {
    val (image, description) = weatherCode match {
			case 395 => "weather-snow.png" -> "Moderate or heavy snow in area with thunder"
			case 392 => "weather-snow.png" -> "Patchy light snow in area with thunder"
			case 371 => "weather-snow.png" -> "Moderate or heavy snow showers"
			case 368 => "weather-snow.png" -> "Light snow showers"
			case 350 => "weather-snow.png" -> "Ice pellets"
			case 338 => "weather-snow.png" -> "Heavy snow"
			case 335 => "weather-snow.png" -> "Patchy heavy snow"
			case 332 => "weather-snow.png" -> "Moderate snow"
			case 329 => "weather-snow.png" -> "Patchy moderate snow"
			case 326 => "weather-snow.png" -> "Light snow"
			case 323 => "weather-snow.png" -> "Patchy light snow"
			case 320 => "weather-snow.png" -> "Moderate or heavy sleet"
			case 317 => "weather-snow.png" -> "Light sleet"
			case 284 =>	"weather-snow.png" -> "Heavy freezing drizzle"
			case 281 => "weather-snow.png" -> "Freezing drizzle"
			case 266 => "weather-snow.png" ->	"Light drizzle"
			case 263 => "weather-snow.png" ->	"Patchy light drizzle"
			case 230 => "weather-snow.png" ->	"Blizzard"
			case 227 => "weather-snow.png" ->	"Blowing snow"
			case 389 => "weather-thunder.png" -> "Moderate or heavy rain in area with thunder"
			case 386 => "weather-thunder.png" -> "Patchy light rain in area with thunder"
			case 200 => "weather-thunder.png" -> "Thundery outbreaks in nearby"
			case 377 => "weather-rain.png" -> "Moderate or heavy showers of ice pellets"
			case 374 => "weather-rain.png" -> "Light showers of ice pellets"
			case 365 => "weather-rain.png" ->	"Moderate or heavy sleet showers"
			case 362 => "weather-rain.png" ->	"Light sleet showers"
			case 359 => "weather-rain.png" ->	"Torrential rain shower"
			case 356 => "weather-rain.png" ->	"Moderate or heavy rain shower"
			case 353 => "weather-rain.png" ->	"Light rain shower"
			case 314 => "weather-rain.png" ->	"Moderate or Heavy freezing rain"
			case 311 => "weather-rain.png" ->	"Light freezing rain"
			case 308 => "weather-rain.png" ->	"Heavy rain"
			case 305 => "weather-rain.png" ->	"Heavy rain at times"
			case 302 => "weather-rain.png" ->	"Moderate rain"
			case 299 => "weather-rain.png" ->	"Moderate rain at times"
			case 296 => "weather-rain.png" ->	"Light rain"
			case 293 => "weather-rain.png" ->	"Patchy light rain"
			case 185 => "weather-rain.png" ->	"Patchy freezing drizzle nearby"
			case 179 => "weather-rain.png" ->	"Patchy snow nearby"
			case 176 => "weather-rain.png" ->	"Patchy rain nearby"
			case 260 => "weather-cloudy.png" ->	"Freezing fog"
			case 248 => "weather-cloudy.png" ->	"Fog"
			case 143 => "weather-cloudy.png" ->	"Mist"
			case 122 => "weather-cloudy.png" ->	"Overcast"
			case 119 => "weather-cloudy.png" ->	"Cloudy"
			case 116 => "weather-clouds.png" -> "Partly Cloudy"
			case 113 => "weather-sun.png" -> "Sunny"
			case 182 => "weather-sleet.png" -> "Patchy sleet nearby"
			case _ => "weather-default.png" -> "Can\"t get any data"
    }
    
    image
  }
}