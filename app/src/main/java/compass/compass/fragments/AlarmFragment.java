package compass.compass.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;

import compass.compass.Alarm;
import compass.compass.R;
import compass.compass.models.User;

import static android.content.Context.ALARM_SERVICE;
import static compass.compass.MainActivity.currentProfile;

public class AlarmFragment extends DialogFragment {

    private SingleDateAndTimePicker singleDateAndTimePicker;
    private Button btSetAlarm, btNoAlarm;
    private DatabaseReference mDatabase;
    String fromHere;
    static String eventId;
    public Date alarmTime;
    //make the database reference and push the alarmtime into the user alarmtime

    public AlarmFragment(){

    }

    public static AlarmFragment newInstance(String eventIdInfo, String fromHere){
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventIdInfo);
        args.putString("fromHere", fromHere);
        args.putBoolean("newEvent", true);
        fragment.setArguments(args);
        eventId = eventIdInfo;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        eventId = getArguments().getString("eventId");
        fromHere = getArguments().getString("fromHere");
        alarmTime = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        eventId = b.getString("eventId");
        alarmTime = null;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragement_alarm, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        btNoAlarm = view.findViewById(R.id.btNoAlarm);
        btSetAlarm = view.findViewById(R.id.btSetAlarm);
        singleDateAndTimePicker = view.findViewById(R.id.sdTimePicker);

        btSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmTime = singleDateAndTimePicker.getDate();
                HashMap<String, Object> infoToAdd = new HashMap<>();
                infoToAdd.put(eventId.toString(), alarmTime.getTime());
                initAlarm();
                mDatabase.child("Users").child(currentProfile.userId).child("alarms").updateChildren(infoToAdd);
                dismiss();
//                Intent i = new Intent(getActivity(), ChatActivity.class);
//                i.putExtra("fromHere", fromHere);
//                i.putExtra("eventId", eventId);
//                startActivity(i);
//                if (rbOut.isChecked()){
//                    mDatabase.child("Users").child(currentProfile.userId).child("events").child(eventId).setValue("out");
//                    mDatabase.child("Events").child(eventId).child("Members").child(currentProfile.userId).setValue("out");
//                    dismiss();
//
//                    Intent i = new Intent(getActivity(), ChatActivity.class);
//                    i.putExtra("fromHere", fromHere);
//                    i.putExtra("eventId", eventId);
//                    startActivity(i);
//                }
//                if (rbOnCall.isChecked()){
//                    mDatabase.child("Users").child(currentProfile.userId).child("events").child(eventId).setValue("on call");
//                    mDatabase.child("Events").child(eventId).child("Members").child(currentProfile.userId).setValue("on call");
//                    dismiss();
//
//                    Intent i = new Intent(getActivity(), ChatActivity.class);
//                    i.putExtra("fromHere", fromHere);
//                    i.putExtra("eventId", eventId);
//                    startActivity(i);
//                }
            }
        });
        btNoAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> infoToAdd = new HashMap<>();
                infoToAdd.put(eventId, "null");
                mDatabase.child("Users").child(currentProfile.userId).child("alarms").updateChildren(infoToAdd);
                currentProfile.alarms.put(eventId, User.KEY_NULL_VALUE);
                dismiss();
//                Intent i = new Intent(getActivity(), ChatActivity.class);
//                i.putExtra("fromHere", fromHere);
//                i.putExtra("eventId", eventId);
//                startActivity(i);
            }
        });


    }

    private void initAlarm(){
        AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getActivity().getApplicationContext(), Alarm.class);
        intent.putExtra("alarmName", eventId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(),0,intent,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTime(), pendingIntent);
    }

}
