package compass.compass.fragments;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import compass.compass.NeedHelpActivity;
import compass.compass.R;
import compass.compass.models.ChatMessage;

import static compass.compass.MainActivity.currentProfile;
import static compass.compass.MainActivity.peopleInEvents;

/**
 * Created by brucegatete on 7/26/17.
 */

public class MessagesFragment extends Fragment implements Call911MenuItemFragment.Call911FragmentListener, Message911MenuItemFragment.Message911FragmentListener{
    public static RecyclerView rvChat;
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
                    etMessage.clearComposingText();
                    etMessage.setText("");
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




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_cancel, menu);
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.markSafe);

        if(!currentProfile.status) {
            menuItem.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void markSafe(final MenuItem menuItem){
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Light_Dialog).create();
        alertDialog.setTitle("Mark Yourself Safe");
        alertDialog.setMessage("Are you sure you would like to mark yourself as safe?");
        alertDialog.setIcon(R.drawable.ic_need_help);

        DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentProfile.userId).child("need help").setValue(false);
                currentProfile.status = false;

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setText(currentProfile.userId + " has marked themselves as safe");
                chatMessage.setSender("SAFE");
                chatMessage.setTime((new Date().getTime()));
                NeedHelpActivity.sendNotificationToUser(peopleInEvents, chatMessage, mDatabase);

                getActivity().recreate();

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

    public void message911(final MenuItem menuItem){
        FragmentManager fm = getFragmentManager();
        Message911MenuItemFragment message911MenuItemFragment = Message911MenuItemFragment.newInstance(this);
        message911MenuItemFragment.show(fm, "tag");
    }

    public void call911(final MenuItem menuItem) {
        FragmentManager fm = getFragmentManager();
        Call911MenuItemFragment call911MenuItemFragment = Call911MenuItemFragment.newInstance(this);
        call911MenuItemFragment.show(fm, "TAG");
    }

    @Override
    public void launchNeedHelpFragment() {
        mDatabase.child("User Status").child(currentProfile.userId).setValue("help");
        mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
        currentProfile.status = true;
        Intent i = new Intent(getContext(), NeedHelpActivity.class);
        i.putExtra("launchHelp", true);
        startActivity(i);
    }

    @Override
    public void launchNeedHelpFromMessage() {
        mDatabase.child("User Status").child(currentProfile.userId).setValue("help");
        mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
        currentProfile.status = true;
        Intent i = new Intent(getContext(), NeedHelpActivity.class);
        i.putExtra("launchHelp", true);
        startActivity(i);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == CALL_ACTIVITY_CODE){
//            Intent i = new Intent(getContext(), NeedHelpActivity.class);
//            startActivity(i);
//        }
//    }

}
