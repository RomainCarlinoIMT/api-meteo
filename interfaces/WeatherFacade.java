package interfaces;
import models.Coordinate;
import service.GeocodingService;
import service.WeatherService;

public class WeatherFacade 
{
    private final GeocodingService mGeocodingService;
    private final WeatherService mWeatherService;  
    
    public WeatherFacade(GeocodingService geocodingService, WeatherService weatherService)
    {
        this.mGeocodingService = geocodingService;
        this.mWeatherService = weatherService;
    }

    public String getWeatherData(String address) 
    {
        Coordinate coords = mGeocodingService.getCoordinate(address);
        return mWeatherService.getForecast(coords.getLatitude(), coords.getLatitude());
    }
}