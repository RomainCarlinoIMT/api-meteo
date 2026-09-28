package com.example.apimeteo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.apimeteo.facades.WeatherFacade;
import com.example.apimeteo.models.WeatherResponse;

@RestController
public class WeatherController {
    private final WeatherFacade weatherFacade;

    public WeatherController(WeatherFacade weatherFacade) {
        this.weatherFacade = weatherFacade;
    }

    @GetMapping("/weather")
    public WeatherResponse getWeather(
            @RequestParam("address") String address,
            @RequestParam(name = "demo", defaultValue = "false") boolean demo) {
        return weatherFacade.getWeatherData(address, demo);
    }
}