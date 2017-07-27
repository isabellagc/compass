package compass.compass.fragments;

import android.app.NotificationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import compass.compass.ChatAdapter;
import compass.compass.EmergencyContactsAdapter;
import compass.compass.R;
import compass.compass.models.ChatMessage;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by brucegatete on 7/26/17.
 */

public class MessagesFragment extends Fragment {
    RecyclerView rvChat;
    ChatAdapter mAdapter;
    EditText etMessage;
    Button btSend;
    Map<String, Marker> markerMap;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    static final int POLL_INTERVAL = 1000; // milliseconds
    String eventId;

    public DatabaseReference mDatabase;
    private LinearLayoutManager layoutManager;
    Double latitude;
    Double longitude;
    NotificationManager mNotificationManager;
    Uri linkToPic;
    public int NOTIFICATION_ID = 12;
    boolean alarmSet = false;
    EmergencyContactsAdapter emergencyContactsAdapter;
    String myStatus;

    String[] members;


    boolean chatExpanded;
    SupportMapFragment mapFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_messages, container, false);
        eventId = getActivity().getIntent().getExtras().getString("eventId");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        etMessage = (EditText) v.findViewById(R.id.etMessage);
        btSend = (Button) v.findViewById(R.id.btSend);
        rvChat = v.findViewById(R.id.rvContacts);
        mFirstLoad = true;

        mAdapter = new ChatAdapter(getActivity(), eventId);
        rvChat.setAdapter(mAdapter);

        chatExpanded = false;

         //associate the LayoutManager with the RecylcerView
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);

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
                    Toast.makeText(getActivity(), "Cannot send empty message", Toast.LENGTH_SHORT).show();
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
        return v;
    }

    public void sendNotificationToUser(String[] user, final ChatMessage message) {

        ArrayList<String> recipients= new ArrayList<String>(Arrays.asList(user));

        Map notification = new HashMap<>();
        notification.put("recipients", recipients);
        notification.put("message", message.getSender().replaceAll(" ", "") + ":" + eventId + ":" + message.getText());
        mDatabase.child("notifications").push().setValue(notification);

    }
}
