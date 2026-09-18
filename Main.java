import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import clients.NominatimClient;
import clients.OpenMeteoClient;

import interfaces.WeatherFacade;

import service.GeocodingService;
import service.WeatherService;

import controllers.WeatherController;

public class Main 
{
    public static void main(String[] args) throws IOException
    {
        GeocodingService mGeocoding = new NominatimClient();
        WeatherService mWeather = new OpenMeteoClient();

        WeatherFacade weatherFacade = new WeatherFacade(mGeocoding, mWeather);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/weather", new WeatherController(weatherFacade));
        server.setExecutor(null);
        server.start();

        System.out.println("Serveur started at http://localhost:8080");
    }    
}
