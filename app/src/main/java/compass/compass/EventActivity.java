package compass.compass;

import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compass.compass.fragments.NewEventFragment;
import compass.compass.models.Event;
import compass.compass.models.User;

import static compass.compass.MainActivity.allContacts;
import static compass.compass.MainActivity.currentProfile;
import static compass.compass.R.id.heatMap;

public class EventActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static RecyclerView rvEvents;
    ArrayList<Event> eventList;
    EventsAdapter eventAdapter;
    FloatingActionButton fab;

    private GoogleMap mMap;
    DatabaseReference mDatabase;
    Map<String, WeightedLatLng> contactLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if(currentProfile.status){
//            getTheme().applyStyle(R.style.AppThemeInverted, true);
//        }
//        else{
//            getTheme().applyStyle(R.style.AppTheme, true);
//        }

        setContentView(R.layout.activity_events_page_back_up);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        rvEvents = (RecyclerView) findViewById(R.id.rvEvents1);
        eventList = new ArrayList<>();
        eventAdapter = new EventsAdapter(this);
        rvEvents.setAdapter(eventAdapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.invalidate();
        initToolbar();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNewEvent();
                fab.hide();
            }
        });

        contactLocations = new HashMap<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(heatMap);
        mapFragment.getMapAsync(this);

        for (User user : allContacts.values()) {
            contactLocations.put(user.name, new WeightedLatLng(new LatLng(user.latitude, user.longitude), 100));
        }

//        for(final String name: allContacts.keySet()){
//            contactLocations.put(name, new LatLng(0,0));
//
//            mDatabase.child("Users").child(name).child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    double tempLong = contactLocations.get(name).longitude;
//                    contactLocations.remove(name);
//                    contactLocations.put(name, new LatLng((double) dataSnapshot.getValue(), tempLong));
//
//                    HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
//                            .data(contactLocations.values())
//                            .build();
//                    // Add a tile overlay to the map, using the heat map tile provider.
//                    TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//            mDatabase.child("Users").child(name).child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    double tempLat = contactLocations.get(name).latitude;
//                    contactLocations.remove(name);
//                    contactLocations.put(name, new LatLng(tempLat, (double) dataSnapshot.getValue()));
//
//                    HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
//                            .data(contactLocations.values())
//                            .build();
//                    // Add a tile overlay to the map, using the heat map tile provider.
//                    TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //final ActionBar actionBar = getSupportActionBar();
    }

    //launch the new activity

    public void launchNewEvent() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameNewEvent, NewEventFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_cancel, menu);
//        MenuItem menuItem = (MenuItem) menu.findItem(R.id.markSafe);
//
//        if(!currentProfile.status) {
//            menuItem.setVisible(false);
//        }
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    public void markSafe(final MenuItem menuItem){
//        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog).create();
//        alertDialog.setTitle("Mark Yourself Safe");
//        alertDialog.setMessage("Are you sure you would like to mark yourself as safe?");
//        alertDialog.setIcon(R.drawable.ic_need_help);
//
//        DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                FirebaseDatabase.getInstance().getReference().child("Users").child(currentProfile.userId).child("need help").setValue(false);
//                FirebaseDatabase.getInstance().getReference().child("User Status").child(currentProfile.userId).setValue("safe");
//                currentProfile.status = false;
//
//                recreate();
//
//                alertDialog.dismiss();
//            }
//        };
//
//        DialogInterface.OnClickListener no = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                alertDialog.dismiss();
//            }
//        };
//
//        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", no);
//        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", yes);
//
//        alertDialog.show();
//    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentProfile.latitude, currentProfile.longitude), 15));


        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .weightedData(contactLocations.values())
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
}
