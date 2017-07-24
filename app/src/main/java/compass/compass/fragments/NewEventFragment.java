package compass.compass.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import compass.compass.MainActivity;
import compass.compass.R;
import compass.compass.models.User;

/**
 * Created by icamargo on 7/21/17.
 */

public class NewEventFragment extends android.support.v4.app.Fragment{
    ArrayList<User> contacts;
    public Button btAddFriends;
    public EditText etNameBox;
    public EditText etStartTime;
    public EditText etEndTime;

    public DatabaseReference mDatabase;

    public String eventName;
    public Long endTime, startTime;


    public static NewEventFragment newInstance() {
        NewEventFragment fragment = new NewEventFragment();
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
        //context = getActivity().getApplicationContext();
        contacts = MainActivity.allContacts;

        //firebase reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //instantiate edit texts
        etStartTime = (EditText) v.findViewById(R.id.etStartTime);
        etEndTime = (EditText) v.findViewById(R.id.etEndTime);
        etNameBox = (EditText) v.findViewById(R.id.etNameBox);


        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

//        //make button not move on keyboard show
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        btAddFriends = (Button) v.findViewById(R.id.btAddFriends);
        btAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventName = etNameBox.getText().toString();
                startTime = Long.valueOf(etStartTime.getText().toString());
                endTime = Long.valueOf(etEndTime.getText().toString());
                addEventNameToUserMaps();
                moveToChooseContacts();
            }
        });

        return v;
    }

    private void addEventNameToUserMaps(){
        for(User user :  contacts){
            user.added.put(eventName, false);
        }
    }

    private void moveToChooseContacts() {
        //get stuff from the time boxes
        //send it to the next fragment
        //move to that fragment

        //create the user fragment
        Fragment newEventContactsFragment = NewEventContactsFragment.newInstance();

        //display the user timeline fragment inside the container (dynamically)
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

        //ADD THE DATA!!
        Bundle b = new Bundle();
        b.putString("eventName", eventName);
        b.putLong("startTime", startTime);
        b.putLong("endTime", endTime);

        newEventContactsFragment.setArguments(b);

        //make changes
        ft.replace(R.id.frameNewEvent, newEventContactsFragment);

        //commit transaction
        ft.commit();

    }
}




















//
