package compass.compass;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import static compass.compass.MainActivity.allContacts;
import static compass.compass.MainActivity.currentProfile;

/**
 * Created by amusipatla on 7/14/17.
 */

public class EmergencyContactsAdapter extends RecyclerView.Adapter<EmergencyContactsAdapter.ViewHolder> {

    public static HashMap<String, String> mEmergencyContacts;
    public static ArrayList<String> mEmergencyContactNames;
    public Map userContacts;
    public DatabaseReference mDatabase;
    //public ArrayList keys;
    static Context mContext;
    LocationManager locationManager;
    Location myLocation;

    public EmergencyContactsAdapter(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userContacts = new HashMap();
        //keys = new ArrayList();
        mEmergencyContacts = new HashMap<>();
        mEmergencyContactNames = new ArrayList<>();
        mContext = context;

        getEmergencyContacts();
    }

    private void getEmergencyContacts(){
        mDatabase.child("Users").child(MainActivity.currentProfile.userId).child("emergency contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Object key = ds.getKey();
                    Object value = ds.getValue();
                    mEmergencyContactNames.add(key.toString());
                    if(value.toString().equals("true")){
                        //get the stuff from allcontacts
                        User user = allContacts.get(key.toString());
                        mEmergencyContacts.put(user.userId, user.phoneNumber);
                    }else{
                        mEmergencyContacts.put(key.toString(), value.toString());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public EmergencyContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View emergencyContactView = inflater.inflate(R.layout.item_emergency_contact, parent, false);
        ViewHolder viewHolder = new ViewHolder(emergencyContactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvNameContact.setText(WordUtils.capitalize(mEmergencyContactNames.get(position)));
        if(mEmergencyContactNames != null){
            if(allContacts.containsKey(mEmergencyContactNames.get(position))){
                int drawableResourceId = mContext.getResources().getIdentifier(mEmergencyContactNames.get(position).replaceAll(" ",""), "drawable", mContext.getPackageName());
                holder.ivProfileImage.setImageResource(drawableResourceId);
                setDistance(mEmergencyContactNames.get(position), holder);
            }
            else{
                holder.tvDistance.setText("");
                holder.ivProfileImage.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private void setDistance(String user, EmergencyContactsAdapter.ViewHolder holder){
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
            if(feet < 10.1){
                return "0.0 ft";
            }
            return String.format("%.2f", feet) + " ft";
        }
        return String.format("%.2f", miles) + " mi";
    }

    @Override
    public int getItemCount() {
        return mEmergencyContactNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvNameContact;
        public TextView tvCallContact;
        public TextView tvDistance;
        public ImageView ivProfileImage;
        //USE THIS TO SET AN ONCLICKLISTENER FOR THE CONTACT THAT BRINGS YOU TO A VIEW OF THAT PERSON
        //OR THEM ON THE MAP!!!
        public static ConstraintLayout clContact;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNameContact = (TextView) itemView.findViewById(R.id.tvNameContact1);
            tvCallContact = (TextView) itemView.findViewById(R.id.tvCallContact1);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImageMain1);
            tvDistance = (TextView) itemView.findViewById(R.id.tvDistance);
            clContact = (ConstraintLayout) itemView.findViewById(R.id.clContact);

            tvCallContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + mEmergencyContacts.get(mEmergencyContactNames.get(getAdapterPosition()))));
                    if (ActivityCompat.checkSelfPermission(mContext,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("YIKES", "lol u don't have permission to call");
                        return;
                    }
                    mContext.startActivity(callIntent);
                }
            });

            //would set onclicklistener for contact here...
        }
    }


}
