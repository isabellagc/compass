package compass.compass.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import compass.compass.ChatActivity;
import compass.compass.ContactsAdapter;
import compass.compass.EventsAdapter;
import compass.compass.MainActivity;
import compass.compass.R;
import compass.compass.models.Event;
import compass.compass.models.Group;
import compass.compass.models.User;

import static compass.compass.MainActivity.currentProfile;
import static compass.compass.R.id.btAddFriends;
import static compass.compass.R.id.btCreateGroup;
import static compass.compass.R.id.rvContacts;

/**
 * Created by icamargo on 7/21/17.
 */

public class NewEventFragment extends android.support.v4.app.Fragment{
    ArrayList<User> contacts;
    //public Button btCreateGroup;
    public Button btAddFriends;
    //public RecyclerView rvContacts;
    //public ContactsAdapter adapter;
    public EditText etNameBox;
    public EditText etStartTime;
    public EditText etEndTime;

    public DatabaseReference mDatabase;

    public String eventName;
    public Long endTime, startTime;
    private boolean eventCreated =  false;
    private Context context;

    public static NewEventFragment newInstance() {
        NewEventFragment fragment = new NewEventFragment();
        return fragment;
    }


    public NewEventFragment(){
        //required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_event_first, container, false);
        context = getActivity().getApplicationContext();


        //firebase reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //instantiate edit texts
        etStartTime = (EditText) v.findViewById(R.id.etStartTime);
        etEndTime = (EditText) v.findViewById(R.id.etEndTime);
        etNameBox = (EditText) v.findViewById(R.id.etNameBox);

//        //getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//
//        //get recyclerview from layout
//        rvContacts = (RecyclerView) v.findViewById(rvContacts);
//        //initialize contacts
//        contacts = MainActivity.allContacts;
//        //create adapter passing in sample contact data
//        adapter = new ContactsAdapter(context, contacts);
//        //attatch adapter to recyclerview to populate items
//        rvContacts.setAdapter(adapter);
//        //set layout manager to position the items
//        rvContacts.setLayoutManager(new LinearLayoutManager(context));

        //make button not move on keyboard show
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        btAddFriends = (Button) v.findViewById(R.id.btAddFriends);
        btAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DO STUFF HERE TO MOVE TO OTHER FRAGMENT AND THEN NOTIFY ALL USERS THAT THIS EVENT EXISTS SO THAT THEY CAN HAVE IT IN
                //THEIR HASHMAP
                //IDK IF WE NEED THIS //eventCreated = true;
                //EventsAdapter.ViewHolder.rlEvent.setClickable(true);
                //adapter.uncheckBoxes();



                //this probably goes in the next fragment
//                createEvent();
//                moveToEventView();
            }
        });

        return v;
    }

//    private void uncheckBoxes(){
//        for(int i = 0; i < adapter.getItemCount(); i ++){
//            View v = adapter.
//        }
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public Event createEvent(){
        ArrayList<User> usersAdded = new ArrayList<>();
        Event newEvent = new Event();

        //populate data fields with entered text
        eventName = etNameBox.getText().toString();
        addEventNameToUserMaps();
        adapter.setEventID(eventName);
        endTime = Long.valueOf(etEndTime.getText().toString());
        startTime = Long.valueOf(etStartTime.getText().toString());
        newEvent.setName(eventName);

        for(int i = 0; i < contacts.size(); i ++){
            User currentUser = contacts.get(i);
            if(currentUser.added.get(eventName) == true){
                usersAdded.add(currentUser);
                currentUser.currentEvent = newEvent;
            }
        }
        usersAdded.add(currentProfile);
        currentProfile.currentEvent = newEvent;


        Group group = new Group(usersAdded, true);
        addEventToDB(usersAdded);
        addEventToMessagesDB();
        newEvent.setGroup(group);
        notifyUsersOfNewEvent(usersAdded);

        //TODO: FIGURE OUT START AND END TIMES
        //newEvent.setStartTime(etStartTime.getText())
        return newEvent;
    }

    private void addEventNameToUserMaps(){
        for(User user :  contacts){
            user.added.put(eventName, false);
        }
    }

    private void addEventToDB(ArrayList<User> usersAdded){
        //add to database
        HashMap<String, Object> masterMap = new HashMap<>();
        HashMap<String, Object> infoToAdd = new HashMap<>();
        HashMap<String, Object> membersMap = new HashMap<>();

        masterMap.put(eventName, infoToAdd);
        infoToAdd.put("End", (Long) endTime);
        infoToAdd.put("Start", (Long) startTime);
        infoToAdd.put("EventName", eventName);

        for(User user: usersAdded){
            membersMap.put(user.userId, "true");
        }

        infoToAdd.put("Members", membersMap);

        mDatabase.child("Events").child(eventName).setValue(infoToAdd);
    }

    private void addEventToMessagesDB(){
        //add to database
        HashMap<String, Object> infoToAdd = new HashMap<>();

        //creates new event child
        infoToAdd.put(eventName, true);
        mDatabase.child("messages").updateChildren(infoToAdd);
    }

    private void moveToEventView() {
        Intent i = new Intent(getActivity(), ChatActivity.class);
        i.putExtra("eventId", eventName);
        i.putExtra("fromHere", "newEventFragment");
        startActivity(i);
        getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }

    private void notifyUsersOfNewEvent(ArrayList<User> usersAdded){
        for(User user: usersAdded){
            HashMap<String, Object> infoToAdd = new HashMap<>();
            infoToAdd.put(eventName, "true");
            mDatabase.child("Users").child(user.userId).child("events").updateChildren(infoToAdd);
            //TODO: ADD NOTIFICATION HERE
        }
        HashMap<String, Object> infoToAdd = new HashMap<>();
        infoToAdd.put(eventName, "true");
        mDatabase.child("Users").child(currentProfile.userId).child("events").updateChildren(infoToAdd);
    }

}


