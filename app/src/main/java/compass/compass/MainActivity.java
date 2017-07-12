package compass.compass;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.io.IOException;
import java.util.ArrayList;

import compass.compass.fragments.NeedHelpSwipe;
import compass.compass.models.User;


public class MainActivity extends AppCompatActivity {
    public ImageButton location;
    public ImageButton drink;
    public ImageButton needhelp;
    //DataBaseManager myDb;
    public DataBaseHelper myDbHelper;
    public SQLiteDatabase myDb;
    public ArrayList<User> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        location = (ImageButton) findViewById(R.id.location);
        drink = (ImageButton) findViewById(R.id.drink);
        needhelp = (ImageButton) findViewById(R.id.needHelp);


        //DATABASE: create, initialize, and load all the potential contacts out of the premade SQL database
        //that comes with the APK of each app.
        myDbHelper = new DataBaseHelper(this);
        //initialize
        initializeDB();
        //now contacts has all the users
        //NOTE: IF WE ADD MORE CONTACTS TO OUR ORIGINAL DB go to dbhelper file and run the db_delete
        //once then take the line out and run again to delete your version of the database and store the new one.
        contacts = myDbHelper.makeUsersOutOfDB("Users");


        launchLocation();
        lauchDrinkActivity();
        launchNeedHelp();
    }

    private void initializeDB(){
        //try to create the database
        try {
            myDbHelper.createDatabase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        //open the database
        try {
            myDbHelper.openDatabase();
            //myDb = myDbHelper.myDataBase;
        }catch(SQLException sqle){
            throw sqle;
        }
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
                FragmentManager fm = getSupportFragmentManager();
                NeedHelpSwipe needHelpSwipe = NeedHelpSwipe.newInstance();
                needHelpSwipe.show(fm, "idk_what_goes_here");
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
