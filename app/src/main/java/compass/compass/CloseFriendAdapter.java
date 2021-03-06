package compass.compass;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.allContacts;
import static compass.compass.MainActivity.currentProfile;

/**
 * Created by brucegatete on 7/27/17.
 */


public class CloseFriendAdapter extends RecyclerView.Adapter<CloseFriendAdapter.ViewHolder> {

    public DatabaseReference mDatabase;
    //public ArrayList keys;
    static Context mContext;
    public static HashMap<String, String> mCloseFriends;
    public static ArrayList<String> mCloseFriendsNames;
    public Map userContacts;
    public GoogleMap mMap;

    public static AdapterListenerSpecial listenerSpecial;

    public CloseFriendAdapter(Context context, AdapterListenerSpecial special) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userContacts = new HashMap();
        //keys = new ArrayList();
        mCloseFriends = new HashMap<>();
        mCloseFriendsNames = new ArrayList<>();
        mContext = context;

        listenerSpecial =  special;
        getCloseFriends();
    }

    public void getCloseFriends() {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mDatabase.child("Events").child(ChatActivity.eventId).child("Members").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Object key = dataSnapshot.getKey();
                Object value = dataSnapshot.getValue();
                final String name = key.toString();

                if(!(name.contentEquals(currentProfile.userId)))
                {
                    mCloseFriendsNames.add(key.toString());

                    User user = allContacts.get(key.toString());
                    mCloseFriends.put(user.userId, user.phoneNumber);
                    mCloseFriends.put(user.userId + " status", value.toString());

                    mDatabase.child("Users").child(name).child("need help").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(mCloseFriends.containsKey(name + " need help")){
                                mCloseFriends.remove(name + " need help");
                            }
                            mCloseFriends.put(name + " need help", dataSnapshot.getValue().toString());

                            if(dataSnapshot.getValue().toString().equals("true")){
                                mCloseFriendsNames.remove(name);
                                mCloseFriendsNames.add(0, name);
                            }

                            notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Object key = dataSnapshot.getKey();
                Object value = dataSnapshot.getValue();

                mCloseFriends.remove(key + " status");
                mCloseFriends.put(key + " status", value.toString());

                notifyItemChanged(mCloseFriendsNames.indexOf(key));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Object key = dataSnapshot.getKey();
                Object value = dataSnapshot.getValue();

                mCloseFriends.remove(key);
                mCloseFriends.remove(key + " status");

                mCloseFriendsNames.remove(key);
                notifyDataSetChanged();
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
    public CloseFriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View closeFriendView = inflater.inflate(R.layout.item_emergency_contact, parent, false);
        CloseFriendAdapter.ViewHolder viewHolder = new CloseFriendAdapter.ViewHolder(closeFriendView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CloseFriendAdapter.ViewHolder holder, int position) {
        String name = mCloseFriendsNames.get(position);

        setDistance(name, holder);
        holder.tvNameContact.setText(WordUtils.capitalize(name));
        int drawableResourceId = mContext.getResources().getIdentifier(name.replaceAll(" ",""), "drawable", mContext.getPackageName());
        holder.ivProfileImage.setImageResource(drawableResourceId);
        if(mCloseFriends.get(name + " status").contentEquals("null")){
            holder.tvNameContact.setTextColor(mContext.getResources().getColor(R.color.bsp_done_text_color_disabled, null));
            holder.tvCallContact.setEnabled(false);
        }
        else{
            holder.tvNameContact.setTextColor(mContext.getResources().getColor(R.color.Black, null));
            holder.tvCallContact.setEnabled(true);
        }
        if(mCloseFriends.containsKey(name + " need help") && mCloseFriends.get(name + " need help").contentEquals("true")){
            holder.tvNameContact.setTextColor(mContext.getResources().getColor(R.color.Red, null));
            //holder.clContact.setBackgroundResource(R.color.bsp_red);
        }
        //get location on click
        holder.tvNameContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something on the map
            }
        });
        holder.tvNameContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //make something on the map

            }
        });
    }

    private void setDistance(String user, CloseFriendAdapter.ViewHolder holder){
        User friend = allContacts.get(user);
        double friendLat = friend.latitude;
        double friendLong = friend.longitude;
        double myLat = currentProfile.latitude;
        double myLong = currentProfile.longitude;

        Location temp = new Location(LocationManager.GPS_PROVIDER);
        Location tempMe = new Location(LocationManager.GPS_PROVIDER);
        temp.setLatitude(friendLat);
        temp.setLongitude(friendLong);
        tempMe.setLatitude(myLat);
        tempMe.setLongitude(myLong);
        Float distance = tempMe.distanceTo(temp);
        String distanceDisplayed = metersToMiles(distance);
        holder.tvDistance.setText(distanceDisplayed);
    }

    public String metersToMiles(Float distance){
        double miles = distance * 0.000621371 ;
        if (miles < 0.5){
            double feet = miles * 5280;
            if(feet < 15.1){
                return "0.0 ft";
            }
            return "(" + String.format("%.2f", feet) + " ft)";
        }
        return "(" + String.format("%.2f", miles) + " mi";
    }

    @Override
    public int getItemCount() {
            return mCloseFriendsNames.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNameContact;
        public TextView tvCallContact;
        public CircleImageView ivProfileImage;
        public ConstraintLayout clContact;
        public TextView tvDistance;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerSpecial.mapZoomIn(allContacts.get(mCloseFriendsNames.get(getAdapterPosition())));
                }
            });
            tvNameContact = (TextView) itemView.findViewById(R.id.tvNameContact1);
            ivProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImageMain1);
            tvCallContact = (TextView) itemView.findViewById(R.id.tvCallContact1);
            clContact = (ConstraintLayout) itemView.findViewById(R.id.clContact);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);


            tvCallContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + mCloseFriends.get(mCloseFriendsNames.get(getAdapterPosition()))));
                    if (ActivityCompat.checkSelfPermission(mContext,
                            android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mContext.startActivity(callIntent);
                }
            });
        }
    }

    public interface AdapterListenerSpecial{
        public void mapZoomIn(User user);
    }
}
