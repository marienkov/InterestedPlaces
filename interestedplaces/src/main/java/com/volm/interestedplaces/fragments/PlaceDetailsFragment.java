package com.volm.interestedplaces.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.volm.interestedplaces.R;
import com.volm.interestedplaces.activity.GalleryActivity;
import com.volm.interestedplaces.core.data.Photo;
import com.volm.interestedplaces.core.data.PlaceDetails;
import com.volm.interestedplaces.core.data.Review;
import com.volm.interestedplaces.core.manager.NetworkRequestManager;
import com.volm.interestedplaces.core.provider.PlaceProvider;
import com.volm.interestedplaces.loader.PlaceDetailsLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlaceDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<PlaceDetails> {

    private final static int PLACE_DETAILS_LOADER = 2;

    private NetworkImageView icon;
    private TextView name;
    private TextView address;
    private TextView phone;
    private RatingBar reviewRatingBar;
    private TextView distance;
    private RecyclerView photoRecycleView;
    private RecyclerView reviewRecyclerView;

    private PlaceDetails placeDetails;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.place_details, container, false);
        icon = (NetworkImageView) rootView.findViewById(R.id.icon);
        name = (TextView) rootView.findViewById(R.id.name);
        address = (TextView) rootView.findViewById(R.id.address);
        phone = (TextView) rootView.findViewById(R.id.international_phone_number);
        reviewRatingBar = (RatingBar) rootView.findViewById(R.id.reviewRatingBar);
        distance = (TextView) rootView.findViewById(R.id.distance);

        reviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.reviewRecyclerView);
        LinearLayoutManager reviewLinearLayoutManager = new LinearLayoutManager(getActivity());
        reviewLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        reviewRecyclerView.setLayoutManager(reviewLinearLayoutManager);

        photoRecycleView = (RecyclerView) rootView.findViewById(R.id.placesRecycleView);
        photoRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        getLoaderManager().initLoader(PLACE_DETAILS_LOADER, getArguments(), this);

        return rootView;
    }

    public void updateCurrentPlaceId(String currentPlaceId) {
        if (placeDetails == null || !placeDetails.getPlaceId().equals(currentPlaceId)) {
            Bundle bundle = new Bundle();
            bundle.putString(PlaceDetailsLoader.PLACE_ID_KEY, currentPlaceId);
            getLoaderManager().restartLoader(PLACE_DETAILS_LOADER, bundle, this);
        }
    }

    private void updateUI(PlaceDetails placeDetails) {
        icon.setImageUrl(placeDetails.getIcon_url(), NetworkRequestManager.getInstance(getActivity()).getImageLoader());
        name.setText(String.format(getString(R.string.place_name_format), placeDetails.getName()));
        address.setText(String.format(getString(R.string.place_address_format), placeDetails.getFormatted_address()));
        distance.setText(String.format(getString(R.string.place_distance_format), (int) placeDetails.getDistanceTo(PlaceProvider.getBestLocation(getActivity()))));

        String phoneNumber = placeDetails.getFormatted_phone_number();
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            phone.setVisibility(View.GONE);
        } else {
            phone.setVisibility(View.VISIBLE);
            phone.setText(String.format(getString(R.string.place_phone), placeDetails.getFormatted_phone_number()));
        }

        double rating = placeDetails.getRating();
        if (rating == -1) {
            reviewRatingBar.setVisibility(View.GONE);
        } else {
            reviewRatingBar.setVisibility(View.VISIBLE);
            reviewRatingBar.setRating((float) rating);
        }

        List<Review> reviewList = placeDetails.getReviewList();
        if (placeDetails.getReviewList() == null || reviewList.isEmpty()) {
            reviewRecyclerView.setVisibility(View.GONE);
        } else {
            reviewRecyclerView.setVisibility(View.VISIBLE);
            reviewRecyclerView.setAdapter(new ReviewRecycleAdapter(reviewList));
        }

        ArrayList<String> photoRefList = new ArrayList<>();
        for (Photo photo : placeDetails.getPhotoList()) {
            photoRefList.add(photo.getPhotoRef());
        }
        photoRecycleView.setAdapter(new PhotoRecycleAdapter(PlaceProvider.buildPlacePhotoUrlList(getActivity(),
                photoRefList, PlaceProvider.PhotoQuality.MEDIUM)));
    }

    @Override
    public Loader<PlaceDetails> onCreateLoader(int id, Bundle args) {
        return new PlaceDetailsLoader(getActivity(), args);
    }

    @Override
    public void onLoadFinished(Loader<PlaceDetails> loader, PlaceDetails data) {
        placeDetails = data;
        updateUI(placeDetails);
    }

    @Override
    public void onLoaderReset(Loader<PlaceDetails> loader) {
    }

    private class PhotoRecycleAdapter extends RecyclerView.Adapter<PhotoRecycleAdapter.ViewHolder> {
        private String[] photoUrl;

        public PhotoRecycleAdapter(String[] photoUrl) {
            this.photoUrl = photoUrl;
        }

        @Override
        public PhotoRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_thumb, parent, false);
            return new ViewHolder(v);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public NetworkImageView networkImageView;

            public ViewHolder(View view) {
                super(view);
                networkImageView = (NetworkImageView) view.findViewById(R.id.photo);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.networkImageView.setImageUrl(photoUrl[position], NetworkRequestManager.getInstance(getActivity()).getImageLoader());
            holder.networkImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PlaceDetailsFragment.this.getActivity(), GalleryActivity.class);
                    intent.putExtra(GalleryActivity.START_PHOTO, position);
                    intent.putExtra(GalleryActivity.PHOTOS_URL_KEY, photoUrl);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return photoUrl.length;
        }

    }

    private class ReviewRecycleAdapter extends RecyclerView.Adapter<ReviewRecycleAdapter.ViewHolder> {
        private List<Review> reviewList;

        public ReviewRecycleAdapter(List<Review> reviewList) {
            this.reviewList = reviewList;
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView authorNameTextView;
            public TextView authorUrlTextView;
            public TextView messageTextView;
            public RatingBar ratingBar;

            public ViewHolder(View view) {
                super(view);
                authorNameTextView = (TextView) view.findViewById(R.id.author_name);
                authorUrlTextView = (TextView) view.findViewById(R.id.author_url);
                messageTextView = (TextView) view.findViewById(R.id.text);
                ratingBar = (RatingBar) view.findViewById(R.id.reviewRatingBar);
            }
        }        @Override
        public ReviewRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_thumb, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            Review review = reviewList.get(position);
            holder.authorNameTextView.setText(review.getAuthor_name());
            holder.authorUrlTextView.setText(review.getAuthor_url());
            holder.messageTextView.setText(review.getText());
            holder.ratingBar.setRating(review.getRating());
        }

        @Override
        public int getItemCount() {
            return reviewList.size();
        }



    }

}
