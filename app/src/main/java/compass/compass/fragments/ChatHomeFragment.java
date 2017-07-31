package compass.compass.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import compass.compass.ChatAdapter;
import compass.compass.CloseFriendAdapter;
import compass.compass.R;
import compass.compass.models.ChatMessage;
import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.allContacts;
import static compass.compass.MainActivity.currentProfile;

/**
 * Created by brucegatete on 7/26/17.
 */

public class ChatHomeFragment extends Fragment implements OnMapReadyCallback {
    StorageReference storage;
    private GoogleMap mMap;
    EditText etMessage;
    Button btSend;
    RecyclerView rvContacts;
    ChatAdapter mAdapter;
    Map<String, Marker> markerMap;
    Map<String, Marker> flagMap;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    static final int POLL_INTERVAL = 1000; // milliseconds
    String eventId;
    FloatingActionButton fabMarkLocation;

    public DatabaseReference mDatabase;
    private LinearLayoutManager layoutManager;
    Double latitude;
    Double longitude;
    NotificationManager mNotificationManager;
    Uri linkToPic;
    public int NOTIFICATION_ID = 12;
    Bitmap flagIcon;

    Double myLatitude;
    Double myLongitude;
    LatLng myLocation;
    String fromHere;
    boolean alarmSet = false;

    String myStatus;

    String[] members;

    boolean newFlag = false;
    boolean mapExpanded;
    boolean chatExpanded;
    SupportMapFragment mapFragment;
    int originalHeight;
    ViewPager vpPager;
    ChatPagerAdapter chatPagerAdapter;
    //EmergencyContactsAdapter emergencyContactsAdapter;
    CloseFriendAdapter closeFriendAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=  inflater.inflate(R.layout.activity_chat_back_up, container, false);
        eventId = getActivity().getIntent().getExtras().getString("eventId");

        flagIcon = getBitmap(getContext(), R.drawable.ic_flag_location_unsafe);
        flagIcon = resizeFlagIcon(flagIcon, 72 , 72);

        markerMap = new HashMap<String, Marker>();
        flagMap = new HashMap<>();
        rvContacts = v.findViewById(R.id.rvContacts);
        //emergencyContactsAdapter = new EmergencyContactsAdapter(getActivity());
        closeFriendAdapter = new CloseFriendAdapter(getActivity());
        rvContacts.setAdapter(closeFriendAdapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvContacts.invalidate();
        rvContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OKAYYY", "HAKAPEJE");
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etMessage = (EditText) v.findViewById(R.id.etMessage);
        btSend = (Button) v.findViewById(R.id.btSend);

        fabMarkLocation = v.findViewById(R.id.fabMarkLocation);
        fabMarkLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //make a fragment to ask
                //update the database
                Map<String, Object> infoToPush = new HashMap<>();
                infoToPush.put("latitude", currentProfile.latitude);
                infoToPush.put("longitude", currentProfile.longitude);
                infoToPush.put("user", currentProfile.userId);
                newFlag = true;
                mDatabase.child("Flagged Locations").push().setValue(infoToPush);
                //make a snackbar
                Snackbar.make(getView(), "Location marked as unsafe.", Snackbar.LENGTH_LONG).show();
            }
        });

        mapExpanded = false;
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fMap);
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId, boolean help) {

        View customMarkerView;
        if(help){
            customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker_red, null);
        }
        else{
            customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        }

        CircleImageView markerImageView = (CircleImageView) customMarkerView.findViewById(R.id.profile_image);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            populateMap();
            populateMapFlags();

            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if(mapExpanded){
                        resizeMap(mapFragment, RelativeLayout.LayoutParams.MATCH_PARENT, originalHeight);
                        rvContacts.setVisibility(View.VISIBLE);
                        mapExpanded = false;
                    }
                    else{
                        resizeMap(mapFragment, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                        rvContacts.setVisibility(View.GONE);
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

            // Add a marker in Sydney and move the camera
            //LatLng sydney = new LatLng(47.628911, -122.342969);
//        Marker something = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

            if(myLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));


                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

    private void populateMapFlags() {
        mDatabase.child("Flagged Locations").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Double lat, longi;
                String title, user;

                title = dataSnapshot.getKey();
                Map<String, Object> info = (Map<String, Object>) dataSnapshot.getValue();
                lat = (Double) info.get("latitude");
                longi = (Double) info.get("longitude");
                user = (String) info.get("user");

                LatLng pos = new LatLng(lat, longi);
//                Marker flag = mMap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory.fromBitmap(flagIcon))
//                        .position(pos)
//                        .title(user)
//                );
                MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(flagIcon))
                        .position(pos)
                        .title(user);
                Marker flag = mMap.addMarker(markerOptions);
                flagMap.put(title, flag);
                startDropMarkerAnimation(flag);
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

    private void startDropMarkerAnimation(final Marker marker) {
//        //Make the marker bounce
//        final Handler handler = new Handler();
//
//        final long startTime = SystemClock.uptimeMillis();
//        final long duration = 2000;
//
//        Projection proj = mMap.getProjection();
//        final LatLng markerLatLng = marker.getPosition();
//        Point startPoint = proj.toScreenLocation(markerLatLng);
//        startPoint.offset(0, -100);
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//
//        final Interpolator interpolator = new BounceInterpolator();
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - startTime;
//                float t = interpolator.getInterpolation((float) elapsed / duration);
//                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
//                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
//
//                if (t < 1.0) {
//                    // Post again 16ms later.
//                    handler.postDelayed(this, 16);
//                }
//            }
//        });



//        final LatLng target = marker.getPosition();
//        final Handler handler = new Handler();
//        final long start = SystemClock.uptimeMillis();
//        Projection proj = mMap.getProjection();
//        Point targetPoint = proj.toScreenLocation(target);
//        final long duration = (long) (200 + (targetPoint.y * 0.6));
//        Point startPoint = proj.toScreenLocation(marker.getPosition());
//        startPoint.y = 0;
//        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//        final Interpolator interpolator = new LinearOutSlowInInterpolator();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                long elapsed = SystemClock.uptimeMillis() - start;
//                float t = interpolator.getInterpolation((float) elapsed / duration);
//                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
//                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
//                marker.setPosition(new LatLng(lat, lng));
//                if (t < 1.0) {
//                    // Post again 16ms later == 60 frames per second
//                    handler.postDelayed(this, 16);
//                }
//            }
//        });

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





    public void sendNotificationToUser(String[] user, final ChatMessage message) {

        ArrayList<String> recipients= new ArrayList<String>(Arrays.asList(user));

        Map notification = new HashMap<>();
        notification.put("recipients", recipients);
        notification.put("message", message.getSender().replaceAll(" ", "") + ":" + eventId + ":" + message.getText());
        mDatabase.child("notifications").push().setValue(notification);

    }

    private void resizeMap(SupportMapFragment f, int newWidth, int newHeight) {
        if (f != null) {
            View view = f.getView();
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(newWidth, newHeight);
            view.setLayoutParams(p);
            view.requestLayout();

        }
    }

    private void populateMap() {
        mDatabase.child("Events").child(eventId).child("Members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String memberName = dataSnapshot.getKey();

                if (!memberName.equals(currentProfile.userId) && !dataSnapshot.getValue().toString().contentEquals("null")) {
                    addMarkerInfo(memberName);

                } else if (memberName.equals(currentProfile.userId)) {
                    mDatabase.child("Users").child(memberName).child("latitude").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            myLatitude = (Double) dataSnapshot.getValue();
                            if (myLatitude != null && myLongitude != null) {
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
                            if (myLatitude != null && myLongitude != null) {
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
                String memberName = dataSnapshot.getKey();
                if (!memberName.equals(currentProfile.userId) && !dataSnapshot.getValue().toString().contentEquals("null")) {
                    addMarkerInfo(memberName);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String memberName = dataSnapshot.getKey();
                if (markerMap.containsKey(memberName)){
                    markerMap.get(memberName).remove();
                    markerMap.remove(memberName);
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addMarkerInfo(final String memberName){
        if (!markerMap.containsKey(memberName)) {
            User user = allContacts.get(memberName);
            LatLng temp = new LatLng(47.628911, -122.342969);
            int drawableResourceId = getResources().getIdentifier(memberName.replaceAll(" ", ""), "drawable", getActivity().getPackageName());
            Marker temp2 = mMap.addMarker(new MarkerOptions()
                    .position(temp)
                    .title(memberName)
                    .snippet("Drinks: " + user.drinkCounter + " BAC: " + user.currentBAC));

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

        mDatabase.child("Users").child(memberName).child("need help").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean help = (boolean) dataSnapshot.getValue();
                if(help){
                    allContacts.get(memberName).status = true;
                    setMarkerBounce(markerMap.get(memberName), memberName);
                    markerMap.get(memberName).setZIndex(2);
                }
                else{
                    allContacts.get(memberName).status = false;
                    //markerMap.get(memberName).setAnchor(0.2f, 1.0f);

                }
                if(isAdded()){
                    int drawableResourceId = getResources().getIdentifier(memberName.replaceAll(" ",""), "drawable", getActivity().getPackageName());
                    markerMap.get(memberName).setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(drawableResourceId, help)));
                }

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

    public void setMarkerBounce(final Marker marker, final String name){
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
                else if (allContacts.get(name).status) {
                    setMarkerBounce(marker, name);
                }
                else{
                    return;
                }
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

    public Bitmap resizeFlagIcon(Bitmap imageBitmap, int width, int height){
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }



}