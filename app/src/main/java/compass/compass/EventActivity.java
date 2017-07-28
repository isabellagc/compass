package compass.compass;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;


import java.util.ArrayList;

import compass.compass.fragments.NewEventFragment;
import compass.compass.models.Event;

public class EventActivity extends AppCompatActivity {
    public static RecyclerView rvEvents;
    ArrayList<Event> eventList;
    EventsAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_page_back_up);

        rvEvents = (RecyclerView) findViewById(R.id.rvEvents1);
        eventList = new ArrayList<>();
        eventAdapter = new EventsAdapter(this);
        rvEvents.setAdapter(eventAdapter);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.invalidate();
        initToolbar();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        //getMenuInflater().inflate(R.menu.locations_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
private void initToolbar() {
    final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    final ActionBar actionBar = getSupportActionBar();

    if (actionBar != null) {
        actionBar.setHomeAsUpIndicator(R.drawable.bsp_ic_add_circle_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}

    //launch the profile activity
    public void launchNewEvent (MenuItem miNewEvent) {
        //EventsAdapter.ViewHolder.rlEvent.setClickable(false);
        //create the user fragment
        android.support.v4.app.Fragment newEventFragment = (android.support.v4.app.Fragment) NewEventFragment.newInstance();

        //display the user timeline fragment inside the container (dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

//        ft.setCustomAnimations(android.R.anim.slide_in_left, 0);

//        View newEventContainer = findViewById(R.id.frameNewEvent);
//        View oldEventContainer = view;
//        CustomAnimator.slide(oldEventContainer, newEventContainer, CustomAnimator.DIRECTION_LEFT, 400);
        //make changes
        ft.replace(R.id.frameNewEvent, newEventFragment);

        //commit transaction
        ft.commit();
    }
}
