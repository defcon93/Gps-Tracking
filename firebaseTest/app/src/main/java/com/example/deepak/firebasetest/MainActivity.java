package com.example.deepak.firebasetest;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.shaded.apache.http.HttpResponse;
import org.shaded.apache.http.client.ClientProtocolException;
import org.shaded.apache.http.client.HttpClient;
import org.shaded.apache.http.client.methods.HttpGet;
import org.shaded.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;


    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    //private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 300000  ;
    private static int FATEST_INTERVAL = 180000  ;
    private static int DISPLACEMENT = 2;
    private TextView gps;

    TelephonyManager tm;
    Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        gps = (TextView) findViewById(R.id.gps);
        


         tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);




            if(checkPlayServices())
        {
            buildGoogleApiClient();

            createLocationRequest();

            try {
                mGoogleApiClient.connect();
            } catch (Exception e) {
                Toast.makeText(this.getApplicationContext(), "connection Error", Toast.LENGTH_LONG);
            }

            //startLocationUpdates();
        }
            else
            {
                Toast.makeText(this.getApplicationContext(), "connection Error second", Toast.LENGTH_LONG);

            }



    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        setLocation();

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {

        startLocationUpdates();


    }

    @Override
    public void onConnectionSuspended(int i) {

        //mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        setLocation();

    }

    private void setLocation()  {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        gps.setText("" + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
        String deviceID = tm.getDeviceId();
        //new AsyncCall().execute(url);
        //Firebase lat = myFirebaseRef.child(deviceID).child("lat");
       //Firebase lon= myFirebaseRef.child(deviceID).child("lon");
        //lon.setValue("" + mLastLocation.getLongitude());
        //lat.setValue("" + mLastLocation.getLatitude());
    }

    /*private class AsyncCall extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... urls) {

            makeGetRequest(urls[0]);

            return null;
        }*/




    }

    /*private void makeGetRequest(String url) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        // replace with your url

        HttpResponse response;
        try {
            response = client.execute(request);

            Log.d("Response of GET request", response.toString());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }*/

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        gps.setText("conncetion failed");

    }


    /*protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        //if (mGoogleApiClient.isConnected()) {
            //startLocationUpdates();
        //}
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

}
