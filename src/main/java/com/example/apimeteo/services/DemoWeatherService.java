package com.example.apimeteo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.apimeteo.models.HourlyForecast;
import com.example.apimeteo.models.WeatherResponse;

@Service
public class DemoWeatherService implements WeatherDataSource {

    @Override
    public WeatherResponse getWeatherData(String address) {
        return new WeatherResponse(
                address,
                44.1279,
                4.0817,
                List.of(new HourlyForecast("2025-06-10T14:00:00Z", 24.3)));
    }
}