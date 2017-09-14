package com.botree.locationheartbeat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.location.LocationProvider;
import java.util.Locale;
import android.widget.Toast;
/**
 * Created by RAVI on 12-09-2017.
 */

public class LocationHeartBeatService extends Service {
    private static final String TAG = "LocationHeartBeatService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 60000 * 5;
    private static final float LOCATION_DISTANCE = 0;


    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
           // Toast.makeText(LocationHeartBeatService.this, "onLocationChanged: " + location, Toast.LENGTH_LONG).show();
            Intent intent = new Intent("MONITOR_LOCATION");
            intent.putExtra("Latitude", location.getLatitude());
            intent.putExtra("Longitude", location.getLongitude());
            sendBroadcast(intent);
            mLastLocation.set(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
            // Intent intent = new Intent("MONITOR_LOCATION");
            // intent.putExtra("Latitude", 0);
            // intent.putExtra("Longitude", 0);
            // sendBroadcast(intent);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
            if(status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                Intent intent = new Intent("MONITOR_LOCATION");
                intent.putExtra("Latitude", 0);
                intent.putExtra("Longitude", 0);
                sendBroadcast(intent);
            }
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        // Toast.makeText(LocationHeartBeatService.this, "onStartCommand " + intent.getIntExtra("INTERVAL", 5), Toast.LENGTH_LONG).show();
        // LOCATION_INTERVAL = LOCATION_INTERVAL * intent.getIntExtra("INTERVAL", 5);
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        // Toast.makeText(LocationHeartBeatService.this, "onCreate", Toast.LENGTH_LONG).show();
        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        // Toast.makeText(LocationHeartBeatService.this, "onDestroy", Toast.LENGTH_LONG).show();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
