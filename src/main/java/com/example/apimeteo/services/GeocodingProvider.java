package com.example.apimeteo.services;

import com.example.apimeteo.models.Coordinate;

public interface GeocodingProvider {
    Coordinate getCoordinate(String query);
}