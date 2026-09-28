package com.example.apimeteo.services;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class WeatherDataSourceFactory {
    private final Map<Boolean, WeatherDataSource> dataSources;

    public WeatherDataSourceFactory(
            LiveWeatherDataService liveWeatherDataService,
            DemoWeatherService demoWeatherService) {
        this.dataSources = Map.of(
                false, liveWeatherDataService,
                true, demoWeatherService);
    }

    public WeatherDataSource getDataSource(boolean demo) {
        return dataSources.get(demo);
    }
}