package compass.compass.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import compass.compass.R;

import static android.content.Context.LOCATION_SERVICE;
import static compass.compass.MainActivity.currentProfile;
import static compass.compass.R.id.resourcesMap;

/**
 * Created by brucegatete on 8/3/17.
 */

public class ResourceLocationFragment extends Fragment implements OnMapReadyCallback {
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
    Bitmap hospitalIcon;
    Bitmap policeIcon;
    Bitmap counsel;
    Double latitude;
    Double longitude;
    Map location;
    LocationManager locationManager;
    LatLng myLocation;
    boolean mapExpanded;
    int originalHeight;
    SupportMapFragment mapFragment;
    CardView cvPolice, cvCounseling, cvHospital;
    public LatLng policeLocation = new LatLng(47.620775, -122.336276);
    public LatLng hospitalLocation = new LatLng(47.625313, -122.334535);
    public LatLng counselingLocation = new LatLng(47.626496, -122.344402);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_resources, container, false);

        if(currentProfile.status){
            getActivity().getTheme().applyStyle(R.style.AppThemeInverted, true);
        }
        else{
            getActivity().getTheme().applyStyle(R.style.AppTheme, true);
        }


        //set up layout
        tvPolice = (TextView) v.findViewById(R.id.tvPolice);
        tvPoliceDistance = (TextView) v.findViewById(R.id.tvPoliceDistance);
        tvPolice.setText("West Presinct, SEA PD");
        tvPoliceDistance.setText("0.9 mi · (206) 625-5011 ");
        tvCounseling = (TextView) v.findViewById(R.id.tvCounseling);
        tvCounseling.setText("Seattle Christian Counseling");
        tvCounselingDistance = (TextView) v.findViewById(R.id.tvCounselingDistance);
        tvCounselingDistance.setText("0.2 mi · (206) 388-3929");
        tvHospital = (TextView) v.findViewById(R.id.tvHospital);
        tvHospital.setText("Kaiser Permanente");
        tvHospitalDistance = (TextView) v.findViewById(R.id.tvHospitalDistance);
        tvHospitalDistance.setText("0.5 mi · (206) 448-4141");
        counsel = getBitmap(getActivity(), R.drawable.ic_heart);
        counsel = resizeIcon(counsel, 80, 80);
        hospitalIcon = getBitmap(getActivity(), R.drawable.ic_local_hospital_secondary_24px);
        hospitalIcon = resizeIcon(hospitalIcon, 80, 80);
        policeIcon = getBitmap(getActivity(), R.drawable.ic_taxi);
        policeIcon = resizeIcon(policeIcon, 80, 80);


        cvPolice = (CardView) v.findViewById(R.id.cvPolice);
        cvPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapZoomIn(policeLocation);
            }
        });
        cvCounseling = (CardView) v.findViewById(R.id.cvCounseling);
        cvCounseling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapZoomIn(counselingLocation);
            }
        });
        cvHospital = (CardView) v.findViewById(R.id.cvHospital);
        cvHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapZoomIn(hospitalLocation);
            }
        });



        //set up database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getSupportMap();
        mapExpanded = false;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.resourcesMap);
        mapFragment.getMapAsync(this);

        final View view = mapFragment.getView();

        view.post(new Runnable() {
            @Override
            public void run() {
                originalHeight = view.getHeight();
            }
        });

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
////        Message911MenuItemFragment message911MenuItemFragment = Message911MenuItemFragment.newInstance(this);
//        message911MenuItemFragment.show(fm, "tag");
    }

    public void call911(final MenuItem menuItem) {
//        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
////        Call911MenuItemFragment call911MenuItemFragment = Call911MenuItemFragment.newInstance(this);
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
        mMap.setMinZoomPreference(13);
        mMap.setMaxZoomPreference(15);
        populateMap();
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(mapExpanded){
                    resizeMap(mapFragment, RelativeLayout.LayoutParams.MATCH_PARENT, originalHeight);
                    mapExpanded = false;
                }
                else{
                    resizeMap(mapFragment, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    mapExpanded = true;
                }

            }
        });

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
        mMap.setMyLocationEnabled(true);
        myLocation = new LatLng(currentProfile.latitude, currentProfile.longitude);
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(47.628911, -122.342969);
//        Marker something = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        if(myLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));


            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 20));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLocation)      // Sets the center of the map to location user
                    .zoom(14)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 40 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void getSupportMap (){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(resourcesMap);
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

    }
    private void resizeMap(SupportMapFragment f, int newWidth, int newHeight) {
        if (f != null) {
            View view = f.getView();
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(newWidth, newHeight);
            view.setLayoutParams(p);
            view.requestLayout();

        }
    }
    public Bitmap resizeIcon(Bitmap imageBitmap, int width, int height){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public void populateMap (){
        MarkerOptions markerOptions_1 = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(hospitalIcon))
                .position(hospitalLocation)
                .title("Kaiser Permanente Hospital");
        Marker flag_1 = mMap.addMarker(markerOptions_1);
        startDropMarkerAnimation(flag_1);

        MarkerOptions markerOptions_2 = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(policeIcon))
                .position(policeLocation)
                .title("West Prescinct, SEA Police Dept");
        Marker flag_2 = mMap.addMarker(markerOptions_2);
        startDropMarkerAnimation(flag_2);

        MarkerOptions markerOptions_3 = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(counsel))
                .position(counselingLocation)
                .title("Seattle Christian Counseling");
        Marker flag_3 = mMap.addMarker(markerOptions_3);
        startDropMarkerAnimation(flag_3);
    }

    private static Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");

        }
    }

    private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
    private void startDropMarkerAnimation(final Marker marker) {
        final Handler handler = new Handler();
        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;
        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed/(duration)), 0);
                marker.setAnchor(0.2f, 1.0f +  t);

                if (t > 0.0) {
                    handler.postDelayed(this, 16);
                }
                else{
                    return;
                }
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
