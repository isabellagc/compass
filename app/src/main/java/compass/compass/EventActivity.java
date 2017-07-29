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
import android.widget.Toast;

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
                launchEvent();
                Toast.makeText(EventActivity.this, "Fab Pressed", Toast.LENGTH_SHORT).show();
            }
        });
        fab.show();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.locations_menu, menu);
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
    public void launchNewEvent () {
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
        ft.replace(R.id.frameNewEvent_1, newEventFragment);

        //commit transaction
        ft.commit();
    }

    public void launchEvent(){
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameNewEvent_1, NewEventFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
