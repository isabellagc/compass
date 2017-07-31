package compass.compass;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import compass.compass.fragments.NeedHelpSwipe;
import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;


@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class MainActivity extends AppCompatActivity {
//    public ImageButton location;
//    public ImageButton drink;
//    public ImageButton needhelp;
    public CardView cvFindFriends;
    public CardView cvGetHelp;
    public CardView cvDrinkCounter;
    public TextView tvNameBox;
    ImageView ivProfileImage;
    public static User currentProfile;
    public static HashMap<String, User> allContacts;

    public static final int OPEN_LOGIN_ACTIVITY = 11111;
    public ArrayList<User> contacts;
    public DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (currentProfile == null) {
            Intent i = new Intent(this, LoginActivity.class);
            i.putExtra("usernames", contacts);
            startActivityForResult(i, OPEN_LOGIN_ACTIVITY);
        }else{

            if(currentProfile.status){
                getTheme().applyStyle(R.style.AppThemeInverted, true);
            }
            else{
                getTheme().applyStyle(R.style.AppTheme, true);
            }

            //setContentView(R.layout.activity_main);
            setContentView(R.layout.activity_main_android_style);
            setHomeScreenButtons();
            setOnClickListeners();

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

            //***start of firebase db!!****//

            contacts = new ArrayList<>();

            mDatabase = FirebaseDatabase.getInstance().getReference();
            //make users out of all items in the Users child in the database
            loadUsers();


        }



        Intent servIntent = new Intent(this, LocationService.class);
        startService(servIntent);

        SessionConfiguration config = new SessionConfiguration.Builder()
                // mandatory
                .setClientId("qzgZk0HDAofYbSO_lKWE5D4Law9icADO")
                // required for enhanced button features
                .setServerToken("ZviX9nLG1vN8CC8xmWFxOsIGU2tCCKibo2cjJRV9")
                // required for implicit grant authentication
                .setRedirectUri("<REDIRECT_URI>")
                // required scope for Ride Request Widget features
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                // optional: set Sandbox as operating environment
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();

        UberSdk.initialize(config);

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
        //setContentView(R.layout.activity_main_pretty);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(currentProfile != null){
            if(currentProfile.status){
                getTheme().applyStyle(R.style.AppThemeInverted, true);
            }
            else{
                getTheme().applyStyle(R.style.AppTheme, true);
            }

            //setContentView(R.layout.activity_main);
            setContentView(R.layout.activity_main_android_style);
            setHomeScreenButtons();
            setOnClickListeners();

            mDatabase = FirebaseDatabase.getInstance().getReference();
        }

        invalidateOptionsMenu();

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


    private void setHomeScreenButtons() {
//        location = (ImageButton) findViewById(R.id.location);
//        drink = (ImageButton) findViewById(R.id.drink);
//        needhelp = (ImageButton) findViewById(R.id.needHelp);
        tvNameBox = (TextView) findViewById(R.id.tvNameBox);
        String name = WordUtils.capitalize(currentProfile.userId);
        tvNameBox.setText(name);

        cvFindFriends = (CardView) findViewById(R.id.cvFindFriends);
        cvGetHelp = (CardView) findViewById(R.id.cvGetHelp);
        cvDrinkCounter = (CardView) findViewById(R.id.cvDrinkCounter);
        ivProfileImage = (CircleImageView) findViewById(R.id.ivProfileImageMain);
        ivProfileImage.setImageResource(getResources().getIdentifier(currentProfile.userId.replaceAll(" ",""), "drawable", getPackageName()));
    }

    private void setOnClickListeners() {
        launchLocation();
        launchDrinkActivity();
        launchNeedHelp();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cancel, menu);
        MenuItem menuItem = (MenuItem) menu.findItem(R.id.markSafe);

        if(!currentProfile.status) {
            menuItem.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void markSafe(final MenuItem menuItem){
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog).create();
        alertDialog.setTitle("Mark Yourself Safe");
        alertDialog.setMessage("Are you sure you would like to mark yourself as safe?");
        alertDialog.setIcon(R.drawable.ic_need_help);

        DialogInterface.OnClickListener yes = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentProfile.userId).child("need help").setValue(false);
                currentProfile.status = false;

                recreate();

                alertDialog.dismiss();
            }
        };

        DialogInterface.OnClickListener no = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        };

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", no);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", yes);

        alertDialog.show();
    }
    //Launch the location activity
    public void launchLocation() {
//        location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, EventActivity.class);
//                startActivity(i);
//
//            }
//        });
        cvFindFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EventActivity.class);
                startActivity(i);

            }
        });
    }

    //Launch the drink activity
    public void launchDrinkActivity() {
//        drink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, DrinkActivityReal.class);
//                startActivity(i);
//            }
//        });
        cvDrinkCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DrinkActivityReal.class);
                startActivity(i);
            }
        });
    }

    //launch the needhelp button
    public void launchNeedHelp() {
//        needhelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fm = getSupportFragmentManager();
//                NeedHelpSwipe needHelpSwipe = NeedHelpSwipe.newInstance();
//                needHelpSwipe.show(fm, "idk_what_goes_here");
//            }
//        }
        cvGetHelp.setOnClickListener(new View.OnClickListener() {
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
        for(String name : allContacts.keySet()){
            FirebaseMessaging.getInstance().unsubscribeFromTopic("user_" + name.replaceAll(" ", ""));
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic("user_"+ currentProfile.name.replaceAll(" ", ""));
        super.onBackPressed();
    }

//    public User getUser(String memberName) {
//        for(User user: allContacts){
//            if(user.userId.equals(memberName)){
//                return user;
//            }
//        }
//        return null;
//    }

    //    @Override
//    protected void onStop() {
//        super.onStop();
//        startService(new Intent(this, NotificationService.class));
//    }
}



















//