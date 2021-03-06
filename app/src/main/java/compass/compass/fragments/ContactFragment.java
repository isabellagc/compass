package compass.compass.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.text.WordUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import compass.compass.R;
import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.allContacts;


public class ContactFragment extends DialogFragment implements OnMapReadyCallback, Message911MenuItemFragment.Message911FragmentListener{

    TextView tvNameContact;
    CircleImageView ivProfileImageMain;
    TextView tvCallContact;
    TextView tvDrinkCount;
    TextView tvBACcount;
    User user;
    TextView tvMessageContact;
    private GoogleMap mMap;
    double latitude;
    double longitude;
    Marker friendLocation;
    private static View view;
    public TextView tvInfo;
    public int drawableResourceId;
    public String formatted;


    public ContactFragment() {

    }


    public static ContactFragment newInstance(String name) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String name = getArguments().getString("name");
            user = allContacts.get(name);
            latitude = user.latitude;
            longitude = user.longitude;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_contact, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvMessageContact = view.findViewById(R.id.tvMessageContact);
        tvNameContact = view.findViewById(R.id.tvNameContact1);
        tvCallContact = view.findViewById(R.id.tvCallContact1);
        ivProfileImageMain = view.findViewById(R.id.ivProfileImageMain1);

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        Double BAC = user.currentBAC;
        formatted = df.format(BAC);

        tvBACcount = view.findViewById(R.id.tvBACcount);
        tvBACcount.setText(formatted);
        tvDrinkCount = view.findViewById(R.id.tvDrinkCount);
        tvDrinkCount.setText(String.valueOf(user.drinkCounter));

        drawableResourceId = getResources().getIdentifier(user.userId.replaceAll(" ",""), "drawable", getActivity().getPackageName());

        ivProfileImageMain.setImageResource(drawableResourceId);

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.friendMap);
        mapFragment.getMapAsync(this);

        tvCallContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + user.phoneNumber));
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("YIKES", "lol u don't have permission to call");
                    return;
                }
                getActivity().getApplicationContext().startActivity(callIntent);
            }
        });

        tvMessageContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                MessageFriendFragment messageFriendFragment = MessageFriendFragment.newInstance(user.phoneNumber, user.name);
                messageFriendFragment.show(fm, "TAG");
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        tvNameContact.setText(WordUtils.capitalize(user.name));
        latitude = user.latitude;
        longitude = user.longitude;

        if(friendLocation == null){
            friendLocation = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(drawableResourceId)))
                    .title(WordUtils.capitalize(user.userId))
                    .snippet("Drinks: " + user.drinkCounter + " BAC: " + formatted));

            setMarkerBounce(friendLocation);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLocation.getPosition(), 15));
    }

    public void setMarkerBounce(final Marker marker){
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
                else {
                    setMarkerBounce(marker);
                }
            }
        });


    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        Activity activity = getActivity();
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View customMarkerView = layoutInflater.inflate(R.layout.view_custom_marker_red, null);
        //View customMarkerView = getActivity().getLayoutInflater().inflate(R.layout.view_custom_marker_red, null);
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

    @Override
    public void launchNeedHelpFromMessage() {

    }
}
