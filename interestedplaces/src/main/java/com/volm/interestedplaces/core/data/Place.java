package com.volm.interestedplaces.core.data;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Place {
    private String icon;
    private String name;
    private String address;
    private String id;
    private Location location;
    private List<Photo> photoList;

    public static List<Place> getPlaceListFromJson(String result) {
        List<Place> placeList = new ArrayList<>();
        try {
            JSONObject resultJson = new JSONObject(result);
            JSONArray placesJsonArray = resultJson.getJSONArray("results");
            for (int i = 0; i < placesJsonArray.length(); i++) {
                placeList.add(getPlaceFromJson(placesJsonArray.getJSONObject(i)));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return placeList;
    }

    public static Place getPlaceFromJson(JSONObject placeJson) {
        Place place = null;
        try {
            place = new Place();
            place.icon = placeJson.getString("icon");
            place.name = placeJson.getString("name");
            place.address = placeJson.getString("vicinity");
            place.id = placeJson.getString("place_id");

            place.location = new Location("");
            JSONObject locationJson = placeJson.getJSONObject("geometry").getJSONObject("location");
            place.location.setLongitude(locationJson.getDouble("lng"));
            place.location.setLatitude(locationJson.getDouble("lat"));

            if (placeJson.has("photos")) {
                JSONArray photoRefJsonArray = placeJson.getJSONArray("photos");
                place.photoList = Photo.getPhotoReferenceListFromJson(photoRefJsonArray);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }

    public Location getLocation() {
        return location;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public String getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public float getDistanceTo(Location location) {
        return this.location.distanceTo(location);
    }

}
