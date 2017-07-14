package compass.compass;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

import compass.compass.fragments.NeedHelpSwipe;
import compass.compass.models.User;


public class MainActivity extends AppCompatActivity {
    public ImageButton location;
    public ImageButton drink;
    public ImageButton needhelp;

    public DataBaseHelper myDbHelper;
    public SQLiteDatabase myDb;
    public static ArrayList<User> contacts;

    public DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setHomeScreenButtons();
        setOnClickListeners();

        //*** TODO: DELETE THIS AND OTHER LOCAL DATABSE THINGS AND HOOKUP TO NEW FIREBASE DB WHEN SETUP
            //DATABASE: create, initialize, and load all the potential contacts out of the premade SQL database
            //that comes with the APK of each app.
            myDbHelper = new DataBaseHelper(this);
            //initialize
            initializeDB();

            //now contacts has all the users
            //NOTE: IF WE ADD MORE CONTACTS TO OUR ORIGINAL DB go to dbhelper file and run the db_delete
            //once then take the line out and run again to delete your version of the database and store the new one.
            contacts = myDbHelper.makeUsersOutOfDB("Users");

        //***start of firebase db!!****//
        final String[] s = {"not working"};

        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query q = mDatabase.child("Users").equalTo("isabella");

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    s[0] = postSnapshot.toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("aggggg", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });



//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                //GenericTypeIndicator<List<String>> t = new GenericTypeIndicator ?<List<String>>() {};
//                List messages = dataSnapshot.getValue(true);
//                if( messages == null ) {
//                    System.out.println("No users");
//                }
//                else {
//                    Log.i("Firebase", "user found i think");
//                    System.out.println("The first user is: " + messages.get(0) );
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.i("Firebase", "firebase array thing didnt work");
//            }
//        });


    }

    //WRONG DONT RUN THIS IT DELETES ALL OUR USERS
//    private void writeNewUser(String name, String email, String gender) {
//        User user = new User(name, email, gender);
//        mDatabase.child("Users").setValue(user);
//    }


    private void setHomeScreenButtons(){
        location = (ImageButton) findViewById(R.id.location);
        drink = (ImageButton) findViewById(R.id.drink);
        needhelp = (ImageButton) findViewById(R.id.needHelp);
    }

    private void setOnClickListeners(){
        launchLocation();
        lauchDrinkActivity();
        launchNeedHelp();
    }

    public static ArrayList<User> getContacts() {
        return contacts;
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
