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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import compass.compass.ChatActivity;
import compass.compass.ContactsAdapter;
import compass.compass.EventActivity;
import compass.compass.EventsAdapter;
import compass.compass.MainActivity;
import compass.compass.R;
import compass.compass.SimpleDividerItemDecoration;
import compass.compass.models.ChatMessage;
import compass.compass.models.Event;
import compass.compass.models.Group;
import compass.compass.models.User;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by icamargo on 7/22/17.
 */

public class NewEventContactsFragment extends android.support.v4.app.Fragment {
    ArrayList<User> contacts;
    public Button btCreateEvent;
    public RecyclerView rvContacts;
    public ContactsAdapter adapter;
    public DatabaseReference mDatabase;

    //WE NEED TO GET THESE THROUGH A BUNDLE FROM PREVIOUS FRAGMENT
    public String eventName;
    public Long endTime, startTime;

    private Context context;

    public static NewEventContactsFragment newInstance() {
        NewEventContactsFragment fragment = new NewEventContactsFragment();
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    public NewEventContactsFragment(){
        //required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        eventName = "TESTIN";
        endTime = new Long(1000);
        startTime = new Long(10000);

        Bundle b = this.getArguments();
        if(b != null){
            eventName = b.getString("eventName");
            startTime = b.getLong("startTime");
            endTime = b.getLong("endTime");
        }

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_event_contacts, container, false);
        context = getActivity().getApplicationContext();

        //**firebase reference**
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //get recyclerview from layout
        rvContacts = (RecyclerView) v.findViewById(R.id.rvContacts);
        //initialize contacts
        contacts = new ArrayList<>();
        contacts.addAll(MainActivity.allContacts.values());
        //create adapter passing in sample contact data
        adapter = new ContactsAdapter(context, contacts, eventName);
        //attatch adapter to recyclerview to populate items
        rvContacts.setAdapter(adapter);
        rvContacts.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        //set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(context));

        //make button not move on keyboard show
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        btCreateEvent = (Button) v.findViewById(R.id.btCreateEvent);
        btCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DO STUFF HERE TO MAKE THE EVENT AND THE USERS AND UNCHECK THE PEOPLE BUT IDK IF WE NEED TO DO THAT
                createEvent();
                moveToEventView();
            }
        });

        return v;
    }

    public Event createEvent(){
        ArrayList<User> usersAdded = new ArrayList<>();
        Event newEvent = new Event();

        adapter.setEventID(eventName);
        newEvent.setName(eventName);
        newEvent.setStartTime(startTime);
        newEvent.setEndTime(endTime);

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

    private void addEventToDB(ArrayList<User> usersAdded){
        //add to database
        HashMap<String, Object> masterMap = new HashMap<>();
        HashMap<String, Object> infoToAdd = new HashMap<>();
        HashMap<String, Object> membersMap = new HashMap<>();

        masterMap.put(eventName, infoToAdd);
        infoToAdd.put("End", endTime);
        infoToAdd.put("Start", startTime);
        infoToAdd.put("EventName", eventName);

        for(User user: usersAdded){
            membersMap.put(user.userId, "null");
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

        String data = currentProfile.name + " created the event.";
        ChatMessage message = new ChatMessage();
        message.setText(data);
        message.setSender("JOINED");
        message.setTime((new Date().getTime()));
        mDatabase.child("messages").child(eventName).push().setValue(message);

        String newData = currentProfile.name + " joined the event as going out.";
        ChatMessage newMessage = new ChatMessage();
        newMessage.setText(newData);
        newMessage.setSender("JOINED");
        newMessage.setTime((new Date().getTime()));
        mDatabase.child("messages").child(eventName).push().setValue(newMessage);
    }

    private void notifyUsersOfNewEvent(ArrayList<User> usersAdded){
        for(User user: usersAdded){
            HashMap<String, Object> infoToAdd = new HashMap<>();
            infoToAdd.put(eventName, "null");
            mDatabase.child("Users").child(user.userId).child("events").updateChildren(infoToAdd);
            //TODO: ADD NOTIFICATION HERE
        }
        HashMap<String, Object> infoToAdd = new HashMap<>();
        infoToAdd.put(eventName, "out");
        mDatabase.child("Users").child(currentProfile.userId).child("events").updateChildren(infoToAdd);

        mDatabase.child("Users").child(currentProfile.userId).child("events").child(eventName).setValue("out");
        mDatabase.child("Events").child(eventName).child("Members").child(currentProfile.userId).setValue("out");

        ((EventActivity) getActivity()).rvEvents.getAdapter().notifyDataSetChanged();

    }


    private void moveToEventView() {
        Intent i = new Intent(getActivity(), ChatActivity.class);
        i.putExtra("eventId", eventName);
        i.putExtra("fromHere", "newEventFragment");
        startActivity(i);

//        String fromHere = "newEventFragment";
//
//        FragmentManager fm = getActivity().getSupportFragmentManager();
//        StatusFragment statusFragment = StatusFragment.newInstance(eventName, fromHere);
//        statusFragment.show(fm, "tag");

        EventsAdapter eventAdapter = new EventsAdapter(getActivity().getApplicationContext());
        ((EventActivity) getActivity()).rvEvents.setAdapter(eventAdapter);

        getFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();

    }


}
