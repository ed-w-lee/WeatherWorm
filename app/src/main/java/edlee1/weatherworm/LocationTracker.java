package edlee1.weatherworm;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * For getting a one-time location based on latitude and longitude from the user.
 *
 * Based off of d.danailov's answer on:
 * http://stackoverflow.com/questions/17519198/how-to-get-the-current-location-latitude-and-longitude-in-android?rq=1
 */

public class LocationTracker implements LocationListener{
    private static String TAG = LocationTracker.class.getName();
    private Context mContext;
    private double longitude, latitude;
    private boolean trackingInOperation;
    private String providerInfo;
    public LocationTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    // Get the location
    public void getLocation() {
        try {
            LocationManager locationManager =
                    (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            // Use GPS if possible. If not, try using network.
            trackingInOperation = false;
            if (isGPSEnabled) {
                int gpsPermission = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (gpsPermission == PackageManager.PERMISSION_GRANTED) {
                    trackingInOperation = true;
                    providerInfo = LocationManager.GPS_PROVIDER;
                }
            }
            if (!trackingInOperation && isNetworkEnabled) {
                int netPermission = ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
                if (netPermission == PackageManager.PERMISSION_GRANTED) {
                    trackingInOperation = true;
                    providerInfo = LocationManager.NETWORK_PROVIDER;
                }
            }

            // Get the location
            if (providerInfo != null && !providerInfo.isEmpty()) {
                locationManager.requestSingleUpdate(providerInfo, this, null);

                Location location = locationManager.getLastKnownLocation(providerInfo);
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to retrieve location", e);
        }
    }

    public boolean getTrackingInOperation() {
        return trackingInOperation;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
