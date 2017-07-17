package compass.compass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import compass.compass.models.Event;

public class EventActivity extends AppCompatActivity {

    RecyclerView rvEvents;
    ArrayList<Event> eventList;
    EventsAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);

        rvEvents = (RecyclerView) findViewById(R.id.rvEvents);
        eventList = new ArrayList<>();

        eventAdapter = new EventsAdapter(this);

        rvEvents.setAdapter(eventAdapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.invalidate();
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
