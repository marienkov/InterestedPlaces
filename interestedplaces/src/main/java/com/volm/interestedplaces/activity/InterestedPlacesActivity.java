package com.volm.interestedplaces.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.volm.interestedplaces.R;
import com.volm.interestedplaces.fragments.PlaceDetailsFragment;
import com.volm.interestedplaces.fragments.PlaceListFragment;
import com.volm.interestedplaces.loader.PlaceDetailsLoader;

public class InterestedPlacesActivity extends AppCompatActivity implements PlaceListFragment.OnPlaceChosenListener {

    private final static String PLACE_LIST_FRAGMENT_KEY = "PLACE_LIST_FRAGMENT_KEY";
    private final static String PLACE_DETAILS_FRAGMENT_KEY = "PLACE_DETAILS_FRAGMENT_KEY";
    private View rootView;
    private Toolbar toolbar;
    private EditText keywordEditText;
    private Button findButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.interested_activity);
        rootView = findViewById(R.id.rootView);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        keywordEditText = (EditText) toolbar.findViewById(R.id.keywordEditText);
        keywordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        hideKeyboard();
                        loadPlaces();
                        return true;
                }
                return false;
            }
        });
        keywordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    hideKeyboard();
                    loadPlaces();
                    return true;
                }
                return false;
            }
        });

        findButton = (Button) toolbar.findViewById(R.id.findButton);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                loadPlaces();
            }
        });

        loadPlaces();
    }

    private void loadPlaces() {
        String keyword = keywordEditText.getText().toString();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PLACE_LIST_FRAGMENT_KEY);
        if (fragment == null) {
            fragment = new PlaceListFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PlaceDetailsLoader.PLACE_ID_KEY, keyword);
            fragment.setArguments(bundle);
        } else {
            ((PlaceListFragment) fragment).updateCurrentPlaceList(keyword);
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right)
                .addToBackStack(null)
                .replace(R.id.contant_fragment, fragment, PLACE_LIST_FRAGMENT_KEY).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();
    }

    @Override
    public void onPlaceChosen(String placeId) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PLACE_DETAILS_FRAGMENT_KEY);
        if (fragment == null) {
            fragment = new PlaceDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putString(PlaceDetailsLoader.PLACE_ID_KEY, placeId);
            fragment.setArguments(bundle);
        } else {
            ((PlaceDetailsFragment) fragment).updateCurrentPlaceId(placeId);
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .addToBackStack(null)
                .replace(R.id.contant_fragment, fragment, PLACE_DETAILS_FRAGMENT_KEY).commit();
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        Fragment detailedFragment = getSupportFragmentManager().findFragmentByTag(PLACE_DETAILS_FRAGMENT_KEY);
        Fragment listFragment = getSupportFragmentManager().findFragmentByTag(PLACE_LIST_FRAGMENT_KEY);
        if (listFragment != null && detailedFragment != null && detailedFragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                    .replace(R.id.contant_fragment, listFragment, PLACE_LIST_FRAGMENT_KEY).commit();
        } else {
            super.onBackPressed();
        }
    }
}
