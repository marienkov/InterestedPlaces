package com.volm.interestedplaces.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.volm.interestedplaces.R;
import com.volm.interestedplaces.core.data.Place;
import com.volm.interestedplaces.core.manager.NetworkRequestManager;
import com.volm.interestedplaces.core.provider.PlaceProvider;
import com.volm.interestedplaces.loader.PlaceListLoader;

import java.util.ArrayList;
import java.util.List;

public class PlaceListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Place>> {

    private final static int PLACE_LOADER_ID = 1;

    private RecyclerView placesRecycleView;
    private RecyclerView.Adapter placesRecycleAdapter;
    private RecyclerView.LayoutManager placesLayoutManager;

    private List<Place> placeList;

    private OnPlaceChosenListener onPlaceChosenListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onPlaceChosenListener = (OnPlaceChosenListener) activity;
        }
        catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        placeList = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.place_list_fragment, container, false);
        placesRecycleView = (RecyclerView) rootView.findViewById(R.id.placesRecycleView);
        initPlacesLayoutManager(getResources().getConfiguration());
        placesRecycleAdapter = new PlacesRecycleAdapter();
        placesRecycleView.setLayoutManager(placesLayoutManager);
        placesRecycleView.setAdapter(placesRecycleAdapter);

        getLoaderManager().initLoader(PLACE_LOADER_ID, getArguments(), this);

        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initPlacesLayoutManager(newConfig);
    }

    private void initPlacesLayoutManager(Configuration configuration) {
        switch (configuration.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                placesLayoutManager = new GridLayoutManager(getActivity(), 1);
                placesRecycleView.setLayoutManager(placesLayoutManager);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                placesLayoutManager = new GridLayoutManager(getActivity(), 2);
                placesRecycleView.setLayoutManager(placesLayoutManager);
                break;
        }
    }

    public void updateCurrentPlaceList(String keyword) {
        Bundle bundle = new Bundle();
        bundle.putString(PlaceListLoader.KEYWORD_KEY, keyword);
        getLoaderManager().restartLoader(PLACE_LOADER_ID, bundle, PlaceListFragment.this);
    }

    @Override
    public Loader<List<Place>> onCreateLoader(int id, Bundle args) {
        return new PlaceListLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<Place>> loader, List<Place> data) {
        placeList = data;
        placesRecycleAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Place>> loader) {
    }

    public interface OnPlaceChosenListener {
        void onPlaceChosen(String placeId);
    }

    private class PlacesRecycleAdapter extends RecyclerView.Adapter<PlacesRecycleAdapter.ViewHolder> {
        @Override
        public PlacesRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_thumb, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Place place = placeList.get(position);
            holder.icon.setImageUrl(place.getIcon(), NetworkRequestManager.getInstance(getActivity()).getImageLoader());
            holder.name.setText(String.format(getString(R.string.place_name_format), place.getName()));
            holder.address.setText(String.format(getString(R.string.place_address_format), place.getAddress()));
            holder.distance.setText(String.format(getString(R.string.place_distance_format), (int) place.getDistanceTo(PlaceProvider.getBestLocation(getActivity()))));
            if (onPlaceChosenListener != null) {
                holder.rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onPlaceChosenListener.onPlaceChosen(place.getId());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return placeList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View rootView;
            public NetworkImageView icon;
            public TextView name;
            public TextView address;
            public TextView distance;

            public ViewHolder(View view) {
                super(view);
                this.rootView = view;
                this.icon = (NetworkImageView) view.findViewById(R.id.tileIcon);
                this.name = (TextView) view.findViewById(R.id.name);
                this.address = (TextView) view.findViewById(R.id.address);
                this.distance = (TextView) view.findViewById(R.id.distance);
            }
        }
    }

}
