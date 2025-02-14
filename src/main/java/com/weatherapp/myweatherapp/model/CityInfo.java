package com.weatherapp.myweatherapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CityInfo {

  @JsonProperty("address")
  String address;

  @JsonProperty("description")
  String description;

  @JsonProperty("currentConditions")
  CurrentConditions currentConditions;

  @JsonProperty("days")
  List<Days> days;

  public CityInfo(CurrentConditions currentConditions) {
    this.currentConditions = currentConditions;
  }

  public CurrentConditions getCurrentConditions() {
    return currentConditions;
  }

  public static class CurrentConditions {
    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("sunrise")
    String sunrise;

    @JsonProperty("sunset")
    String sunset;

    @JsonProperty("feelslike")
    String feelslike;

    @JsonProperty("humidity")
    String humidity;

    @JsonProperty("conditions")
    String conditions;

    public CurrentConditions(String sunrise, String sunset, String conditions) {
      this.sunrise = sunrise;
      this.sunset = sunset;
      this.conditions = conditions;
    }

    public String getSunrise(){
      return sunrise;
    }

    public String getSunset(){
      return sunset;
    }

    public String getConditions(){
      return conditions;
    }
  }

  public static class Days {

    @JsonProperty("datetime")
    String date;

    @JsonProperty("temp")
    String currentTemperature;

    @JsonProperty("tempmax")
    String maxTemperature;

    @JsonProperty("tempmin")
    String minTemperature;

    @JsonProperty("conditions")
    String conditions;

    @JsonProperty("description")
    String description;

  }

}
