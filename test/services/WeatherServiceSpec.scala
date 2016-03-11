package services

import org.scalatest.Finders
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.mock.MockitoSugar

import com.typesafe.config._
import kipsigman.domain.entity.Location
import play.api.libs.json._
import play.api.libs.ws.WSClient

import models.Weather

class WeatherServiceSpec extends WordSpec with Matchers with MockitoSugar {
  val config = ConfigFactory.load()
  val wsClient: WSClient = mock[WSClient]
  val weatherService = new WeatherService(config, wsClient)(scala.concurrent.ExecutionContext.Implicits.global)
  
  val locationJsonStr = 
"""{
  "areaName": [{
   "value": "Denver"
  }],
  "country": [{
  	  "value": "United States of America"
  }],
  "latitude": "39.739",
  "longitude": "-104.984",
  "region": [{
    "value": "Colorado"
  }]
}"""
  val weatherJsonStr =
"""{
	"data": {
		"current_condition": [{
			"cloudcover": "25",
			"FeelsLikeC": "-2",
			"FeelsLikeF": "28",
			"humidity": "51",
			"observation_time": "10:27 AM",
			"precipMM": "0.0",
			"pressure": "1021",
			"temp_C": "1",
			"temp_F": "33",
			"visibility": "16",
			"weatherCode": "116",
			"weatherDesc": [{
				"value": "Partly Cloudy"
			}],
			"weatherIconUrl": [{
				"value": "http://cdn.worldweatheronline.net/images/wsymbols01_png_64/wsymbol_0004_black_low_cloud.png"
			}],
			"winddir16Point": "NE",
			"winddirDegree": "50",
			"windspeedKmph": "9",
			"windspeedMiles": "6"
		}],
		"nearest_area": [{
			"areaName": [{
				"value": "Denver"
			}],
			"country": [{
				"value": "United States of America"
			}],
			"latitude": "39.739",
			"longitude": "-104.984",
			"region": [{
				"value": "Colorado"
			}]
		}],
		"request": [{
			"query": "Denver, United States of America",
			"type": "City"
		}],
		"weather": [{
			"astronomy": [{
				"moonrise": "12:28 AM",
				"moonset": "10:54 AM",
				"sunrise": "06:32 AM",
				"sunset": "05:53 PM"
			}],
			"date": "2016-03-01",
			"hourly": [{
				"chanceoffog": "0",
				"chanceoffrost": "20",
				"chanceofhightemp": "0",
				"chanceofovercast": "0",
				"chanceofrain": "0",
				"chanceofremdry": "0",
				"chanceofsnow": "0",
				"chanceofsunshine": "92",
				"chanceofthunder": "0",
				"chanceofwindy": "0",
				"cloudcover": "0",
				"DewPointC": "-9",
				"DewPointF": "16",
				"FeelsLikeC": "-2",
				"FeelsLikeF": "28",
				"HeatIndexC": "3",
				"HeatIndexF": "37",
				"humidity": "73",
				"precipMM": "0.0",
				"pressure": "1021",
				"tempC": "2",
				"tempF": "36",
				"time": "0",
				"visibility": "10",
				"weatherCode": "113",
				"weatherDesc": [{
					"value": "Clear"
				}],
				"weatherIconUrl": [{
					"value": "http://cdn.worldweatheronline.net/images/wsymbols01_png_64/wsymbol_0008_clear_sky_night.png"
				}],
				"WindChillC": "-2",
				"WindChillF": "28",
				"winddir16Point": "NNW",
				"winddirDegree": "343",
				"WindGustKmph": "37",
				"WindGustMiles": "23",
				"windspeedKmph": "21",
				"windspeedMiles": "13"
			}, {
				"chanceoffog": "0",
				"chanceoffrost": "81",
				"chanceofhightemp": "0",
				"chanceofovercast": "0",
				"chanceofrain": "1",
				"chanceofremdry": "0",
				"chanceofsnow": "1",
				"chanceofsunshine": "91",
				"chanceofthunder": "0",
				"chanceofwindy": "0",
				"cloudcover": "0",
				"DewPointC": "-8",
				"DewPointF": "18",
				"FeelsLikeC": "-6",
				"FeelsLikeF": "22",
				"HeatIndexC": "-1",
				"HeatIndexF": "30",
				"humidity": "59",
				"precipMM": "0.0",
				"pressure": "1026",
				"tempC": "-1",
				"tempF": "30",
				"time": "300",
				"visibility": "10",
				"weatherCode": "113",
				"weatherDesc": [{
					"value": "Clear"
				}],
				"weatherIconUrl": [{
					"value": "http://cdn.worldweatheronline.net/images/wsymbols01_png_64/wsymbol_0008_clear_sky_night.png"
				}],
				"WindChillC": "-6",
				"WindChillF": "22",
				"winddir16Point": "NW",
				"winddirDegree": "325",
				"WindGustKmph": "26",
				"WindGustMiles": "16",
				"windspeedKmph": "14",
				"windspeedMiles": "9"
			}, {
				"chanceoffog": "0",
				"chanceoffrost": "30",
				"chanceofhightemp": "0",
				"chanceofovercast": "0",
				"chanceofrain": "0",
				"chanceofremdry": "0",
				"chanceofsnow": "0",
				"chanceofsunshine": "90",
				"chanceofthunder": "0",
				"chanceofwindy": "0",
				"cloudcover": "0",
				"DewPointC": "-6",
				"DewPointF": "22",
				"FeelsLikeC": "-2",
				"FeelsLikeF": "28",
				"HeatIndexC": "0",
				"HeatIndexF": "33",
				"humidity": "63",
				"precipMM": "0.0",
				"pressure": "1028",
				"tempC": "0",
				"tempF": "33",
				"time": "600",
				"visibility": "10",
				"weatherCode": "113",
				"weatherDesc": [{
					"value": "Clear"
				}],
				"weatherIconUrl": [{
					"value": "http://cdn.worldweatheronline.net/images/wsymbols01_png_64/wsymbol_0008_clear_sky_night.png"
				}],
				"WindChillC": "-2",
				"WindChillF": "28",
				"winddir16Point": "WNW",
				"winddirDegree": "293",
				"WindGustKmph": "9",
				"WindGustMiles": "6",
				"windspeedKmph": "8",
				"windspeedMiles": "5"
			}, {
				"chanceoffog": "0",
				"chanceoffrost": "89",
				"chanceofhightemp": "0",
				"chanceofovercast": "0",
				"chanceofrain": "0",
				"chanceofremdry": "0",
				"chanceofsnow": "0",
				"chanceofsunshine": "100",
				"chanceofthunder": "0",
				"chanceofwindy": "0",
				"cloudcover": "94",
				"DewPointC": "-11",
				"DewPointF": "12",
				"FeelsLikeC": "2",
				"FeelsLikeF": "36",
				"HeatIndexC": "5",
				"HeatIndexF": "41",
				"humidity": "64",
				"precipMM": "0.0",
				"pressure": "1028",
				"tempC": "4",
				"tempF": "40",
				"time": "900",
				"visibility": "10",
				"weatherCode": "122",
				"weatherDesc": [{
					"value": "Overcast"
				}],
				"weatherIconUrl": [{
					"value": "http://cdn.worldweatheronline.net/images/wsymbols01_png_64/wsymbol_0004_black_low_cloud.png"
				}],
				"WindChillC": "2",
				"WindChillF": "36",
				"winddir16Point": "S",
				"winddirDegree": "181",
				"WindGustKmph": "7",
				"WindGustMiles": "4",
				"windspeedKmph": "13",
				"windspeedMiles": "8"
			}, {
				"chanceoffog": "0",
				"chanceoffrost": "0",
				"chanceofhightemp": "0",
				"chanceofovercast": "0",
				"chanceofrain": "0",
				"chanceofremdry": "0",
				"chanceofsnow": "0",
				"chanceofsunshine": "100",
				"chanceofthunder": "0",
				"chanceofwindy": "0",
				"cloudcover": "100",
				"DewPointC": "-13",
				"DewPointF": "9",
				"FeelsLikeC": "11",
				"FeelsLikeF": "52",
				"HeatIndexC": "11",
				"HeatIndexF": "52",
				"humidity": "34",
				"precipMM": "0.0",
				"pressure": "1023",
				"tempC": "10",
				"tempF": "50",
				"time": "1200",
				"visibility": "10",
				"weatherCode": "122",
				"weatherDesc": [{
					"value": "Overcast"
				}],
				"weatherIconUrl": [{
					"value": "http://cdn.worldweatheronline.net/images/wsymbols01_png_64/wsymbol_0004_black_low_cloud.png"
				}],
				"WindChillC": "11",
				"WindChillF": "52",
				"winddir16Point": "S",
				"winddirDegree": "181",
				"WindGustKmph": "9",
				"WindGustMiles": "6",
				"windspeedKmph": "18",
				"windspeedMiles": "11"
			}, {
				"chanceoffog": "0",
				"chanceoffrost": "0",
				"chanceofhightemp": "0",
				"chanceofovercast": "0",
				"chanceofrain": "0",
				"chanceofremdry": "0",
				"chanceofsnow": "0",
				"chanceofsunshine": "100",
				"chanceofthunder": "0",
				"chanceofwindy": "0",
				"cloudcover": "6",
				"DewPointC": "-11",
				"DewPointF": "12",
				"FeelsLikeC": "13",
				"FeelsLikeF": "55",
				"HeatIndexC": "13",
				"HeatIndexF": "55",
				"humidity": "30",
				"precipMM": "0.0",
				"pressure": "1019",
				"tempC": "12",
				"tempF": "54",
				"time": "1500",
				"visibility": "10",
				"weatherCode": "113",
				"weatherDesc": [{
					"value": "Sunny"
				}],
				"weatherIconUrl": [{
					"value": "http://cdn.worldweatheronline.net/images/wsymbols01_png_64/wsymbol_0001_sunny.png"
				}],
				"WindChillC": "13",
				"WindChillF": "55",
				"winddir16Point": "ESE",
				"winddirDegree": "122",
				"WindGustKmph": "5",
				"WindGustMiles": "3",
				"windspeedKmph": "19",
				"windspeedMiles": "12"
			}, {
				"chanceoffog": "0",
				"chanceoffrost": "0",
				"chanceofhightemp": "0",
				"chanceofovercast": "0",
				"chanceofrain": "0",
				"chanceofremdry": "0",
				"chanceofsnow": "0",
				"chanceofsunshine": "86",
				"chanceofthunder": "0",
				"chanceofwindy": "0",
				"cloudcover": "7",
				"DewPointC": "-3",
				"DewPointF": "27",
				"FeelsLikeC": "10",
				"FeelsLikeF": "49",
				"HeatIndexC": "9",
				"HeatIndexF": "49",
				"humidity": "42",
				"precipMM": "0.0",
				"pressure": "1017",
				"tempC": "9",
				"tempF": "49",
				"time": "1800",
				"visibility": "10",
				"weatherCode": "113",
				"weatherDesc": [{
					"value": "Clear"
				}],
				"weatherIconUrl": [{
					"value": "http://cdn.worldweatheronline.net/images/wsymbols01_png_64/wsymbol_0008_clear_sky_night.png"
				}],
				"WindChillC": "10",
				"WindChillF": "49",
				"winddir16Point": "S",
				"winddirDegree": "171",
				"WindGustKmph": "4",
				"WindGustMiles": "2",
				"windspeedKmph": "3",
				"windspeedMiles": "2"
			}, {
				"chanceoffog": "0",
				"chanceoffrost": "0",
				"chanceofhightemp": "0",
				"chanceofovercast": "0",
				"chanceofrain": "0",
				"chanceofremdry": "0",
				"chanceofsnow": "0",
				"chanceofsunshine": "84",
				"chanceofthunder": "0",
				"chanceofwindy": "0",
				"cloudcover": "68",
				"DewPointC": "-2",
				"DewPointF": "28",
				"FeelsLikeC": "5",
				"FeelsLikeF": "40",
				"HeatIndexC": "6",
				"HeatIndexF": "42",
				"humidity": "57",
				"precipMM": "0.0",
				"pressure": "1018",
				"tempC": "6",
				"tempF": "42",
				"time": "2100",
				"visibility": "10",
				"weatherCode": "119",
				"weatherDesc": [{
					"value": "Cloudy"
				}],
				"weatherIconUrl": [{
					"value": "http://cdn.worldweatheronline.net/images/wsymbols01_png_64/wsymbol_0004_black_low_cloud.png"
				}],
				"WindChillC": "5",
				"WindChillF": "40",
				"winddir16Point": "SSW",
				"winddirDegree": "197",
				"WindGustKmph": "9",
				"WindGustMiles": "6",
				"windspeedKmph": "6",
				"windspeedMiles": "4"
			}],
			"maxtempC": "12",
			"maxtempF": "54",
			"mintempC": "3",
			"mintempF": "38",
			"uvIndex": "4"
		}]
	}
}"""
  
  "locationReads" should {
    "deseriazlize JSON to Address" in {
      val json = Json.parse(locationJsonStr)
      val location = json.as[Location](weatherService.locationReads)
      location.city shouldBe "Denver"
      location.state shouldBe "Colorado"
      location.country shouldBe "United States of America"
      location.postalCode shouldBe None
      location.latitude.get shouldBe 39.739 +- 0.001
      location.longitude.get shouldBe -104.984 +- 0.001
    }
  }
  
  "weatherReads" should {
    "deseriazlize JSON to Weather" in {
      val json = Json.parse(weatherJsonStr)
      val weather = json.as[Weather](weatherService.weatherReads)
      weather.code shouldBe 116
    }
  }
}