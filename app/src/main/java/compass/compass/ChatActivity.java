package compass.compass;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import compass.compass.models.ChatMessage;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by brucegatete on 7/11/17.
 */

public class ChatActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    EditText etMessage;
    Button btSend;
    RecyclerView rvChat;
    ChatAdapter mAdapter;
    Map<String, Marker> markerMap;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    static final int POLL_INTERVAL = 1000; // milliseconds
    Long eventId;
    public DatabaseReference mDatabase;
    private LinearLayoutManager layoutManager;
    Double latitude;
    Double longitude;
    NotificationManager mNotificationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        markerMap = new HashMap<String, Marker>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        eventId = getIntent().getExtras().getLong("eventId");

        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);

        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mFirstLoad = true;

        mAdapter = new ChatAdapter(ChatActivity.this, eventId);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        layoutManager = new LinearLayoutManager(ChatActivity.this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);

        mAdapter.registerAdapterDataObserver( new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                layoutManager.smoothScrollToPosition(rvChat, null, mAdapter.getItemCount());
            }
        });

        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
//                ParseObject message = ParseObject.create("Message");
//                message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
//                message.put(BODY_KEY, data);

                Toast.makeText(ChatActivity.this, data, Toast.LENGTH_SHORT).show();

                ChatMessage message = new ChatMessage();
                message.setText(data);
                message.setSender(currentProfile.name);
                message.setTime((new Date().getTime()));
                etMessage.getText().clear();
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ChatActivity.this)
                        .setSmallIcon(R.drawable.ic_need_help)
                        .setContentTitle("New Message from " + message.getSender())
                        .setContentText(message.getText())
                        .setOnlyAlertOnce(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                mBuilder.setAutoCancel(true);
                mBuilder.setLocalOnly(false);
//
//
                mNotificationManager.notify(Integer.parseInt(currentProfile.userId), mBuilder.build());

                mDatabase.child("messages").child(eventId.toString()).push().setValue(message);
                mAdapter.notifyDataSetChanged();
                rvChat.post( new Runnable() {
                    @Override
                    public void run() {
                        rvChat.smoothScrollToPosition(mAdapter.getItemCount());
                    }
                });
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fMap);
        mapFragment.getMapAsync(this);

        mDatabase.child("Events").child(eventId.toString()).child("Members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String memberName = dataSnapshot.getKey();

                if(!memberName.equals(currentProfile.userId)){
                    if(!markerMap.containsKey(memberName)){
                        LatLng temp = new LatLng(47.628911, -122.342969);
                        Marker temp2 = mMap.addMarker(new MarkerOptions().position(temp).title(memberName));
                        markerMap.put(memberName, temp2);
                    }

                    if(memberName != null){
                        mDatabase.child("Users").child(memberName).child("latitude").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                latitude = (Double) dataSnapshot.getValue();
                                longitude = markerMap.get(memberName).getPosition().longitude;
                                markerMap.get(memberName).setPosition(new LatLng(latitude, longitude));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        mDatabase.child("Users").child(memberName).child("longitude").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                longitude = (Double) dataSnapshot.getValue();
                                latitude = markerMap.get(memberName).getPosition().latitude;
                                markerMap.get(memberName).setPosition(new LatLng(latitude, longitude));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.locations_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //launch the profile activity
    public void launchNewEvent (MenuItem miProfile) {
        Intent i = new Intent(this, NewEventActivity.class);
        startActivity(i);
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
        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(47.628911, -122.342969);
//        Marker something = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 40 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

}
