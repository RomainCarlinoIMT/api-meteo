package com.example.apimeteo.clients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.example.apimeteo.models.Coordinate;
import com.example.apimeteo.services.GeocodingService;

@Component 
public class NominatimClient implements  GeocodingService
{
    private final HttpClient mHttpClient = HttpClient.newHttpClient();
    private static final String URL = "https://nominatim.openstreetmap.org/search?q=";

    @Override
    public Coordinate getCoordinate(String query) 
    {
        String requestURL = URL + query + "&format=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURL))
                .header("User-Agent", "WeatherApp-EducationalTP") // Nominatim exige un User-Agent
                .GET()
                .build();
        
        try 
        {
            HttpResponse<String> response = mHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // TODO : parsing
            return parsCoordinateFromJson(response.body());
        } 
        catch (IOException | InterruptedException ex) 
        {
            ex.printStackTrace();
            throw new RuntimeException("Failed to call Nominatim", ex);
        }
    }

    private Coordinate parsCoordinateFromJson(String json)
    {
        System.out.println("Tried to use fonction with : " + json);
        try {
        Pattern latPattern = Pattern.compile("\"lat\"\\s*:\\s*\"([^\"]+)\"");
        Pattern lonPattern = Pattern.compile("\"lon\"\\s*:\\s*\"([^\"]+)\"");

        Matcher latMatcher = latPattern.matcher(json);
        Matcher lonMatcher = lonPattern.matcher(json);

        if (latMatcher.find() && lonMatcher.find()) {
            double latitude = Double.parseDouble(latMatcher.group(1));
            double longitude = Double.parseDouble(lonMatcher.group(1));
            return new Coordinate(latitude, longitude);
        }
        
        throw new RuntimeException("No coordinate found");
    } catch (NumberFormatException e) {
        throw new RuntimeException("Error during parsing of coordinates", e);
    }
    }
}
