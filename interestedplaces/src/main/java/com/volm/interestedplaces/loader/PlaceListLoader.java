package com.volm.interestedplaces.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

import com.volm.interestedplaces.core.data.Place;
import com.volm.interestedplaces.core.provider.PlaceProvider;
import com.volm.interestedplaces.exceptions.LocationNotDefinedException;

import java.util.List;

public class PlaceListLoader extends Loader<List<Place>> {
    public static final String KEYWORD_KEY = "KEYWORD_KEY";
    private String keyword;
    private List<Place> placeList;
    private PlaceProvider.PlaceProviderListener placeListener = new PlaceProvider.PlaceProviderListener<List<Place>>() {
        @Override
        public void onResult(List<Place> result) {
            placeList = result;
            deliverResult(placeList);
        }

        @Override
        public void onFail() {
        }
    };

    public PlaceListLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            keyword = args.getString(KEYWORD_KEY);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (placeList == null) {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        try {
            if (keyword == null || "".equals(keyword) || " ".equals(keyword)) {
                PlaceProvider.getNearestPlaces(getContext(), placeListener, PlaceProvider.getBestLocation(getContext()));
            } else {
                PlaceProvider.getNearestPlaces(getContext(), placeListener, PlaceProvider.getBestLocation(getContext()), keyword);
            }
        }
        catch (LocationNotDefinedException e) {
            e.printStackTrace();
        }
    }
}
