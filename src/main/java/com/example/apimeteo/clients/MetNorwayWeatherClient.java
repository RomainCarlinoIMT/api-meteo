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
@ConditionalOnProperty(name = "weather.provider", havingValue = "met-norway")
public class MetNorwayWeatherClient implements WeatherService 
{
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    @Value("${metnorway.api.url:https://api.met.no}")
    private String apiUrl;

    public MetNorwayWeatherClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<HourlyForecast> getForecast(Double latitude, Double longitude)
    {
        String url = String.format(Locale.US, 
                "%s/weatherapi/locationforecast/2.0/compact?lat=%.4f&lon=%.4f",
                apiUrl, latitude, longitude);
        
        HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "TP2-MeteoApi/1.0 romain.carlino@etu.mines-ales.fr")
                    .GET()
                    .build();
        
        try
        {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
            {
                throw new RuntimeException("Error from MET Norway API with code : " + response.statusCode());
            }

            return parseForecast(response.body());
        } 
        catch (IOException | InterruptedException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("Failed to query MET Norway API", ex);
        }
    }

    private List<HourlyForecast> parseForecast(String json) {
        try {
            JsonNode timeseries = objectMapper.readTree(json)
                    .path("properties")
                    .path("timeseries");
            List<HourlyForecast> forecasts = new ArrayList<>();
            for (JsonNode entry : timeseries) {
                JsonNode time = entry.path("time");
                JsonNode temperature = entry.path("data")
                        .path("instant")
                        .path("details")
                        .path("air_temperature");
                if (time.isTextual() && temperature.isNumber()) {
                    forecasts.add(new HourlyForecast(
                            Instant.parse(time.asText()).toString(),
                            temperature.doubleValue()));
                }
            }
            return forecasts;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to parse MET Norway forecast", ex);
        }
    }
}
