package compass.compass;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static compass.compass.MainActivity.currentProfile;

public class LocationService extends Service implements LocationListener{

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    DatabaseReference mDatabase;

    LocationManager locationManager;
    Location location; // location
    double latitude = 0; // latitude
    double longitude = 0; // longitude

    Location tempLocation;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1 * 10000;

    String provider;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // This won't be a bound service, so simply return null
        return null;
    }

    @Override
    public void onCreate() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tempLocation = new Location(NETWORK_PROVIDER);
        // This will be called when your Service is created for the first time
        // Just do any operations you need in this method.
        locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
        Log.i("HEYOO", "in onCreate");
        getLocation();

    }

    public Location getLocation() {
        try {
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled. DEFAULT COORDINATES


            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return tempLocation;
                    }
                    locationManager.requestLocationUpdates(NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                this);
                        Log.d("GPPPPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(currentProfile != null) {
            mDatabase.child("Users").child(currentProfile.userId).child("latitude").setValue(latitude);
            mDatabase.child("Users").child(currentProfile.userId).child("longitude").setValue(longitude);
        }
        Log.i("LOCATION", "Latitude: " + latitude + "- Longitude: " + longitude);


        return location;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if(currentProfile != null) {
            mDatabase.child("Users").child(currentProfile.userId).child("latitude").setValue(latitude);
            mDatabase.child("Users").child(currentProfile.userId).child("longitude").setValue(longitude);
        }
        Log.i("NEW LOCATION", "Latitude: " + latitude + "- Longitude: " + longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
