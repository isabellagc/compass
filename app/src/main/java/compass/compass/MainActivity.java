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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Map;

import compass.compass.fragments.NeedHelpSwipe;
import compass.compass.models.User;



@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class MainActivity extends AppCompatActivity {
    public ImageButton location;
    public ImageButton drink;
    public ImageButton needhelp;
    public static User currentProfile;
    public static ArrayList<User> allContacts;

    public static final int OPEN_LOGIN_ACTIVITY = 11111;
    public ArrayList<User> contacts;
    public DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setHomeScreenButtons();
        setOnClickListeners();

        //***start of firebase db!!****//

        contacts = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //make users out of all items in the Users child in the database
        loadUsers();

        Intent servIntent = new Intent(this, LocationService.class);
        startService(servIntent);

    }

//    private void setAlarms(){
////        AlarmManager alarmManager = (AlarmManager) getSystemService(getApplicationContext().ALARM_SERVICE);
////        Intent intent = new Intent(this, AlarmReceiver.class);
////        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0010000,intent,0);
////
////        Calendar time = Calendar.getInstance();
////        time.set(Calendar.HOUR, 5);
////        time.set(Calendar.MINUTE, 59);
////        time.set(Calendar.SECOND, 0);
////
////        alarmManager.set(AlarmManager.RTC,time.getTimeInMillis(),pendingIntent);
//
//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
//        Set<String> keys = currentProfile.alarms.keySet();
//        for(String key: keys){
//            if(currentProfile.alarms.get(key).equals("null")){
//                Intent intent = new Intent(this, Alarm.class);
//                intent.putExtra("alarmName", key);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
//                alarmManager.set(AlarmManager.RTC_WAKEUP, currentProfile.alarms.get(key), pendingIntent);
//            }
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentProfile == null) {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("usernames", contacts);
            startActivityForResult(i, OPEN_LOGIN_ACTIVITY);
        }
    }

    private void loadUsers() {
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == OPEN_LOGIN_ACTIVITY) {
//            String name = data.getStringExtra("userToCheck");
//            setCurrentUser(name);
//        }
//    }

    public boolean setCurrentUser(String name) {
        User user = null;
        for (User x : contacts) {
            if (x.name == name) {
                currentProfile = x;
                Toast.makeText(MainActivity.this, "setting current profile", Toast.LENGTH_SHORT).show();
                allContacts = contacts;
                allContacts.remove(name);
            }
        }
        return false;
    }

    private void setHomeScreenButtons() {
        location = (ImageButton) findViewById(R.id.location);
        drink = (ImageButton) findViewById(R.id.drink);
        needhelp = (ImageButton) findViewById(R.id.needHelp);
    }

    private void setOnClickListeners() {
        launchLocation();
        launchDrinkActivity();
        launchNeedHelp();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Launch the location activity
    public void launchLocation() {

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EventActivity.class);
                startActivity(i);

            }
        });
    }

    //Launch the drink activity
    public void launchDrinkActivity() {
        drink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DrinkActivityReal.class);
                startActivity(i);
            }
        });
    }

    //launch the needhelp button
    public void launchNeedHelp() {
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
    public void launchProfile(MenuItem miProfile) {
        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(i);
    }

    //launch the resourches activity
    public void launchResources(MenuItem miResources) {
        Intent i = new Intent(MainActivity.this, ResourcesActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("user_"+ currentProfile.name.replaceAll(" ", ""));
        super.onBackPressed();
    }

    //    @Override
//    protected void onStop() {
//        super.onStop();
//        startService(new Intent(this, NotificationService.class));
//    }
}



















//