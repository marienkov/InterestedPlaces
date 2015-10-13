package com.volm.interestedplaces.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

import com.volm.interestedplaces.core.data.PlaceDetails;
import com.volm.interestedplaces.core.provider.PlaceProvider;

public class PlaceDetailsLoader extends Loader<PlaceDetails> {

    public final static String PLACE_ID_KEY = "PLACE_ID_KEY";
    private String placeId;
    private PlaceDetails placeDetails;

    public PlaceDetailsLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            placeId = args.getString(PLACE_ID_KEY);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (placeDetails == null) {
            forceLoad();
        }
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        PlaceProvider.getPlaceDetails(getContext(), placeId, new PlaceProvider.PlaceProviderListener<PlaceDetails>() {
            @Override
            public void onResult(PlaceDetails result) {
                placeDetails = result;
                deliverResult(result);
            }

            @Override
            public void onFail() {
            }
        });
    }
}
