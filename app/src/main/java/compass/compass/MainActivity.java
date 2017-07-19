package compass.compass;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import compass.compass.fragments.NeedHelpSwipe;
import compass.compass.models.User;



@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class MainActivity extends AppCompatActivity{
    public ImageButton location;
    public ImageButton drink;
    public ImageButton needhelp;
    public static User currentProfile;
    public static ArrayList<User> allContacts;

    public static final int OPEN_LOGIN_ACTIVITY = 11111;

    //public DataBaseHelper myDbHelper;


    //    public SQLiteDatabase myDb;
    public ArrayList<User> contacts;
    private String[] userNames;

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

        contacts = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //make users out of all items in the Users child in the database
        loadUsers();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentProfile == null){
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("usernames", contacts);
            //startActivity(i);
            startActivityForResult(i, OPEN_LOGIN_ACTIVITY);
        }

//        Intent i = new Intent(this, LoginActivity.class);
//        startActivityForResult(i, OPEN_LOGIN_ACTIVITY);
    }

    //    //WRONG DONT RUN THIS IT DELETES ALL OUR USERS
//    private void writeNewUser(String name, String email, String gender) {
//        User user = new User(name, email, gender);
//        mDatabase.child("Users").child("isabella").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                dataSnapshot.getValue();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }



    private void loadUsers(){
        mDatabase.child("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map userData = (Map) dataSnapshot.getValue();
                User user = new User();
                user.name = (String) userData.get("name");
                user.email = (String) userData.get("email");
                user.gender = (String) userData.get("gender");
                user.weight = (Integer) ((Long) userData.get("weight")).intValue();
                //user.drinkCounter = (Integer) userData.get("drinks");
                contacts.add(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("wow", "here");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("wow", "here");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d("wow", "here");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("wow", "here");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == OPEN_LOGIN_ACTIVITY){
            String name = data.getStringExtra("userToCheck");
            setCurrentUser(name);
        }
    }

    public boolean setCurrentUser(String name){
        User user = null;
        for(User x: contacts){
            if(x.name == name){
                currentProfile = x;
                Toast.makeText(MainActivity.this, "setting current profile", Toast.LENGTH_SHORT).show();
                allContacts = contacts;
                allContacts.remove(name);
            }
        }
        return false;
    }

    private void setHomeScreenButtons(){
        location = (ImageButton) findViewById(R.id.location);
        drink = (ImageButton) findViewById(R.id.drink);
        needhelp = (ImageButton) findViewById(R.id.needHelp);
    }

    private void setOnClickListeners(){
        launchLocation();
        launchDrinkActivity();
        launchNeedHelp();

    }

//    // public static ArrayList<User> getContacts() {
//        return contacts;
//    }

//    private void initializeDB(){
//        //try to create the database
//        try {
//            myDbHelper.createDatabase();
//        } catch (IOException ioe) {
//            throw new Error("Unable to create database");
//        }
//
//        //open the database
//        try {
//            myDbHelper.openDatabase();
//            //myDb = myDbHelper.myDataBase;
//        }catch(SQLException sqle){
//            throw sqle;
//        }
//    }

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
                Intent i = new Intent(MainActivity.this, EventActivity.class);
                startActivity(i);

            }
        });
    }
    //Launch the drink activity
    public void launchDrinkActivity (){
        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DrinkActivityReal.class);
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