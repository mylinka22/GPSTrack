package com.example.gpstrack;

public class GeoLocation {
    private double latitude;
    private double longitude;

    public GeoLocation() {
        // Пустой конструктор требуется для Firebase
    }

    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
