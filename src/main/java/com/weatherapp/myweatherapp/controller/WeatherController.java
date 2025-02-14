package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalTime;

/**
 * Controller handling weather-related API endpoints.
 */
@Controller
public class WeatherController {

  @Autowired
  private WeatherService weatherService;

  /**
   * Fetches the weather forecast for a given city.
   *
   * @param city the city name
   * @return ResponseEntity containing the forecast details
   */
  @GetMapping("/forecast/{city}")
  public ResponseEntity<CityInfo> forecastByCity(@PathVariable("city") String city) {
    CityInfo ci = weatherService.forecastByCity(city);
    return ResponseEntity.ok(ci);
  }

  /**
   * Compares the length of daylight hours between two cities.
   *
   * @param city1 the first city name
   * @param city2 the second city name
   * @return ResponseEntity containing the city with the longest daylight or a message if they are the same
   */
  @GetMapping("/compareDaylight/{city1}/{city2}")
  public ResponseEntity<String> compareDaylight(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {
    CityInfo c1 = weatherService.forecastByCity(city1);
    CityInfo c2 = weatherService.forecastByCity(city2);

    if (c1 == null || c2 == null || c1.getCurrentConditions() == null || c2.getCurrentConditions() == null) {
      return ResponseEntity.badRequest().body("Error: One or both cities could not be found.");
    }

    String sunrise1 = c1.getCurrentConditions().getSunrise();
    String sunset1 = c1.getCurrentConditions().getSunset();
    String sunrise2 = c2.getCurrentConditions().getSunrise();
    String sunset2 = c2.getCurrentConditions().getSunset();

    if (sunrise1 == null || sunset1 == null || sunrise2 == null || sunset2 == null) {
      return ResponseEntity.badRequest().body("Error: Missing sunrise or sunset data for one or both cities.");
    }

    try {
      LocalTime sunriseFormatted1 = LocalTime.parse(sunrise1.substring(0,5));
      LocalTime sunsetFormatted1 = LocalTime.parse(sunset1.substring(0,5));
      LocalTime sunriseFormatted2 = LocalTime.parse(sunrise2.substring(0,5));
      LocalTime sunsetFormatted2 = LocalTime.parse(sunset2.substring(0,5));

      long daylight1 = java.time.Duration.between(sunriseFormatted1, sunsetFormatted1).toMinutes();
      long daylight2 = java.time.Duration.between(sunriseFormatted2, sunsetFormatted2).toMinutes();

      if (daylight1 > daylight2) {
        return ResponseEntity.ok(city1);
      } else if (daylight1 < daylight2) {
        return ResponseEntity.ok(city2);
      } else {
        return ResponseEntity.ok("both cities have same daylight");
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Determines which city is currently experiencing rain.
   *
   * @param city1 the first city name
   * @param city2 the second city name
   * @return ResponseEntity containing the city experiencing rain or a message if neither is raining
   */
  @GetMapping("/rainingCity/{city1}/{city2}")
  public ResponseEntity<String> rainingCity(@PathVariable("city1") String city1, @PathVariable("city2") String city2) {
    CityInfo c1 = weatherService.forecastByCity(city1);
    CityInfo c2 = weatherService.forecastByCity(city2);

    if (c1 == null || c2 == null || c1.getCurrentConditions() == null || c2.getCurrentConditions() == null) {
      return ResponseEntity.badRequest().body("Error: Missing data for one or both cities.");
    }

    String conditions1 = c1.getCurrentConditions().getConditions().toLowerCase();
    String conditions2 = c2.getCurrentConditions().getConditions().toLowerCase();

    boolean isRainingCity1 = conditions1.contains("rain");
    boolean isRainingCity2 = conditions2.contains("rain");

    if (isRainingCity1 && isRainingCity2) {
      return ResponseEntity.ok(city1 + "," + city2 + " both raining");
    } else if (isRainingCity1) {
      return ResponseEntity.ok(city1);
    } else if (isRainingCity2) {
      return ResponseEntity.ok(city2);
    } else {
      return ResponseEntity.ok("neither are raining");
    }
  }
}
