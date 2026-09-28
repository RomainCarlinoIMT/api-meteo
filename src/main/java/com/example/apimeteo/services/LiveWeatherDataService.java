package com.example.apimeteo.services;

import org.springframework.stereotype.Service;

import com.example.apimeteo.models.Coordinate;
import com.example.apimeteo.models.WeatherResponse;

@Service
public class LiveWeatherDataService implements WeatherDataSource {
    private final GeocodingService geocodingService;
    private final WeatherService weatherService;

    public LiveWeatherDataService(GeocodingService geocodingService, WeatherService weatherService) {
        this.geocodingService = geocodingService;
        this.weatherService = weatherService;
    }

    @Override
    public WeatherResponse getWeatherData(String address) {
        Coordinate coords = geocodingService.getCoordinate(address);
        return new WeatherResponse(
                address,
                coords.getLatitude(),
                coords.getLongitude(),
                weatherService.getForecast(coords.getLatitude(), coords.getLongitude()));
    }
}