package com.volm.interestedplaces.core.data;

import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceDetails {

    private String placeId = "";
    private String formatted_address = "";
    private String formatted_phone_number = "";
    private String international_phone_number = "";
    private String icon_url = "";
    private String name = "";
    private Location location;
    private double rating = -1;
    private List<Review> reviewList;
    private List<Photo> photoList;

    public static PlaceDetails getPlaceDetailsFromJson(String placeDetailsJson) {
        PlaceDetails placeDetails = null;
        try {
            JSONObject resultJson = new JSONObject(placeDetailsJson);
            resultJson = resultJson.getJSONObject("result");
            placeDetails = new PlaceDetails();
            placeDetails.placeId = resultJson.getString("place_id");
            placeDetails.formatted_address = resultJson.getString("formatted_address");
            placeDetails.icon_url = resultJson.getString("icon");
            placeDetails.name = resultJson.getString("name");
            placeDetails.location = new Location("");
            JSONObject locationJson = resultJson.getJSONObject("geometry").getJSONObject("location");
            placeDetails.location.setLongitude(locationJson.getDouble("lng"));
            placeDetails.location.setLatitude(locationJson.getDouble("lat"));
            if (resultJson.has("formatted_phone_number")) {
                placeDetails.formatted_phone_number = resultJson.getString("formatted_phone_number");
            }
            if (resultJson.has("international_phone_number")) {
                placeDetails.international_phone_number = resultJson.getString("international_phone_number");
            }
            if (resultJson.has("rating")) {
                placeDetails.rating = resultJson.getDouble("rating");
            }
            if (resultJson.has("reviews")) {
                JSONArray reviewJsonArray = resultJson.getJSONArray("reviews");
                placeDetails.reviewList = Review.getReviewListFromJson(reviewJsonArray);
            } else {
                placeDetails.reviewList = new ArrayList<>();
            }
            if (resultJson.has("photos")) {
                JSONArray photoRefJsonArray = resultJson.getJSONArray("photos");
                placeDetails.photoList = Photo.getPhotoReferenceListFromJson(photoRefJsonArray);
            } else {
                placeDetails.photoList = new ArrayList<>();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return placeDetails;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public String getPlaceId() {
        return placeId;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public Location getLocation() {
        return location;
    }

    public float getDistanceTo(Location location) {
        return this.location.distanceTo(location);
    }
}
