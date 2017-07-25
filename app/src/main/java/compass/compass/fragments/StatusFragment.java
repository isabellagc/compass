package compass.compass.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import compass.compass.ChatActivity;
import compass.compass.EventActivity;
import compass.compass.EventsAdapter;
import compass.compass.R;

import static compass.compass.MainActivity.currentProfile;

public class StatusFragment extends DialogFragment {

    private RadioGroup rgStatus;
    private Button bSubmit;
    private RadioButton rbOnCall;
    private RadioButton rbOut;
    private RadioButton rbLeave;
    private DatabaseReference mDatabase;
    String fromHere;
    String eventId;

    public StatusFragment(){

    }

    public static StatusFragment newInstance(String eventId, String fromHere){
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        args.putString("fromHere", fromHere);
        args.putBoolean("newEvent", true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        eventId = getArguments().getString("eventId");
        fromHere = getArguments().getString("fromHere");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_status, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rgStatus = (RadioGroup) view.findViewById(R.id.rgStatus);

        rbLeave = (RadioButton) view.findViewById(R.id.rbLeave);
        rbOnCall = (RadioButton) view.findViewById(R.id.rbOnCall);
        rbOut = (RadioButton) view.findViewById(R.id.rbOut);

        bSubmit = (Button) view.findViewById(R.id.bSubmit);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean onCall = rbOnCall.isChecked();

                if (rgStatus.getCheckedRadioButtonId() == -1)
                {
                    Toast.makeText(getContext(), "Please choose a status", Toast.LENGTH_SHORT).show();
                }else if (rbLeave.isChecked()){
                    mDatabase.child("Users").child(currentProfile.userId).child("events").child(eventId).removeValue();
                    mDatabase.child("Events").child(eventId).child("Members").child(currentProfile.userId).removeValue();
                    dismiss();
                }else if (rbOut.isChecked()){
                    EventsAdapter eventAdapter = new EventsAdapter(getActivity().getApplicationContext());
                    ((EventActivity) getActivity()).rvEvents.setAdapter(eventAdapter);

                    mDatabase.child("Users").child(currentProfile.userId).child("events").child(eventId).setValue("out");
                    mDatabase.child("Events").child(eventId).child("Members").child(currentProfile.userId).setValue("out");
                    dismiss();

                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("fromHere", fromHere);
                    i.putExtra("eventId", eventId);
                    i.putExtra("firstLogin", "goingOut");
                    startActivity(i);
                }else if (rbOnCall.isChecked()){
                    mDatabase.child("Users").child(currentProfile.userId).child("events").child(eventId).setValue("on call");
                    mDatabase.child("Events").child(eventId).child("Members").child(currentProfile.userId).setValue("on call");
                    dismiss();

                    EventsAdapter eventAdapter = new EventsAdapter(getActivity().getApplicationContext());
                    ((EventActivity) getActivity()).rvEvents.setAdapter(eventAdapter);

                    Intent i = new Intent(getActivity(), ChatActivity.class);
                    i.putExtra("fromHere", fromHere);
                    i.putExtra("eventId", eventId);
                    startActivity(i);
                }
            }
        });


    }

}
