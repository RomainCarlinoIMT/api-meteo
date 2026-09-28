package com.example.apimeteo.models;

import java.util.List;

public record WeatherResponse(
        String address,
        Double latitude,
        Double longitude,
        List<HourlyForecast> hourly) {
}