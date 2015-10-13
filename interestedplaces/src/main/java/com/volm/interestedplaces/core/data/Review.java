package com.volm.interestedplaces.core.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Review {
    private short rating;
    private String author_name;
    private String author_url;
    private String text;
    private long time;

    public static List<Review> getReviewListFromJson(JSONArray result) {
        List<Review> reviewList = new ArrayList<>();
        try {
            for (int i = 0; i < result.length(); i++) {
                reviewList.add(getReviewFromJson(result.getJSONObject(i)));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return reviewList;
    }

    public static Review getReviewFromJson(JSONObject reviewJson) {
        Review review = new Review();
        try {
            review.rating = (short) reviewJson.getInt("rating");
            review.author_name = reviewJson.getString("author_name");
            review.author_url = reviewJson.getString("author_url");
            review.text = reviewJson.getString("text");
            review.time = reviewJson.getLong("time");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return review;
    }

    public short getRating() {
        return rating;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public String getAuthor_url() {
        return author_url;
    }

    public String getText() {
        return text;
    }

    public long getTime() {
        return time;
    }
}
