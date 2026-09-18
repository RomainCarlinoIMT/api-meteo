package com.example.apimeteo.clients;

import java.io.IOException;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Locale;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.example.apimeteo.models.Coordinate;
import com.example.apimeteo.services.GeocodingService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Component
@ConditionalOnProperty(name = "geo.provider", havingValue = "ban")
public class BanGeocodingClient implements GeocodingService 
{
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public Coordinate getCoordinate(String query)
    {
        String encodedAddress = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format(Locale.US, "https://api-adresse.data.gouv.fr/search/?q=%s&limit=1", encodedAddress);

        HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

        try
        {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) 
            {
                throw new RuntimeException("Failed to call BAN API");
            }

            BanResponseDto dto = parseResponse(response.body());
            if (dto.features == null || dto.features.isEmpty())
            {
                throw new RuntimeException("Unable to extract address from BAN response : " + query);
            }

            double longitude = dto.features.get(0).geometry.coordinates.get(0);
            double latitude = dto.features.get(0).geometry.coordinates.get(1);
            
            return new Coordinate(latitude, longitude);

        } 
        catch (IOException | InterruptedException ex) 
        {
            ex.printStackTrace();
            throw new RuntimeException("Failed to query BAN API", ex);
        }        
    }

    private BanResponseDto parseResponse(String json)
    {
        System.out.println("Tried to use function with : " + json);
        try 
        {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, BanResponseDto.class);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Failed to parse JSON BAN", ex);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BanResponseDto
    {
        public List<Feature> features;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Feature
    {
        public Geometry geometry;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry
    {
        public List<Double> coordinates;
    }
}
