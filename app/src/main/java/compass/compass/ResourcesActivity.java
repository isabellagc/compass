package compass.compass;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import compass.compass.fragments.Call911MenuItemFragment;
import compass.compass.fragments.Message911MenuItemFragment;
import compass.compass.fragments.ResourcePagerAdapter;

import static compass.compass.MainActivity.currentProfile;

public class ResourcesActivity extends AppCompatActivity implements OnMapReadyCallback, Call911MenuItemFragment.Call911FragmentListener, Message911MenuItemFragment.Message911FragmentListener {

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
    ViewPager vpPager;
    ResourcePagerAdapter resourcePagerAdapter;
    Location temp;
    String policePhone, counselPhone, hospitalPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(currentProfile.status){
            getTheme().applyStyle(R.style.AppThemeInverted, true);
        }
        else{
            getTheme().applyStyle(R.style.AppTheme, true);
        }

        setContentView(R.layout.activity_chat);

        //set up layout
        tvPolice = (TextView) findViewById(R.id.tvPolice);
        tvPoliceDistance = (TextView) findViewById(R.id.tvPoliceDistance);

        tvCounseling = (TextView) findViewById(R.id.tvCounseling);
        tvCounselingDistance = (TextView) findViewById(R.id.tvCounselingDistance);
        tvHospital = (TextView) findViewById(R.id.tvHospital);
        tvHospitalDistance = (TextView) findViewById(R.id.tvHospitalDistance);
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        resourcePagerAdapter = new ResourcePagerAdapter(getSupportFragmentManager(), this);
        vpPager.setAdapter(resourcePagerAdapter);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                final InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(vpPager.getWindowToken(), 0);
            }

            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //add the sliding_tab
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        //set up database
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(resourcesMap);
//        mapFragment.getMapAsync();

//        locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        temp = new Location(LocationManager.GPS_PROVIDER);
//
//        mDatabase.child("resources").child(currentProfile.school).child("counseling").addListenerForSingleValueEvent(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                location = (Map) dataSnapshot.getValue();
//                latitude = (Double) location.get("latitude");
//                longitude = (Double) location.get("longitude");
//                LatLng counselingLatLng = new LatLng(latitude, longitude);
//                Marker counseling = mMap.addMarker(new MarkerOptions()
//                        .position(counselingLatLng)
//                        .title(currentProfile.school + " Counseling Services")
//                        .icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.ic_location_pin_green, null))));
//                tvCounseling.setText(currentProfile.school + " Counseling Services");
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(counselingLatLng, 15));
//                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(counselingLatLng, 15));
//
//                temp.setLatitude(latitude);
//                temp.setLongitude(longitude);
//                Float distance = myLocation.distanceTo(temp);
//                String distanceDisplayed = metersToMiles(distance);
//                tvCounselingDistance.setText(distanceDisplayed);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        mDatabase.child("resources").child(currentProfile.school).child("hospital").addListenerForSingleValueEvent(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                location = (Map) dataSnapshot.getValue();
//                latitude = (Double) location.get("latitude");
//                longitude = (Double) location.get("longitude");
//                LatLng hospitalLatLng = new LatLng(latitude, longitude);
//                Marker health = mMap.addMarker(new MarkerOptions()
//                        .position(hospitalLatLng)
//                        .title(currentProfile.school + " Health Services")
//                        .icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.ic_location_pin_red, null))));
//                tvHospital.setText(currentProfile.school + " Health Services");
//
//                temp.setLatitude(latitude);
//                temp.setLongitude(longitude);
//                Float distance = myLocation.distanceTo(temp);
//                String distanceDisplayed = metersToMiles(distance);
//                tvHospitalDistance.setText(distanceDisplayed);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        mDatabase.child("resources").child(currentProfile.school).child("police").addListenerForSingleValueEvent(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                location = (Map) dataSnapshot.getValue();
//                latitude = (Double) location.get("latitude");
//                longitude = (Double) location.get("longitude");
//                LatLng policeLatLng = new LatLng(latitude, longitude);
//                Marker police = mMap.addMarker(new MarkerOptions()
//                        .position(policeLatLng)
//                        .title(currentProfile.school + " Police")
//                        .icon(getMarkerIconFromDrawable(getResources().getDrawable(R.drawable.ic_location_pin_blue, null))));
//                tvPolice.setText(currentProfile.school + " Police");
//
//                temp.setLatitude(latitude);
//                temp.setLongitude(longitude);
//                Float distance = myLocation.distanceTo(temp);
//                String distanceDisplayed = metersToMiles(distance);
//                tvPoliceDistance.setText(distanceDisplayed);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancel, menu);
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.markSafe);

        if(!currentProfile.status) {
            menuItem.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void message911(final MenuItem menuItem){
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Message911MenuItemFragment message911MenuItemFragment = Message911MenuItemFragment.newInstance(this);
        message911MenuItemFragment.show(fm, "tag");
    }

    public void call911(final MenuItem menuItem) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        Call911MenuItemFragment call911MenuItemFragment = Call911MenuItemFragment.newInstance(this);
        call911MenuItemFragment.show(fm, "TAG");
    }

    public void markSafe(final MenuItem menuItem){
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog).create();
        alertDialog.setTitle("Mark Yourself Safe");
        alertDialog.setMessage("Are you sure you would like to mark yourself as safe?");
        alertDialog.setIcon(R.drawable.ic_need_help);

        DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentProfile.userId).child("need help").setValue(false);
                FirebaseDatabase.getInstance().getReference().child("User Status").child(currentProfile.userId).setValue("safe");
                currentProfile.status = false;

                recreate();

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
    public void launchNeedHelpFragment() {
        mDatabase.child("User Status").child(currentProfile.userId).setValue("help");
        mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
        currentProfile.status = true;
        Intent i = new Intent(this, NeedHelpActivity.class);
        startActivity(i);
    }

    @Override
    public void launchNeedHelpFromMessage() {
        mDatabase.child("User Status").child(currentProfile.userId).setValue("help");
        mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
        currentProfile.status = true;
        Intent i = new Intent(this, NeedHelpActivity.class);
        startActivity(i);
    }
}
