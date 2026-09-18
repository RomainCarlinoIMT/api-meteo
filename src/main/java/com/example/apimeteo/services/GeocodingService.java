package com.example.apimeteo.services;

import com.example.apimeteo.models.Coordinate;

public interface GeocodingService 
{
    Coordinate getCoordinate(String query);
}
