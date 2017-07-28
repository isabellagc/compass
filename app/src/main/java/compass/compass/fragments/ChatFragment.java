package compass.compass.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import compass.compass.ChatAdapter;
import compass.compass.R;
import compass.compass.models.ChatMessage;

import static compass.compass.MainActivity.currentProfile;

public class ChatFragment extends Fragment {

    EditText etMessage;
    Button btSend;
    RecyclerView rvChat;
    ChatAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    static final int POLL_INTERVAL = 1000; // milliseconds
    String eventId;
    public DatabaseReference mDatabase;
    private LinearLayoutManager layoutManager;
    RecyclerView rvContacts;
    Context context;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        context = getActivity().getApplicationContext();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //gets the bundle arguments
        eventId = getArguments().getString("eventId");

        etMessage = (EditText) v.findViewById(R.id.etMessage);
        btSend = (Button) v.findViewById(R.id.btSend);

        rvChat = (RecyclerView) v.findViewById(R.id.rvContacts);
        mFirstLoad = true;

        mAdapter = new ChatAdapter(context, eventId);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                layoutManager.smoothScrollToPosition(rvChat, null, mAdapter.getItemCount());
            }
        });


        // When send button is clicked, create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = etMessage.getText().toString();
//                ParseObject message = ParseObject.create("Message");
//                message.put(USER_ID_KEY, ParseUser.getCurrentUser().getObjectId());
//                message.put(BODY_KEY, data);

                Toast.makeText(context, data, Toast.LENGTH_SHORT).show();

                ChatMessage message = new ChatMessage();
                message.setText(data);
                message.setSender(currentProfile.name);
                message.setTime((new Date().getTime()));
                etMessage.getText().clear();
                mDatabase.child("messages").child(eventId).push().setValue(message);
                mAdapter.notifyDataSetChanged();
                rvChat.post(new Runnable() {
                    @Override
                    public void run() {
                        rvChat.smoothScrollToPosition(mAdapter.getItemCount());
                    }
                });
            }
        });

        return v;
    }


}
