package com.application.earthquaquewatcher.Model;

import java.io.Serializable;

public class EarthQuake implements Serializable {
    private String place;
    private Long time;
    private Double magnitude;
    private String urlDetails;
    private Double lat;
    private Double lon;
    private String type;

    public EarthQuake(String place, Long time, Double magnitude, String urlDetails, Double lat, Double lon, String type) {
        this.place = place;
        this.time = time;
        this.magnitude = magnitude;
        this.urlDetails = urlDetails;
        this.lat = lat;
        this.lon = lon;
        this.type = type;
    }

    public EarthQuake() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public String getUrlDetails() {
        return urlDetails;
    }

    public void setUrlDetails(String urlDetails) {
        this.urlDetails = urlDetails;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
