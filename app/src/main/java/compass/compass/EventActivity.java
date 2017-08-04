package compass.compass;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import compass.compass.fragments.Call911MenuItemFragment;
import compass.compass.fragments.Message911MenuItemFragment;
import compass.compass.fragments.NewEventFragment;
import compass.compass.models.ChatMessage;
import compass.compass.models.Event;

import static compass.compass.MainActivity.currentProfile;
import static compass.compass.MainActivity.peopleInEvents;

public class EventActivity extends AppCompatActivity implements Message911MenuItemFragment.Message911FragmentListener, Call911MenuItemFragment.Call911FragmentListener {
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
        setTitle("Circles");

        if(currentProfile.status){
            getTheme().applyStyle(R.style.AppThemeInverted, true);
        }
        else{
            getTheme().applyStyle(R.style.AppTheme, true);
        }

        setContentView(R.layout.activity_events_page_back_up);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        rvEvents = (RecyclerView) findViewById(R.id.rvEvents1);
        eventList = new ArrayList<>();
        eventAdapter = new EventsAdapter(EventActivity.this);
        rvEvents.setAdapter(eventAdapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.invalidate();

        //initToolbar();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNewEvent();
                fab.hide();
            }
        });

        contactLocations = new HashMap<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(heatMap);
//        mapFragment.getMapAsync(this);
//
//        for (User user : allContacts.values()) {
//            contactLocations.put(user.name, new WeightedLatLng(new LatLng(user.latitude, user.longitude), 100));
//        }

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

//    private void initToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        //final ActionBar actionBar = getSupportActionBar();
//    }

    //launch the new activity

    public void launchNewEvent() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameNewEvent, NewEventFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
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

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setText(currentProfile.userId + " has marked themselves as safe");
                chatMessage.setSender("SAFE");
                chatMessage.setTime((new Date().getTime()));
                NeedHelpActivity.sendNotificationToUser(peopleInEvents, chatMessage, mDatabase);

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
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    @Override
    public void launchNeedHelpFragment() {
        mDatabase.child("User Status").child(currentProfile.userId).setValue("help");
        mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
        currentProfile.status = true;
        Intent i = new Intent(this, NeedHelpActivity.class);
        i.putExtra("launchHelp", true);
        startActivity(i);
    }

    @Override
    public void launchNeedHelpFromMessage() {
        mDatabase.child("User Status").child(currentProfile.userId).setValue("help");
        mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
        currentProfile.status = true;
        //TODO:THIS IS WHERE WE COULD THEORETICALLY ASK IF THEY ACTUALLY WANT TO
        Intent i = new Intent(this, NeedHelpActivity.class);
        i.putExtra("launchHelp", true);
        startActivity(i);
    }


//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//
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
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentProfile.latitude, currentProfile.longitude), 15));
//
//
//        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
//                .weightedData(contactLocations.values())
//                .build();
//        // Add a tile overlay to the map, using the heat map tile provider.
//        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
//    }
}
