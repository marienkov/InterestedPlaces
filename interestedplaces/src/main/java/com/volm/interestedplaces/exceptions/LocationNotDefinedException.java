package com.volm.interestedplaces.exceptions;

public class LocationNotDefinedException extends Exception {
    public LocationNotDefinedException() {
        super("Location is unknown or null");
    }
}
