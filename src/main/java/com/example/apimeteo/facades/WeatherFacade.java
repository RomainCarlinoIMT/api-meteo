package com.example.apimeteo.facades;

import org.springframework.stereotype.Service;

import com.example.apimeteo.models.WeatherResponse;
import com.example.apimeteo.services.WeatherDataSourceFactory;

@Service
public class WeatherFacade 
{
    private final WeatherDataSourceFactory mWeatherDataSourceFactory;
    
    public WeatherFacade(WeatherDataSourceFactory weatherDataSourceFactory)
    {
        this.mWeatherDataSourceFactory = weatherDataSourceFactory;
    }

    public WeatherResponse getWeatherData(String address, boolean demo)
    {
        return mWeatherDataSourceFactory.getDataSource(demo).getWeatherData(address);
    }
}