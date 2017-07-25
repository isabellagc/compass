package compass.compass;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

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

import compass.compass.models.ChatMessage;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by brucegatete on 7/11/17.
 */

public class NeedHelpActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_help);

        level = (SeekBar) findViewById(R.id.level);
        alc = (ImageButton) findViewById(R.id.ibAlc);
        home = (ImageButton) findViewById(R.id.ibHome);
        sa = (ImageButton) findViewById(R.id.ibSA);
        other = (ImageButton) findViewById(R.id.ibOther);
        callPolice = (SwipeButton) findViewById(R.id.callPolice);
        mDatabase = FirebaseDatabase.getInstance().getReference();


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

        final Set<String> people= new HashSet<String>();

        mDatabase.child("Events").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map Events = (Map) dataSnapshot.getValue();
                Set<String> events = new HashSet<String>();
                for (Object e : Events.keySet()){
                    String temp = e.toString();
                    mDatabase.child("Events").child(temp).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map otherPeople = (Map) dataSnapshot.getValue();
                            for(Object s : otherPeople.keySet()) {
                                String temp = s.toString().replaceAll(" ", "");
                                if((otherPeople.get(s).toString().contentEquals("out")) || (otherPeople.get(s).toString().contentEquals("on call"))){
                                    people.add(temp);
                                }
                            }
                            people.remove(currentProfile.name.replaceAll(" ", ""));
                            members = (String[]) people.toArray(new String[people.size()]);

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
                ChatMessage message = new ChatMessage();
                message.setText(Alert_message);
                message.setSender(currentProfile.name);
                message.setTime((new Date().getTime()));
                //change the database and notify the adapter
                for (int i = 0; i < event_n0.length; i ++) {
                    mDatabase.child("messages").child(event_n0[i]).push().setValue(message);
                    mAdapter = new ChatAdapter(ChatActivity.class, event_n0[i]);
                    mAdapter.notifyDataSetChanged();
                }
                sendNotificationToUser(members, message);
                Toast.makeText(NeedHelpActivity.this, Alert_message, Toast.LENGTH_SHORT).show();
            }
        });

        alc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Alert_message = ("Please help, " + currentProfile.name + " needs your help. getting wasted :-(");
                ChatMessage message = new ChatMessage();
                message.setText(Alert_message);
                message.setSender(currentProfile.name);
                message.setTime((new Date().getTime()));
                //change the database and notify the adapter
                for (int i = 0; i < event_n0.length; i ++) {
                    mDatabase.child("messages").child(event_n0[i]).push().setValue(message);
                    mAdapter = new ChatAdapter(ChatActivity.class, event_n0[i]);
                    mAdapter.notifyDataSetChanged();
                }
                sendNotificationToUser(members, message);
                Toast.makeText(NeedHelpActivity.this, Alert_message, Toast.LENGTH_SHORT).show();
            }
        });

        sa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Alert_message = ("Please help, " + currentProfile.name + " needs your help getting safe from Sexual Assault!");
                ChatMessage message = new ChatMessage();
                message.setText(Alert_message);
                message.setSender(currentProfile.name);
                message.setTime((new Date().getTime()));
                //change the database and notify the adapter
                for (int i = 0; i < event_n0.length; i ++) {
                    mDatabase.child("messages").child(event_n0[i]).push().setValue(message);
                    mAdapter = new ChatAdapter(ChatActivity.class, event_n0[i]);
                    mAdapter.notifyDataSetChanged();
                }
                sendNotificationToUser(members, message);
                Toast.makeText(NeedHelpActivity.this, Alert_message, Toast.LENGTH_SHORT).show();
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
        return super.onCreateOptionsMenu(menu);
    }

    public void sendMessages (String [] members, final ChatMessage message){

    }

    public void sendNotificationToUser(String[] user, final ChatMessage message) {

        ArrayList<String> recipients= new ArrayList<String>(Arrays.asList(user));

        Map notification = new HashMap<>();
        notification.put("recipients", recipients);
        notification.put("message", message.getSender().replaceAll(" ", "") + ":" + " anything " + ":" + message.getText());
        mDatabase.child("notifications").push().setValue(notification);

    }

}
