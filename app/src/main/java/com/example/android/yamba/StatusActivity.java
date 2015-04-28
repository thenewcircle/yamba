package com.example.android.yamba;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class StatusActivity extends AppCompatActivity implements
        View.OnClickListener, TextWatcher,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = StatusActivity.class.getSimpleName();
    private static final int UPDATE_INTERVAL = 5 * 1000;
    private static final int FASTEST_UPDATE_INTERVAL = 1 * 1000;

    private int mDefaultColor;

    private Button mPostButton;
    private EditText mTextStatus;
    private TextView mTextCount;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLatestLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Action bar stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_status);

        mPostButton = (Button) findViewById(R.id.status_button);
        mTextStatus = (EditText) findViewById(R.id.status_text);
        mTextCount = (TextView) findViewById(R.id.status_text_count);

        mPostButton.setOnClickListener(this);
        mTextStatus.addTextChangedListener(this);

        mDefaultColor = mTextCount.getTextColors().getDefaultColor();

        mTextStatus.setText(getIntent()
                .getStringExtra(StatusUpdateService.EXTRA_MESSAGE));

        checkPlayServices();

        //Build the Google API client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Add location updates monitoring
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
        }

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onClick(View v) {
        String status = mTextStatus.getText().toString();

        //Send the update to our background service
        Intent intent = new Intent(this, StatusUpdateService.class);
        intent.putExtra(StatusUpdateService.EXTRA_MESSAGE, status);
        if (mLatestLocation != null) {
            intent.putExtra(StatusUpdateService.EXTRA_LOCATION, mLatestLocation);
        }

        startService(intent);

        //We're done here
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int count = 140 - s.length();
        mTextCount.setText(Integer.toString(count));

        if (count < 10) {
            mTextCount.setTextColor(Color.RED);
        } else {
            mTextCount.setTextColor(mDefaultColor);
        }

        mPostButton.setEnabled(count >= 0);
    }

    /* Play Services Location */

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "Connected to Play Services");

        //Check user preference for items.
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        if (prefs.getBoolean("postLocation", false)) {
            //Get last known location immediately
            mLatestLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            //Register for updates
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } else {
            mLatestLocation = null;
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "Play Services Connection Suspended: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "Play Services Connection Failed: "
                + result.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Received location update");
        mLatestLocation = location;
    }

    private void checkPlayServices() {
        //Verify play services is active and up to date
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        switch (resultCode) {
            case ConnectionResult.SUCCESS:
                Log.d(TAG, "Google Play Services is ready to go!");
                break;
            default:
                showPlayServicesError(resultCode);
                break;
        }
    }

    private void showPlayServicesError(int errorCode) {
        // Get the error dialog from Google Play services
        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                this,
                1000 /* RequestCode */);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {
            // Create a new DialogFragment for the error dialog
            ErrorDialogFragment errorFragment =
                    ErrorDialogFragment.newInstance(errorDialog);
            // Show the error dialog in the DialogFragment
            errorFragment.show(
                    getFragmentManager(),
                    "Location Updates");
        }
    }
}
