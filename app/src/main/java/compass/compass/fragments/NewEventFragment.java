package compass.compass.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import compass.compass.MainActivity;
import compass.compass.R;
import compass.compass.models.User;

/**
 * Created by icamargo on 7/21/17.
 */

public class NewEventFragment extends android.support.v4.app.Fragment {
    ArrayList<User> contacts;
    public Button btAddFriends;
    public EditText etNameBox;
    public EditText etStartTime;
    public EditText etEndTime;

    public DatabaseReference mDatabase;

    public String eventName;
    public Long endTime, startTime;
    public Date startDate, endDate;
    private boolean datesOK = false;

    public SingleDateAndTimePicker singleDateAndTimePicker;


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
        final View v = inflater.inflate(R.layout.fragment_new_event_first, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);;
        contacts = new ArrayList<>();
        contacts.addAll(MainActivity.allContacts.values());

        eventName = null;
        startDate = endDate = null;
        //firebase reference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //instantiate edit texts
        etStartTime = (EditText) v.findViewById(R.id.etStartTime);
        etEndTime = (EditText) v.findViewById(R.id.etEndTime);
        etNameBox = (EditText) v.findViewById(R.id.etNameBox);

        etStartTime.setShowSoftInputOnFocus(false);
        etEndTime.setShowSoftInputOnFocus(false);


        etStartTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    startTimeDisplay(view);
                }

            }
        });

        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimeDisplay(view);
            }

        });

        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimeDisplay(view);
            }

        });

        etEndTime.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    endTimeDisplay(view);
                }

            }
        });


        btAddFriends = (Button) v.findViewById(R.id.btAddFriends);
        btAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(readyToGo(view)){
                    eventName = etNameBox.getText().toString();
                    startTime = Long.valueOf(startDate.getTime());
                    endTime = Long.valueOf(endDate.getTime());
                    addEventNameToUserMaps();
                    moveToChooseContacts();
                }
            }
        });

        return v;
    }

    private void startTimeDisplay(View v){
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  // hide the soft keyboard
        }
        Date today = Calendar.getInstance().getTime();
        new SingleDateAndTimePickerDialog.Builder(getContext())
                .bottomSheet()
                .curved()
                .defaultDate(today)
                //.displayDays(false)
                .minutesStep(30)
                .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                    @Override
                    public void onDisplayed(SingleDateAndTimePicker picker) {
                        //idkif we need this listener
                    }
                })

                .title("Pick Start Time")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        startDate = date;
                        DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
                        String reportDate = df.format(date);
                        etStartTime.setText(reportDate);
                        startTime = date.getTime();
                    }
                }).display();
    }

    private void endTimeDisplay(final View v){
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  // hide the soft keyboard
        }
        Calendar cal = Calendar.getInstance();
        new SingleDateAndTimePickerDialog.Builder(getContext())
                .bottomSheet()
                .curved()
                .defaultDate(cal.getTime())
                .minutesStep(30)
                .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                    @Override
                    public void onDisplayed(SingleDateAndTimePicker picker) {

                    }
                })
                .title("Pick End Time")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        endDate = date;
                        endTime = date.getTime();
                        DateFormat df = new SimpleDateFormat("MM/dd HH:mm");
                        String reportDate = df.format(date);
                        etEndTime.setText(reportDate);
                        validateDates(v);
                    }
                }).display();
    }

    private boolean readyToGo(View v){
        if(etNameBox.getText().toString().length() == 0){
            Snackbar.make(v, "Must enter an event name!", Snackbar.LENGTH_INDEFINITE).show();
            return false;
        }else if(startDate == null || endDate == null || !datesOK){
            Snackbar.make(v, "Please enter valid start/end times!", Snackbar.LENGTH_INDEFINITE).show();
            return false;
        }else{
            return true;
        }
    }

    private void validateDates(View v){
        if(startDate == null){
            Snackbar.make(v, "Pick a start time!", Snackbar.LENGTH_INDEFINITE);
        }else if(!startDate.before(endDate)){
            Snackbar.make(v, "Start time must be before end time!", Snackbar.LENGTH_INDEFINITE);
        }else{
            datesOK = true;
        }
    }

    private void addEventNameToUserMaps(){
        for(User user :  contacts){
            user.added.put(eventName, false);
        }
    }

    private void moveToChooseContacts() {
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
