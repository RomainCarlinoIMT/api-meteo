package com.example.apimeteo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.apimeteo.facades.WeatherFacade;

@RestController
public class WeatherController {
    private final WeatherFacade weatherFacade;

    public WeatherController(WeatherFacade weatherFacade) {
        this.weatherFacade = weatherFacade;
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam("address") String address) {
        return weatherFacade.getWeatherData(address);
    }
}