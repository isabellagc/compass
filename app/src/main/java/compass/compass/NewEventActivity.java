package compass.compass;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import compass.compass.models.Event;
import compass.compass.models.Group;
import compass.compass.models.User;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by icamargo on 7/12/17.
 */

public class NewEventActivity extends AppCompatActivity{
    ArrayList<User> contacts;
    public Button btCreateGroup;
    public RecyclerView rvContacts;
    public ContactsAdapter adapter;
    public EditText etNameBox;
    public EditText etStartTime;
    public EditText etEndTime;
    public DatabaseReference mDatabase;
    public String eventName;
    public String endTime, startTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        //firebase reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //instantiate edit texts
        etStartTime = (EditText) findViewById(R.id.etStartTime);
        etEndTime = (EditText) findViewById(R.id.etEndTime);
        etNameBox = (EditText) findViewById(R.id.etNameBox);

        btCreateGroup = (Button) findViewById(R.id.btCreateGroup);
        btCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
                moveToEventView();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        //get recyclerview from layout
        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        //initialize contacts
        contacts = MainActivity.allContacts;
        //create adapter passing in sample contact data
        adapter = new ContactsAdapter(this, contacts);
        //attatch adapter ot recyclerview to populate items
        rvContacts.setAdapter(adapter);
        //set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public Event createEvent(){
        ArrayList<User> usersAdded = new ArrayList<>();
        Event newEvent = new Event();

        //populate data fields with entered text
        eventName = etNameBox.getText().toString();
        endTime = etEndTime.getText().toString();
        startTime = etStartTime.getText().toString();
        newEvent.setName(eventName);

        for(int i = 0; i < contacts.size(); i ++){
            User currentUser = contacts.get(i);
            if(currentUser.added == true){
                usersAdded.add(currentUser);
                currentUser.currentEvent = newEvent;
            }
        }
        usersAdded.add(currentProfile);
        currentProfile.currentEvent = newEvent;


        Group group = new Group(usersAdded, true);
        addEventToDB(usersAdded);
        notifyUsersOfNewEvent(usersAdded);
        newEvent.setGroup(group);

        //TODO: FIGURE OUT START AND END TIMES
        //newEvent.setStartTime(etStartTime.getText())
        return newEvent;
    }

    private void addEventToDB(ArrayList<User> usersAdded){
        //add to database
        HashMap<String, Object> infoToAdd = new HashMap<>();

        //creates new event child
        infoToAdd.put(eventName, null);
        mDatabase.child("Events").updateChildren(infoToAdd);

        //now, populate the child
        infoToAdd.clear();
        infoToAdd.put("End", endTime);
        infoToAdd.put("Start", startTime);
        infoToAdd.put("EventName", eventName);
        infoToAdd.put("Members", null);
        mDatabase.child("Events").child(eventName).updateChildren(infoToAdd);

        //now, populate subchild member with appropriate members
        infoToAdd.clear();
        for(User user: usersAdded){
            infoToAdd.put(user.userId, "true");
        }
        mDatabase.child("Events").child(eventName).child("Members").updateChildren(infoToAdd);

    }

    private void moveToEventView() {
        Intent i = new Intent(NewEventActivity.this, ChatActivity.class);
        i.putExtra("eventID", eventName);
        startActivity(i);
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
