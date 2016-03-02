package com.example.srinidhi.augmentiiitd;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener/*, com.google.android.gms.location.LocationListener*/ {

    private TextView yawVal, pitchVal, rollVal;
    private TextView latitude, longitude;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float oldYaw, oldPitch, oldRoll;
    private static float thresh = 1.0f;

    GoogleApiClient mGoogleApiClient;
    private Location currentLoc;

    LocationRequest locRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yawVal = (TextView)findViewById(R.id.yawVal) ;
        pitchVal = (TextView)findViewById(R.id.pitchVal) ;
        rollVal = (TextView)findViewById(R.id.rollVal) ;
        latitude = (TextView)findViewById(R.id.latVal) ;
        longitude = (TextView)findViewById(R.id.longVal) ;


        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mSensor, SensorManager.SENSOR_DELAY_NORMAL);
//        if(mGoogleApiClient.isConnected())
//            startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float yaw_angle = event.values[0];
        float pitch_angle = event.values[1];
        float roll_angle = event.values[2];

        if (Math.abs(yaw_angle - oldYaw) > 2*thresh)
            yawVal.setText(String.valueOf(yaw_angle));
        if(Math.abs(pitch_angle - oldPitch) > 2*thresh)
            pitchVal.setText(String.valueOf(pitch_angle));
        if(Math.abs(roll_angle - oldRoll) > thresh)
            rollVal.setText(String.valueOf(roll_angle));

        oldYaw = yaw_angle;
        oldPitch = pitch_angle;
        oldRoll = roll_angle;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nothing for now.
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        currentLoc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(currentLoc != null)
        {
            latitude.setText(String.valueOf(currentLoc.getLatitude()));
            longitude.setText(String.valueOf(currentLoc.getLongitude()));
        }

//        startLocationUpdates();

    }

//    protected void startLocationUpdates() {
//        LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, locRequest, this);
//
//    }

    protected void createLocationRequest() {
        locRequest = new LocationRequest();
        locRequest.setInterval(10000);
        locRequest.setFastestInterval(5000);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    @Override
//    public void onLocationChanged(Location location) {
//        currentLoc = location;
//        latitude.setText(String.valueOf(currentLoc.getLatitude()));
//        longitude.setText(String.valueOf(currentLoc.getLongitude()));
//    }

}
