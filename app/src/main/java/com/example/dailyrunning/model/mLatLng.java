package com.example.dailyrunning.model;

public class mLatLng {
    private double latitude;
    private double longitude;

    public mLatLng() {
    }

    public mLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

}