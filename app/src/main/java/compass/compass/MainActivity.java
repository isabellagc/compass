package compass.compass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {
    public ImageButton location;
    public ImageButton drink;
    public ImageButton needhelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = (ImageButton) findViewById(R.id.location);
        drink = (ImageButton) findViewById(R.id.drink);
        needhelp = (ImageButton) findViewById(R.id.needHelp);
        launchLocation();
        lauchDrinkActivity();
        launchNeedHelp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Launch the location activity
    public void launchLocation(){
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(i);
            }
        });
    }
    //Launch the drink activity
    public void lauchDrinkActivity (){
        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DrinkActivity.class);
                startActivity(i);
            }
        });
    }
    //launch the needhelp button
    public void launchNeedHelp (){
        needhelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NeedHelpActivity.class);
                startActivity(i);
            }
        });
    }
    //launch the profile activity
    public void launchProfile (MenuItem miProfile) {
        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(i);
    }

    //launch the resourches activity
    public void launchResources (MenuItem miResources){
        Intent i = new Intent(MainActivity.this, ResourcesActivity.class);
        startActivity(i);
    }
}
