package service;
import models.Coordinate;

public interface GeocodingService 
{
    Coordinate getCoordinate(String query);
}
