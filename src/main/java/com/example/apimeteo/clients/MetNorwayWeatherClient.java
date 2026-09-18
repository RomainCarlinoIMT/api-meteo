package com.example.apimeteo.clients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.example.apimeteo.services.WeatherService;

@Component
@ConditionalOnProperty(name = "weather.provider", havingValue = "met-norway")
public class MetNorwayWeatherClient implements WeatherService 
{
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String getForecast(Double latitude, Double longitude)
    {
        String url = String.format(Locale.US, 
                "https://api.met.no/weatherapi/locationforecast/2.0/compact?lat=%.4f&lon=%.4f", 
                latitude, longitude);
        
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

            return response.body();
        } 
        catch (IOException | InterruptedException ex)
        {
            ex.printStackTrace();
            throw new RuntimeException("Failed to query MET Norway API", ex);
        }
    }
}
