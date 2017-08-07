package compass.compass.fragments;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import compass.compass.R;

import static android.content.Context.LOCATION_SERVICE;
import static compass.compass.MainActivity.currentProfile;
import static compass.compass.R.id.resourcesMap1;

/**
 * Created by brucegatete on 8/3/17.
 */

public class ResourceSchoolFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    DatabaseReference mDatabase;

    //xml layout things
    TextView tvPolice;
    TextView tvPoliceDistance;

    TextView tvCounseling;
    TextView tvCounselingDistance;

    TextView tvHospital;
    TextView tvHospitalDistance;

    //location
    Double latitude;
    Double longitude;
    Map location;
    LocationManager locationManager;
    Location myLocation;
    Location temp;
    String policePhone, counselPhone, hospitalPhone;
    LatLng hospitalLatLng, policeLatLng, counselingLatLng;
    CardView cvPolice1, cvCounseling1, cvHospital1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_resources_school, container, false);

        if(currentProfile.status){
            getActivity().getTheme().applyStyle(R.style.AppThemeInverted, true);
        }
        else{
            getActivity().getTheme().applyStyle(R.style.AppTheme, true);
        }

        counselingLatLng = new LatLng(1,1);
        policeLatLng = new LatLng(1,1);
        hospitalLatLng = new LatLng(1,1);
        //set up layout
        tvPolice = (TextView) v.findViewById(R.id.tvPolice1);
        tvPoliceDistance = (TextView) v.findViewById(R.id.tvPoliceDistance1);

        tvCounseling = (TextView) v.findViewById(R.id.tvCounseling1);
        tvCounselingDistance = (TextView) v.findViewById(R.id.tvCounselingDistance1);

        tvHospital = (TextView) v.findViewById(R.id.tvHospital1);
        tvHospitalDistance = (TextView) v.findViewById(R.id.tvHospitalDistance1);

        cvHospital1 = (CardView) v.findViewById(R.id.cvHospital1);
        cvPolice1 = (CardView) v.findViewById(R.id.cvPolice1);
        cvCounseling1 = (CardView) v.findViewById(R.id.cvCounseling1);

        cvHospital1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapZoomIn(hospitalLatLng);
            }
        });
        cvCounseling1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapZoomIn(counselingLatLng);
            }
        });
        cvPolice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapZoomIn(policeLatLng);
            }
        });

        //set up database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getSupportMap();
        return v;
    }


    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, 100, 100);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public String metersToMiles(Float distance){
        double miles = distance * 0.000621371 ;
        if (miles < 0.5){
            double feet = miles * 5280;
            return String.format("%.2f", feet) + " feet away";
        }
        return String.format("%.2f", miles) + " miles away";
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getActivity().getMenuInflater().inflate(R.menu.menu_cancel, menu);
//        MenuItem menuItem = (MenuItem) menu.findItem(R.id.markSafe);
//
//        if(!currentProfile.status) {
//            menuItem.setVisible(false);
//        }
//
//        return super.onCreateOptionsMenu(menu);
//    }

    public void message911(final MenuItem menuItem){
//        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
//        Message911MenuItemFragment message911MenuItemFragment = Message911MenuItemFragment.newInstance(this);
//        message911MenuItemFragment.show(fm, "tag");
    }

    public void call911(final MenuItem menuItem) {
//        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
//        Call911MenuItemFragment call911MenuItemFragment = Call911MenuItemFragment.newInstance(this);
//        call911MenuItemFragment.show(fm, "TAG");
    }

    public void markSafe(final MenuItem menuItem){
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_Light_Dialog).create();
        alertDialog.setTitle("Mark Yourself Safe");
        alertDialog.setMessage("Are you sure you would like to mark yourself as safe?");
        alertDialog.setIcon(R.drawable.ic_need_help);

        DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentProfile.userId).child("need help").setValue(false);
                FirebaseDatabase.getInstance().getReference().child("User Status").child(currentProfile.userId).setValue("safe");
                currentProfile.status = false;

                getActivity().recreate();

                alertDialog.dismiss();
            }
        };

        DialogInterface.OnClickListener no = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        };

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", no);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", yes);
        alertDialog.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
    }

    public void getSupportMap (){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(resourcesMap1);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getActivity().getBaseContext().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myLocation = new Location(LocationManager.GPS_PROVIDER);
        myLocation.setLongitude(currentProfile.longitude);
        myLocation.setLatitude(currentProfile.latitude);
        temp = new Location(LocationManager.GPS_PROVIDER);

        mDatabase.child("resources").child(currentProfile.school).child("counseling").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location = (Map) dataSnapshot.getValue();
                latitude = (Double) location.get("latitude");
                longitude = (Double) location.get("longitude");
                counselingLatLng = new LatLng(latitude, longitude);
                Marker counseling = mMap.addMarker(new MarkerOptions()
                        .position(counselingLatLng)
                        .title(currentProfile.school + " Counseling Services")
                        .icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.ic_heart, null))));
                tvCounseling.setText(currentProfile.school + " Counseling Services");

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(counselingLatLng, 15));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(counselingLatLng, 15));

                temp.setLatitude(latitude);
                temp.setLongitude(longitude);
                Float distance = myLocation.distanceTo(temp);
                String distanceDisplayed = metersToMiles(distance);
                tvCounselingDistance.setText(distanceDisplayed);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("resources").child(currentProfile.school).child("hospital").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location = (Map) dataSnapshot.getValue();
                latitude = (Double) location.get("latitude");
                longitude = (Double) location.get("longitude");
                hospitalLatLng = new LatLng(latitude, longitude);
                Marker health = mMap.addMarker(new MarkerOptions()
                        .position(hospitalLatLng)
                        .title(currentProfile.school + " Health Services")
                        .icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.ic_local_hospital_secondary_24px, null))));
                tvHospital.setText(currentProfile.school + " Health Services");

                temp.setLatitude(latitude);
                temp.setLongitude(longitude);
                Float distance = myLocation.distanceTo(temp);
                String distanceDisplayed = metersToMiles(distance);
                tvHospitalDistance.setText(distanceDisplayed);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabase.child("resources").child(currentProfile.school).child("police").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                location = (Map) dataSnapshot.getValue();
                latitude = (Double) location.get("latitude");
                longitude = (Double) location.get("longitude");
                policeLatLng = new LatLng(latitude, longitude);
                Marker police = mMap.addMarker(new MarkerOptions()
                        .position(policeLatLng)
                        .title(currentProfile.school + " Police")
                        .icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.ic_taxi, null))));
                tvPolice.setText(currentProfile.school + " Police");

                temp.setLatitude(latitude);
                temp.setLongitude(longitude);
                Float distance = myLocation.distanceTo(temp);
                String distanceDisplayed = metersToMiles(distance);
                tvPoliceDistance.setText(distanceDisplayed);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void mapZoomIn(LatLng location){
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(15)
                .bearing(0)
                .tilt(40)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
