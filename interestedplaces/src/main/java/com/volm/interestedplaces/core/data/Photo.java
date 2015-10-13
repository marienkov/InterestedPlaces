package com.volm.interestedplaces.core.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Photo {
    private int width;
    private int height;
    private String ref;

    public static List<Photo> getPhotoReferenceListFromJson(JSONArray result) {
        List<Photo> photoList = new ArrayList<>();
        try {
            for (int i = 0; i < result.length(); i++) {
                photoList.add(getPhotoReferenceFromJson(result.getJSONObject(i)));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return photoList;
    }

    public static Photo getPhotoReferenceFromJson(JSONObject photoReferenceJson) {
        Photo photo = new Photo();
        try {
            photo.width = photoReferenceJson.getInt("width");
            photo.height = photoReferenceJson.getInt("height");
            photo.ref = photoReferenceJson.getString("photo_reference");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return photo;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getPhotoRef() {
        return ref;
    }

}
