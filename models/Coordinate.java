package models;

public class Coordinate {
    private Double mLatitude;
    private Double mLongitude;

    public Coordinate(double latitude, double longitude)
    {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public Double getLatitude()
    {
        return mLatitude;
    }

    public Double getLongitude()
    {
        return mLongitude;
    }
}
