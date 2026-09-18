package clients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;

import service.WeatherService;

public class OpenMeteoClient implements WeatherService{
    private final HttpClient mHttpClient = HttpClient.newHttpClient();

    @Override
    public String getForecast(Double latitude, Double longitude) 
    {
        String url = String.format(Locale.US, 
            "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&hourly=shortwave_radiation", 
            latitude, longitude
        );
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try
        {
            HttpResponse<String> response = mHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();    
        }
        catch (IOException | InterruptedException ex) 
        {
            ex.printStackTrace();
            throw new RuntimeException("Failed to call Open-meteo", ex);
        }
    }
}
