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
import android.widget.TextView;

import java.util.ArrayList;

import compass.compass.models.Event;
import compass.compass.models.Group;
import compass.compass.models.User;

import static compass.compass.MainActivity.allContacts;

/**
 * Created by icamargo on 7/12/17.
 */

public class NewEvent extends AppCompatActivity{
    ArrayList<User> contacts;
    public Button btCreateGroup;
    public RecyclerView rvContacts;
    public ContactsAdapter adapter;
    public TextView tvEventName;
    public EditText etStartTime;
    public EditText etEndTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);


        etStartTime = (EditText) findViewById(R.id.etStartTime);
        etEndTime = (EditText) findViewById(R.id.etEndTime);


        tvEventName = (TextView) findViewById(R.id.tvEventName);
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
        //contacts = MainActivity.getContacts();
        //create adapter passing in sample contact data
        adapter = new ContactsAdapter(this, allContacts);
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
        newEvent.setName((String)tvEventName.getText());

        for(int i = 0; i < contacts.size(); i ++){
            User currentUser = contacts.get(i);
            if(currentUser.added == true){
                usersAdded.add(currentUser);
                currentUser.currentEvent = newEvent;
            }
        }
        Group group = new Group(usersAdded, true);
        newEvent.setGroup(group);
        //TODO: FIGURE OUT START AND END TIMES
        //newEvent.setStartTime(etStartTime.getText())
        return newEvent;
    }

    private void moveToEventView() {
        Intent i = new Intent(NewEvent.this, LocationActivity.class);
        startActivity(i);
    }

}
