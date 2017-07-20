package compass.compass;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import compass.compass.fragments.ChatFragment;

public class EventDetailsActivity extends AppCompatActivity {

    Context context;
    ViewPager vpChat;
    ViewPager vpMap;
    ChatFragment chatFragment;
    Long eventId;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);


        eventId = getIntent().getExtras().getLong("eventId");

        android.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("eventId", eventId.toString());
        // set Fragmentclass Arguments
        chatFragment = new ChatFragment();
        chatFragment.setArguments(bundle);
        transaction.replace(R.id.fChat, chatFragment);
        transaction.commit();
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
