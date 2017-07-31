package compass.compass;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.rides.RequestDeeplink;
import com.uber.sdk.android.rides.RideParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import compass.compass.models.ChatMessage;
import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.currentProfile;
import static compass.compass.R.id.friendMap;

/**
 * Created by brucegatete on 7/11/17.
 */

public class NeedHelpActivity extends AppCompatActivity implements OnMapReadyCallback {

    SeekBar level;
    ImageButton alc;
    ImageButton home;
    ImageButton sa;
    ImageButton other;
    SwipeButton callPolice;
    public DatabaseReference mDatabase;
    ChatAdapter mAdapter;
    String [] event_n0;
    String[] members;
    String[] eventIds;
    Button btCallContact;
    TextView tvNameContact;
    CircleImageView ivProfileImage;

    LocationManager locationManager;
    Location myLocation;
    Location tempLocation = new Location(LocationManager.GPS_PROVIDER);
    Location closest;
    Float distanceToClosest;
    Double closestLatitude;
    Double closestLongitude;
    Marker closestFriend;
    Map location;
    Double latitude;
    Double longitude;
    public String closestFriendName;
    private GoogleMap mMap;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(currentProfile.status){
            getTheme().applyStyle(R.style.AppThemeInverted, true);
        }
        else{
            getTheme().applyStyle(R.style.AppTheme, true);
        }

        setContentView(R.layout.activity_need_help);

        level = (SeekBar) findViewById(R.id.level);
        alc = (ImageButton) findViewById(R.id.ibAlc);
        home = (ImageButton) findViewById(R.id.ibHome);
        sa = (ImageButton) findViewById(R.id.ibSA);
        other = (ImageButton) findViewById(R.id.ibOther);
        callPolice = (SwipeButton) findViewById(R.id.callPolice);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        btCallContact = (Button) findViewById(R.id.btCallContact);
        tvNameContact = (TextView) findViewById(R.id.tvNameContact);
        ivProfileImage = (CircleImageView) findViewById(R.id.ivProfileImageMain);

        btCallContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                User closestUser = MainActivity.allContacts.get(closestFriendName);
                callIntent.setData(Uri.parse("tel:" + closestUser.phoneNumber));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("YIKES", "lol u don't have permission to call");
                    return;
                }
                getApplicationContext().startActivity(callIntent);
            }
        });

        locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
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

        locationManager = (LocationManager) getBaseContext().getSystemService(LOCATION_SERVICE);
        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        myLocation = new Location(LocationManager.GPS_PROVIDER);
        myLocation.setLatitude(currentProfile.latitude);
        myLocation.setLongitude(currentProfile.longitude);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(friendMap);
        mapFragment.getMapAsync(NeedHelpActivity.this);

        SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                onSwiped();
            }
        };

        if (callPolice != null) {
            callPolice.setSwipeButtonCustomItems(swipeButtonSettings);
        }
        //get the events that will receive the message
        mDatabase.child("Users").child(currentProfile.name).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map receivers = (Map) dataSnapshot.getValue();
                Set<String> valid_events = receivers.keySet();
                event_n0 = (String[]) valid_events.toArray(new String[valid_events.size()]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final Map<String, String> people= new HashMap<String, String>();

        mDatabase.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map Events = (Map) dataSnapshot.getValue();
                Set<String> events = new HashSet<String>();
                for (Object e : Events.keySet()){
                    final String tempEvent = e.toString();
                    mDatabase.child("Events").child(tempEvent).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map otherPeople = (Map) dataSnapshot.getValue();
                            for(Object s : otherPeople.keySet()) {
                                String temp = s.toString().replaceAll(" ", "");
                                if((otherPeople.get(s).toString().contentEquals("out")) || (otherPeople.get(s).toString().contentEquals("on call"))){
                                    people.put(temp, tempEvent);
                                }
                            }
                            people.remove(currentProfile.name.replaceAll(" ", ""));
                            members = (String[]) people.keySet().toArray(new String[people.size()]);
                            eventIds = (String[]) people.values().toArray(new String[people.size()]);

                            for(String s: members){
                                if(s.equals("testperson")){
                                    s = "test person";
                                }
                                final String finalS = s;
                                mDatabase.child("Users").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        location = (Map) dataSnapshot.getValue();
                                        latitude = (Double) location.get("latitude");
                                        longitude = (Double) location.get("longitude");
                                        LatLng latLng = new LatLng(latitude, longitude);
                                        int drawableResourceId = getResources().getIdentifier(finalS.replaceAll(" ",""), "drawable", getPackageName());


                                        tempLocation.setLatitude(latitude);
                                        tempLocation.setLongitude(longitude);
                                        Float distance = myLocation.distanceTo(tempLocation);

                                        if(distanceToClosest == null || distance < distanceToClosest){
                                            distanceToClosest = distance;
                                            closestLatitude = latitude;
                                            closestLongitude = longitude;
                                            if(closestFriend != null){
                                                closestFriend.remove();
                                            }
                                            closestFriendName = finalS;
                                            tvNameContact.setText(closestFriendName);
                                            //mCloseFriendsNames.get(position).replaceAll(" ",""), "drawable", getPackageName()

                                            int drawableResource_Id = getResources().getIdentifier(closestFriendName, "drawable", getPackageName());
                                            ivProfileImage.setImageResource(drawableResource_Id);
                                            closestFriend = mMap.addMarker(new MarkerOptions()
                                                    .position(latLng)
                                                    .title(finalS)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(drawableResourceId))));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            //Send the message to the event
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Alert_message = ("Please help, " + currentProfile.name + " needs your help getting home");
                mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
                currentProfile.status = true;
                ChatMessage message = new ChatMessage();
                message.setText(Alert_message);
                message.setSender("BOT");
                message.setTime((new Date().getTime()));
                //change the database and notify the adapter
                for (int i = 0; i < event_n0.length; i ++) {
                    mDatabase.child("messages").child(event_n0[i]).push().setValue(message);
                    mAdapter = new ChatAdapter(ChatActivity.class, event_n0[i]);
                    mAdapter.notifyDataSetChanged();
                }
                sendNotificationToUser(members, eventIds, message);
                Toast.makeText(NeedHelpActivity.this, Alert_message, Toast.LENGTH_SHORT).show();

                recreate();

                callUber();
            }
        });

        alc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Alert_message = ("Please help, " + currentProfile.name + " needs your help. getting wasted");
                mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
                currentProfile.status = true;
                ChatMessage message = new ChatMessage();
                message.setText(Alert_message);
                message.setSender("BOT");
                message.setTime((new Date().getTime()));
                //change the database and notify the adapter
                for (int i = 0; i < event_n0.length; i ++) {
                    mDatabase.child("messages").child(event_n0[i]).push().setValue(message);
                    mAdapter = new ChatAdapter(ChatActivity.class, event_n0[i]);
                    mAdapter.notifyDataSetChanged();
                }
                sendNotificationToUser(members, eventIds, message);
                Toast.makeText(NeedHelpActivity.this, Alert_message, Toast.LENGTH_SHORT).show();

                recreate();
            }
        });

        sa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Alert_message = ("Please help, " + currentProfile.name + " needs your help getting safe from Sexual Assault!");
                mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
                currentProfile.status = true;
                ChatMessage message = new ChatMessage();
                message.setText(Alert_message);
                message.setSender("BOT");
                message.setTime((new Date().getTime()));
                //change the database and notify the adapter
                for (int i = 0; i < event_n0.length; i ++) {
                    mDatabase.child("messages").child(event_n0[i]).push().setValue(message);
                    mAdapter = new ChatAdapter(ChatActivity.class, event_n0[i]);
                    mAdapter.notifyDataSetChanged();
                }
                sendNotificationToUser(members, eventIds, message);
                Toast.makeText(NeedHelpActivity.this, Alert_message, Toast.LENGTH_SHORT).show();

                recreate();
            }

        });

    }

    private void onSwiped() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:7325168820"));

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("YIKES", "lol u dont have permission to call");
            return;
        }

        startActivity(callIntent);
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

    public void markSafe(final MenuItem menuItem){
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog).create();
        alertDialog.setTitle("Mark Yourself Safe");
        alertDialog.setMessage("Are you sure you would like to mark yourself as safe?");
        alertDialog.setIcon(R.drawable.ic_need_help);

        DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentProfile.userId).child("need help").setValue(false);
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

    public void sendNotificationToUser(String[] user, String[] events, final ChatMessage message) {

        ArrayList<String> recipients= new ArrayList<String>(Arrays.asList(user));
        ArrayList<String> eventIds= new ArrayList<>(Arrays.asList(events));

        Map notification = new HashMap<>();
        for(int i = 0; i < recipients.size(); i++)
        {
            ArrayList<String> tempRecipient = new ArrayList<String>();
            tempRecipient.add(recipients.get(i));

            notification.put("recipients", tempRecipient);
            notification.put("message", message.getSender().replaceAll(" ", "") + ":" + eventIds.get(i) + ":" + message.getText());
            mDatabase.child("notifications").push().setValue(notification);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

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

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (CircleImageView) customMarkerView.findViewById(R.id.profile_image);


//        Glide.with(getApplicationContext())
//                .load(linkToPic)
//                .placeholder(R.color.c50)
//                .dontAnimate()
//                .into(markerImageView);
//
//        Glide.with(this)
//                .load(R.color.Black)
//                .into(markerImageView);

        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    public void callUber(){

        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog).create();
        alertDialog.setTitle("Call an Uber");
        alertDialog.setMessage("Would you like to request an Uber to go home?");
        alertDialog.setIcon(R.drawable.ic_need_help);

        DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RideParameters rideParams = new RideParameters.Builder()
                        // Optional product_id from /v1/products endpoint (e.g. UberX). If not provided, most cost-efficient product will be used
                        .setProductId("a1111c8c-c720-46c3-8534-2fcdd730040d")
                        // Required for price estimates; lat (Double), lng (Double), nickname (String), formatted address (String) of dropoff location
                        //.setPickupLocation(currentProfile.latitude, currentProfile.longitude, "My Location", null)
                        .setPickupToMyLocation()
                        .setDropoffLocation(currentProfile.homeLat, currentProfile.homeLong, "Home", null)
                        .build();

                RequestDeeplink deeplink = new RequestDeeplink.Builder(NeedHelpActivity.this)
                        .setSessionConfiguration(UberSdk.getDefaultSessionConfiguration())
                        .setRideParameters(rideParams).build();
                deeplink.execute();

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

}
