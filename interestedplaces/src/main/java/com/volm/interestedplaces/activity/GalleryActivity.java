package com.volm.interestedplaces.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.toolbox.NetworkImageView;
import com.volm.interestedplaces.R;
import com.volm.interestedplaces.core.manager.NetworkRequestManager;

public class GalleryActivity extends Activity {

    public final static String PHOTOS_URL_KEY = "PHOTOS_URL_KEY";
    public final static String START_PHOTO = "START_PHOTO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity);

        String[] photoUrl = getIntent().getStringArrayExtra(PHOTOS_URL_KEY);
        int startPhoto = getIntent().getIntExtra(START_PHOTO, 0);

        ViewPager photoViewParent = (ViewPager) findViewById(R.id.photoPager);
        photoViewParent.setAdapter(new ImageViewAdapter(photoUrl));
        photoViewParent.setCurrentItem(startPhoto);
    }

    private class ImageViewAdapter extends PagerAdapter {
        private String[] photoUrl;

        public ImageViewAdapter(String[] photoUrl) {
            this.photoUrl = photoUrl;
        }

        @Override
        public int getCount() {
            return photoUrl.length;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            NetworkImageView imageView = new NetworkImageView(GalleryActivity.this);
            imageView.setImageUrl(photoUrl[position], NetworkRequestManager.getInstance(GalleryActivity.this).getImageLoader());
            container.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}