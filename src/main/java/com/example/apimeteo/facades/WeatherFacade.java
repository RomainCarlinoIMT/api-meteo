package com.example.apimeteo.facades;

import org.springframework.stereotype.Service;

import com.example.apimeteo.models.Coordinate;
import com.example.apimeteo.services.GeocodingService;
import com.example.apimeteo.services.WeatherService;

@Service
public class WeatherFacade 
{
    private final GeocodingService mGeocodingService;
    private final WeatherService mWeatherService;  
    
    public WeatherFacade(GeocodingService geocodingService, WeatherService weatherService)
    {
        this.mGeocodingService = geocodingService;
        this.mWeatherService = weatherService;
    }

    public String getWeatherData(String address) 
    {
        Coordinate coords = mGeocodingService.getCoordinate(address);
        return mWeatherService.getForecast(coords.getLatitude(), coords.getLongitude());
    }
}