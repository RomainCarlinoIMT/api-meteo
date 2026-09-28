package com.example.apimeteo.services;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.apimeteo.models.Coordinate;

@Service
public class CachedGeocodingService implements GeocodingService {
    private final GeocodingProvider geocodingProvider;

    public CachedGeocodingService(GeocodingProvider geocodingProvider) {
        this.geocodingProvider = geocodingProvider;
    }

    @Override
    @Cacheable(
            cacheNames = "geocoding",
            key = "#p0.trim().toLowerCase(T(java.util.Locale).ROOT)")
    public Coordinate getCoordinate(String query) {
        return geocodingProvider.getCoordinate(query);
    }
}