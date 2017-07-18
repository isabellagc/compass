package compass.compass;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import compass.compass.models.ChatMessage;

import static compass.compass.MainActivity.currentProfile;

/**
 * Created by brucegatete on 7/11/17.
 */

public class LocationActivity extends AppCompatActivity{

    EditText etMessage;
    Button btSend;
    RecyclerView rvChat;
    ChatAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    static final int POLL_INTERVAL = 1000; // milliseconds
    Long eventId;
    public DatabaseReference mDatabase;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        eventId = getIntent().getExtras().getLong("eventId");

        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);

        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mFirstLoad = true;

        mAdapter = new ChatAdapter(LocationActivity.this, eventId);
        rvChat.setAdapter(mAdapter);

        // associate the LayoutManager with the RecylcerView
        layoutManager = new LinearLayoutManager(LocationActivity.this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);

        mAdapter.registerAdapterDataObserver( new RecyclerView.AdapterDataObserver(){
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

                Toast.makeText(LocationActivity.this, data, Toast.LENGTH_SHORT).show();

                ChatMessage message = new ChatMessage();
                message.setText(data);

                message.setSender(currentProfile.name);
                message.setTime((new Date().getTime()));

                mDatabase.child("messages").child(eventId.toString()).push().setValue(message);
                mAdapter.notifyDataSetChanged();
                rvChat.post( new Runnable() {
                    @Override
                    public void run() {
                        rvChat.smoothScrollToPosition(mAdapter.getItemCount());
                        etMessage.setHint("Write message");
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.locations_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //launch the profile activity
    public void launchNewEvent (MenuItem miProfile) {
        Intent i = new Intent(this, NewEvent.class);
        startActivity(i);
    }

}
