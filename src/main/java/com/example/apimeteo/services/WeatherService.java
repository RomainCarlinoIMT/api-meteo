package com.example.apimeteo.services;

import java.util.List;

import com.example.apimeteo.models.HourlyForecast;

public interface WeatherService 
{
    List<HourlyForecast> getForecast(Double latitude, Double longitude);
}
