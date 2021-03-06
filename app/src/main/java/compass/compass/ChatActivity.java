package compass.compass;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import compass.compass.fragments.Call911MenuItemFragment;
import compass.compass.fragments.ChatPagerAdapter;
import compass.compass.fragments.Message911MenuItemFragment;
import compass.compass.fragments.NewEventFragment;
import compass.compass.models.ChatMessage;
import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.allContacts;
import static compass.compass.MainActivity.currentProfile;
import static compass.compass.MainActivity.peopleInEvents;

/**
 * Created by brucegatete on 7/11/17.
 */

public class ChatActivity extends AppCompatActivity implements OnMapReadyCallback, LaunchFlagLocationActivity.MyDialogListener, Call911MenuItemFragment.Call911FragmentListener, Message911MenuItemFragment.Message911FragmentListener {

    StorageReference storage;
    private GoogleMap mMap;
    EditText etMessage;
    Button btSend;
    RecyclerView rvChat;
    ChatAdapter mAdapter;

    Map<String, Marker> markerMap;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    static final int POLL_INTERVAL = 1000; // milliseconds
    public static String eventId;

    public DatabaseReference mDatabase;
    private LinearLayoutManager layoutManager;
    Double latitude;
    Double longitude;
    NotificationManager mNotificationManager;
    Uri linkToPic;
    public int NOTIFICATION_ID = 12;

    Double myLatitude;
    Double myLongitude;
    LatLng myLocation;
    String fromHere;
    boolean alarmSet = false;

    String myStatus;

    String[] members;

    boolean mapExpanded;
    boolean chatExpanded;
    SupportMapFragment mapFragment;
    int originalHeight;
    ViewPager vpPager;
    ChatPagerAdapter chatPagerAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(currentProfile.status){
            getTheme().applyStyle(R.style.AppThemeInverted, true);
        }
        else{
            getTheme().applyStyle(R.style.AppTheme, true);
        }

        setContentView(R.layout.activity_chat);

        Bundle args = getIntent().getExtras();
        storage = FirebaseStorage.getInstance().getReference();
        eventId = getIntent().getStringExtra("eventId");
        fromHere = getIntent().getStringExtra("fromHere");
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        chatPagerAdapter = new ChatPagerAdapter(getSupportFragmentManager(), this);
        vpPager.setAdapter(chatPagerAdapter);
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



        markerMap = new HashMap<String, Marker>();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);

        rvChat = (RecyclerView) findViewById(R.id.rvContacts);
        mFirstLoad = true;

        mAdapter = new ChatAdapter(ChatActivity.this, eventId);
        rvChat.setAdapter(mAdapter);

        chatExpanded = false;

        rvChat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (chatExpanded){
                    resizeChat(rvChat, RelativeLayout.LayoutParams.MATCH_PARENT, 270);
                    chatExpanded = false;
                }
                else{
                    resizeChat(rvChat, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    chatExpanded = true;
                }
                return true;
            }
        });

        // associate the LayoutManager with the RecylcerView
        layoutManager = new LinearLayoutManager(ChatActivity.this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);


        //mDatabase.child("Events").child().child("Members").addValueEventListener()

        //get other members of group
        mDatabase.child("Events").child(eventId).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map otherPeople = (Map) dataSnapshot.getValue();
                Set<String> people= new HashSet<String>();
                for(Object s : otherPeople.keySet()) {
                    String temp = s.toString().replaceAll(" ", "");
                    if(s.toString().contentEquals(currentProfile.userId)){
                        myStatus = otherPeople.get(s).toString();
                    }
                    if((otherPeople.get(s).toString().contentEquals("out"))){
                        people.add(temp);
                    }
                }
                if (people.contains(currentProfile.name.replaceAll(" ", ""))){
                    people.remove(currentProfile.name.replaceAll(" ", ""));
                }
                members = (String[]) people.toArray(new String[people.size()]);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

                //Toast.makeText(ChatActivity.this, data, Toast.LENGTH_SHORT).show();
                if(data.matches("( *)"))
                {
                    Toast.makeText(ChatActivity.this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
                    etMessage.getText().clear();
                }
                else{
                    ChatMessage message = new ChatMessage();
                    message.setText(data);
                    message.setSender(currentProfile.name);
                    message.setTime((new Date().getTime()));
                    etMessage.getText().clear();
                    mDatabase.child("messages").child(eventId).push().setValue(message);
                    mAdapter.notifyDataSetChanged();

                    rvChat.post( new Runnable() {
                        @Override
                        public void run() {
                            rvChat.smoothScrollToPosition(mAdapter.getItemCount());
                        }
                    });

                    //send notification to the user
                    sendNotificationToUser(members, message);
                }

            }
        });

        mapExpanded = false;
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.friendMap);
        mapFragment.getMapAsync(this);
        final View view = mapFragment.getView();

        view.post(new Runnable() {
            @Override
            public void run() {
                originalHeight = view.getHeight(); //height is ready
            }
        });


        mDatabase.child("Events").child(eventId).child("Members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String memberName = dataSnapshot.getKey();

                if(!memberName.equals(currentProfile.userId) && !dataSnapshot.getValue().toString().contentEquals("null")){
                    if(!markerMap.containsKey(memberName)){
                        User user = allContacts.get(memberName);
                        LatLng temp = new LatLng(47.628911, -122.342969);
                        int drawableResourceId = getResources().getIdentifier(memberName.replaceAll(" ",""), "drawable", getPackageName());
                        Marker temp2 = mMap.addMarker(new MarkerOptions()
                                .position(temp)
                                .title(memberName)
                                .snippet("Drinks: " + user.drinkCounter + " BAC: " + user.currentBAC)
                                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(drawableResourceId))));
                        markerMap.put(memberName, temp2);

                    }

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

                    mDatabase.child("Drinks").child(memberName).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            User user = allContacts.get(memberName);
                            updateUserInfo(dataSnapshot, memberName, user);
                            updateMarker(user);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            User user = allContacts.get(memberName);
                            updateUserInfo(dataSnapshot, memberName, user);
                            updateMarker(user);
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
                else if (memberName.equals(currentProfile.userId)){
                    mDatabase.child("Users").child(memberName).child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            myLatitude = (Double) dataSnapshot.getValue();
                            if(myLatitude != null && myLongitude != null){
                                myLocation = new LatLng(myLatitude, myLongitude);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mDatabase.child("Users").child(memberName).child("longitude").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            myLongitude = (Double) dataSnapshot.getValue();
                            if(myLatitude != null && myLongitude != null){
                                myLocation = new LatLng(myLatitude, myLongitude);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

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

    private void updateUserInfo(DataSnapshot dataSnapshot, String memberName, User user){
        Object o = dataSnapshot.getValue();

        if(dataSnapshot.getKey().toString().equals("BAC")){
            Object BAC = dataSnapshot.getValue();
            //find and cast BAC
            if(BAC instanceof Integer){
                Integer temp = ((Integer) BAC).intValue();
                user.currentBAC = temp.doubleValue();
            }else if (BAC instanceof Double){
                user.currentBAC = (double) BAC;
            }else if (BAC instanceof Long){
                Long x = (Long) BAC;
                user.currentBAC = x.doubleValue();
            }
        }else{
            //must be drink count then...
            Object drinkCount = dataSnapshot.getValue();
            //find and cast drink count
            if(drinkCount instanceof Integer){
                user.drinkCounter = (int) drinkCount;
            }else if (drinkCount instanceof Double){
                Double d = ((Double) drinkCount).doubleValue();
                user.drinkCounter = d.intValue();
            }else if (drinkCount instanceof Long){
                Long x = (Long) drinkCount;
                user.drinkCounter = x.intValue();
            }
        }
    }

    private void updateMarker(User user){
        markerMap.get(user.userId).setSnippet("Drinks: " + user.drinkCounter + " BAC: " + user.currentBAC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancel, menu);
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.markSafe);

        if(!currentProfile.status) {
            menuItem.setVisible(false);
        }

        setTitle(eventId);


        return super.onCreateOptionsMenu(menu);
    }

    public void message911(final MenuItem menuItem){
        FragmentManager fm = getSupportFragmentManager();
        Message911MenuItemFragment message911MenuItemFragment = Message911MenuItemFragment.newInstance(this);
        message911MenuItemFragment.show(fm, "tag");
    }

    public void call911(final MenuItem menuItem) {
        FragmentManager fm = getSupportFragmentManager();
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

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setText(WordUtils.capitalize(currentProfile.userId) + " has marked themselves as safe");
                chatMessage.setSender("SAFE");
                chatMessage.setTime((new Date().getTime()));
                NeedHelpActivity.sendNotificationToUser(peopleInEvents, chatMessage, mDatabase);

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

    //launch the profile activity
    public void launchNewEvent (MenuItem miProfile) {
        //create the user fragment
        android.support.v4.app.Fragment newEventFragment = (android.support.v4.app.Fragment) NewEventFragment.newInstance();

        //display the user timeline fragment inside the container (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        //make changes
        ft.replace(R.id.frameNewEvent, newEventFragment);

        //commit transaction
        ft.commit();
    }

    private void resizeMap(SupportMapFragment f, int newWidth, int newHeight) {
        if (f != null) {
            View view = f.getView();
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(newWidth, newHeight);
            view.setLayoutParams(p);
            view.requestLayout();

        }
    }

    private void resizeChat(RecyclerView rvChat, int newWidth, int newHeight) {
        if (rvChat != null) {
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(newWidth, newHeight);
            rvChat.setLayoutParams(p);
            rvChat.requestLayout();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
        //LatLng sydney = new LatLng(47.628911, -122.342969);
//        Marker something = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        if(myLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));


            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLocation)      // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 40 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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

    public void sendNotificationToUser(String[] user, final ChatMessage message) {

        ArrayList<String> recipients= new ArrayList<String>(Arrays.asList(user));

        Map notification = new HashMap<>();
        notification.put("recipients", recipients);
        notification.put("message", message.getSender().replaceAll(" ", "") + ":" + eventId + ":" + message.getText());
        mDatabase.child("notifications").push().setValue(notification);

    }


    @Override
    public void onContinueClicked() {
        Map<String, Object> infoToPush = new HashMap<>();
        infoToPush.put("latitude", currentProfile.latitude);
        infoToPush.put("longitude", currentProfile.longitude);
        infoToPush.put("user", currentProfile.userId);
        infoToPush.put("time flagged", new Date().getTime());
        mDatabase.child("Flagged Locations").push().setValue(infoToPush);
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
        Intent i = new Intent(this, NeedHelpActivity.class);
        i.putExtra("launchHelp", true);
        startActivity(i);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == CALL_ACTIVITY_CODE){
//            Intent i = new Intent(this, NeedHelpActivity.class);
//            startActivity(i);
//        }
//    }

}
