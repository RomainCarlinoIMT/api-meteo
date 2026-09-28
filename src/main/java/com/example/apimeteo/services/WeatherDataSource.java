package com.example.apimeteo.services;

import com.example.apimeteo.models.WeatherResponse;

public interface WeatherDataSource {
    WeatherResponse getWeatherData(String address);
}