package compass.compass;

import android.content.Intent;
import android.database.SQLException;

import android.os.Build;

import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import compass.compass.fragments.NeedHelpSwipe;
import compass.compass.models.User;



@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class MainActivity extends AppCompatActivity{
    public ImageButton location;
    public ImageButton drink;
    public ImageButton needhelp;
    public User currentProfile;

    public static final int OPEN_LOGIN_ACTIVITY = 11111;

    public DataBaseHelper myDbHelper;

    public static ArrayList<User> contacts;


//    public SQLiteDatabase myDb;
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
//            myDbHelper = new DataBaseHelper(this);
//            //initialize
//            initializeDB();
//
//            //now contacts has all the users
//            //NOTE: IF WE ADD MORE CONTACTS TO OUR ORIGINAL DB go to dbhelper file and run the db_delete
//            //once then take the line out and run again to delete your version of the database and store the new one.
//            contacts = myDbHelper.makeUsersOutOfDB("Users");

        //***start of firebase db!!****//

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //make users out of all items in the Users child in the database
        makeAllUsers();

        Intent i = new Intent(this, LoginActivity.class);
        startActivityForResult(i, OPEN_LOGIN_ACTIVITY);
    }

    //WRONG DONT RUN THIS IT DELETES ALL OUR USERS
//    private void writeNewUser(String name, String email, String gender) {
//        User user = new User(name, email, gender);
//        mDatabase.child("Users").setValue(user);
//    }




        //DATABASE: create, initialize, and load all the potential contacts out of the premade SQL database
        //that comes with the APK of each app.
       // myDbHelper = new DataBaseHelper(this);
        //initialize
      //  initializeDB();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == OPEN_LOGIN_ACTIVITY){
            String user = data.getStringExtra("userToCheck");
            mDatabase.child("Users").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map s = (Map) dataSnapshot.getValue();
                    if(s != null){
                        currentProfile = new User();

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private void makeAllUsers(){
        DatabaseReference reference = mDatabase.child("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    Map s = (Map) userSnapshot.getValue();
                    Log.i("Make all users", "made it ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setCurrentUser(Map <String, Object> s){

    }

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
//                String token = FirebaseInstanceId.getInstance().getToken();
//                //        registerToken(token);
//                Toast.makeText(MainActivity.this, token, Toast.LENGTH_LONG).show();
//                Log.d("Token Bruce", token);
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
