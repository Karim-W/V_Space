package com.sunflower.vspace.Models;

public class LocationMarkerItems {
    private String LocationId;
    private String Name;
    private Float Lat;
    private Float Long;

    public LocationMarkerItems(String locationId, String name, Float lat, Float aLong) {
        LocationId = locationId;
        Name = name;
        Lat = lat;
        Long = aLong;
    }

    public String getLocationId() {
        return LocationId;
    }

    public void setLocationId(String locationId) {
        LocationId = locationId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Float getLat() {
        return Lat;
    }

    public void setLat(Float lat) {
        Lat = lat;
    }

    public Float getLong() {
        return Long;
    }

    public void setLong(Float aLong) {
        Long = aLong;
    }
}
