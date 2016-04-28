package com.tomorrowdev.badulakemapcreator;

/**
 * gabriel.esteban.gullon@gmail.com, May 2015
 */
public class Badulake {
    int id;
    String name;
    double longitude;
    double latitude;
    boolean alwaysopened;

    public boolean isAlwaysopened() {
        return alwaysopened;
    }

    public void setAlwaysopened(boolean alwaysopened) {
        this.alwaysopened = alwaysopened;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
