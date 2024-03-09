package com.example.gpstrack;

public class LocationData {
    public double latitude;
    public double longitude;

    public LocationData() {
        // Default constructor required for calls to DataSnapshot.getValue(LocationData.class)
    }

    public LocationData(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
