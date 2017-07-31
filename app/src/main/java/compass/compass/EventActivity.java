package compass.compass;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import compass.compass.fragments.NewEventFragment;
import compass.compass.models.Event;

public class EventActivity extends AppCompatActivity {
    public static RecyclerView rvEvents;
    ArrayList<Event> eventList;
    EventsAdapter eventAdapter;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_page_back_up);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        rvEvents = (RecyclerView) findViewById(R.id.rvEvents1);
        eventList = new ArrayList<>();
        eventAdapter = new EventsAdapter(this);
        rvEvents.setAdapter(eventAdapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.invalidate();
        //initToolbar();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNewEvent();
                fab.hide();
            }
        });
    }

private void initToolbar() {
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();
}

    //launch the new activity

    public void launchNewEvent(){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameNewEvent, NewEventFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
