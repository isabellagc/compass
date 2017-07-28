package compass.compass;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compass.compass.models.User;

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


    public CloseFriendAdapter(Context context) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userContacts = new HashMap();
        //keys = new ArrayList();
        mCloseFriends = new HashMap<>();
        mCloseFriendsNames = new ArrayList<>();
        mContext = context;

        getCloseFriends();
    }

    public void getCloseFriends() {
        mDatabase.child("Events").child(ChatActivity.eventId).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Object key = ds.getKey();
                    Object value = ds.getValue();
                    mCloseFriendsNames.add(key.toString());
                    if (value.toString().equals("true")) {
                        //get the stuff from allcontacts
                        User user = allContacts.get(key.toString());
                        mCloseFriends.put(user.userId, user.phoneNumber);
                    } else {
                        mCloseFriends.put(key.toString(), value.toString());
                    }
                    mCloseFriendsNames.remove(currentProfile.userId);
                }
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
        holder.tvNameContact.setText(mCloseFriendsNames.get(position));
        int drawableResourceId = mContext.getResources().getIdentifier(mCloseFriendsNames.get(position).replaceAll(" ",""), "drawable", mContext.getPackageName());
        holder.ivProfileImage.setImageResource(drawableResourceId);
    }

    @Override
    public int getItemCount() {
            return mCloseFriendsNames.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNameContact;
        public Button btCallContact;
        public ImageView ivProfileImage;
        public static ConstraintLayout clContact;

        public ViewHolder(View itemView) {
            super(itemView);

            tvNameContact = (TextView) itemView.findViewById(R.id.tvNameContact);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            btCallContact = (Button) itemView.findViewById(R.id.btCallContact);
            clContact = (ConstraintLayout) itemView.findViewById(R.id.clContact);


            btCallContact.setOnClickListener(new View.OnClickListener() {
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
}
