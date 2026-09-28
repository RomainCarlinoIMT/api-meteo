package com.example.apimeteo.clients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.example.apimeteo.models.HourlyForecast;
import com.example.apimeteo.services.WeatherService;

@Component
@ConditionalOnProperty(name = "weather.provider", havingValue = "open-meteo", matchIfMissing = true)
public class OpenMeteoClient implements WeatherService{
    private final HttpClient mHttpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    @Value("${openmeteo.api.url:https://api.open-meteo.com}")
    private String apiUrl;

    public OpenMeteoClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<HourlyForecast> getForecast(Double latitude, Double longitude)
    {
        String url = String.format(Locale.US, 
            "%s/v1/forecast?latitude=%.4f&longitude=%.4f&hourly=temperature_2m&timezone=UTC&timeformat=unixtime",
            apiUrl, latitude, longitude
        );
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try
        {
            HttpResponse<String> response = mHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return parseForecast(response.body());
        }
        catch (IOException | InterruptedException ex) 
        {
            ex.printStackTrace();
            throw new RuntimeException("Failed to call Open-meteo", ex);
        }
    }

    private List<HourlyForecast> parseForecast(String json) {
        try {
            JsonNode hourly = objectMapper.readTree(json).path("hourly");
            JsonNode times = hourly.path("time");
            JsonNode temperatures = hourly.path("temperature_2m");
            List<HourlyForecast> forecasts = new ArrayList<>();
            int count = Math.min(times.size(), temperatures.size());
            for (int index = 0; index < count; index++) {
                JsonNode time = times.get(index);
                JsonNode temperature = temperatures.get(index);
                if (time.isNumber() && temperature.isNumber()) {
                    forecasts.add(new HourlyForecast(
                            Instant.ofEpochSecond(time.asLong()).toString(),
                            temperature.doubleValue()));
                }
            }
            return forecasts;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to parse Open-Meteo forecast", ex);
        }
    }
}
