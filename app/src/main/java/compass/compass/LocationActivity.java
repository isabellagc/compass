package compass.compass;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by brucegatete on 7/11/17.
 */

public class LocationActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
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
