package com.volm.interestedplaces.core.provider;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.volm.interestedplaces.R;
import com.volm.interestedplaces.core.data.Place;
import com.volm.interestedplaces.core.data.PlaceDetails;
import com.volm.interestedplaces.core.manager.NetworkRequestManager;
import com.volm.interestedplaces.exceptions.LocationNotDefinedException;

import java.util.List;

public class PlaceProvider {
    private final static int MAX_SEARCH_RADIUS = 5000;
    private final static String nearby_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private final static String place_details_url = "https://maps.googleapis.com/maps/api/place/details/json?";
    private final static String place_photo = "https://maps.googleapis.com/maps/api/place/photo?";

    public static Location getBestLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
            }
        }
        return bestLocation;
    }

    public static void getNearestPlaces(Context context, final PlaceProviderListener<List<Place>> listener,
                                        Location location, String keyword) throws LocationNotDefinedException {
        if (location == null) {
            throw new LocationNotDefinedException();
        }

        final String request = new StringBuilder(nearby_url)
                .append("key=").append(context.getString(R.string.google_api_key))
                .append("&location=").append(location.getLatitude()).append(",").append(location.getLongitude())
                .append("&rankby=distance")
                .append("&keyword=").append(keyword).toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResult(Place.getPlaceListFromJson(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onFail();
                    }
                });

        stringRequest.setShouldCache(false);
        NetworkRequestManager.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void getNearestPlaces(Context context, final PlaceProviderListener<List<Place>> listener,
                                        Location location) throws LocationNotDefinedException {
        if (location == null) {
            throw new LocationNotDefinedException();
        }

        final String request = new StringBuilder(nearby_url)
                .append("key=").append(context.getString(R.string.google_api_key))
                .append("&location=").append(location.getLatitude()).append(",").append(location.getLongitude())
                .append("&radius=").append(MAX_SEARCH_RADIUS).toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResult(Place.getPlaceListFromJson(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onFail();
                    }
                });

        stringRequest.setShouldCache(false);
        NetworkRequestManager.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static void getPlaceDetails(Context context, String placeId, final PlaceProviderListener<PlaceDetails> listener) {
        final String request = new StringBuilder(place_details_url)
                .append("key=").append(context.getString(R.string.google_api_key))
                .append("&placeid=").append(placeId).toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        listener.onResult(PlaceDetails.getPlaceDetailsFromJson(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onFail();
                    }
                });

        stringRequest.setShouldCache(false);
        NetworkRequestManager.getInstance(context).addToRequestQueue(stringRequest);
    }

    public static String buildPlacePhotoUrl(Context context, String photoreference, PhotoQuality quality) {
        return new StringBuilder(place_photo)
                .append("key=").append(context.getString(R.string.google_api_key))
                .append("&photoreference=").append(photoreference)
                .append("&maxheight=").append(quality.getMaxSize())
                .append("&maxwidth=").append(quality.getMaxSize()).toString();
    }

    public static String[] buildPlacePhotoUrlList(Context context, List<String> photoReferenceList, PhotoQuality quality) {
        String[] photoUrls = new String[photoReferenceList.size()];
        for (int i = 0; i < photoReferenceList.size(); i++) {
            photoUrls[i] = new StringBuilder(place_photo)
                    .append("key=").append(context.getString(R.string.google_api_key))
                    .append("&photoreference=").append(photoReferenceList.get(i))
                    .append("&maxheight=").append(quality.getMaxSize())
                    .append("&maxwidth=").append(quality.getMaxSize()).toString();
        }
        return photoUrls;
    }

    public enum PhotoQuality {
        LOW(100), MEDIUM(400), HIGH(800);

        int maxSize;

        PhotoQuality(int maxSize) {
            this.maxSize = maxSize;
        }

        int getMaxSize() {
            return maxSize;
        }
    }

    public interface PlaceProviderListener<T> {
        void onResult(T result);
        void onFail();
    }
}
