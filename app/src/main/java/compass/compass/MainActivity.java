package compass.compass;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import compass.compass.fragments.Call911MenuItemFragment;
import compass.compass.fragments.ContactFragment;
import compass.compass.fragments.Message911MenuItemFragment;
import compass.compass.fragments.NeedHelpSwipe;
import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;


@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class MainActivity extends AppCompatActivity implements Call911MenuItemFragment.Call911FragmentListener, Message911MenuItemFragment.Message911FragmentListener{
//    public ImageButton location;
//    public ImageButton drink;
//    public ImageButton needhelp;
    public CardView cvFindFriends;
    public CardView cvGetHelp;
    public CardView cvDrinkCounter;
    public TextView tvNameBox;
    public ImageView ivProfileBox;
    public CircleImageView ivProfileImage;
    public TextView tvPeopleNeedHelp;
    public  TextView tvContext;
    public LinearLayout linearLayout;
    public HorizontalScrollView horizontalScrollView;
    public static User currentProfile;
    public static HashMap<String, User> allContacts;
    public FloatingActionButton fabResources;
    public HashMap<String, User> needHelpFriends;
    public static HashMap<String, String> peopleInEvents;

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
            needHelpFriends = new HashMap();
            setContentView(R.layout.activity_main_android_style);
            setHomeScreenButtons();
            setOnClickListeners();
            loadPeopleInEvents();

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
        getIntent().setAction("Already created");

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
    protected void onRestart() {
        super.onRestart();
        recreate();
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
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == CALL_ACTIVITY_CODE){
//            Intent i = new Intent(this, NeedHelpActivity.class);
//            startActivity(i);
//        }
//    }


    private void setHomeScreenButtons() {
//        location = (ImageButton) findViewById(R.id.location);
//        drink = (ImageButton) findViewById(R.id.drink);
//        needhelp = (ImageButton) findVie
        mDatabase = FirebaseDatabase.getInstance().getReference();
        tvNameBox = (TextView) findViewById(R.id.tvNameBox);
        String name = WordUtils.capitalize(currentProfile.userId);
        tvNameBox.setText(name);

        fabResources = (FloatingActionButton) findViewById(R.id.fabResources);
        fabResources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchResources();
            }
        });
        cvFindFriends = (CardView) findViewById(R.id.cvFindFriends);
        cvGetHelp = (CardView) findViewById(R.id.cvGetHelp);
        cvDrinkCounter = (CardView) findViewById(R.id.cvDrinkCounter);
        ivProfileBox = (ImageView) findViewById(R.id.ivProfileBox);
        ivProfileImage = (CircleImageView) findViewById(R.id.ivProfileImageMain);
        ivProfileImage.setImageResource(getResources().getIdentifier(currentProfile.userId.replaceAll(" ",""), "drawable", getPackageName()));
        linearLayout = (LinearLayout) findViewById(R.id.linlayoutfriends);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontalScroll);
        tvPeopleNeedHelp = (TextView) findViewById(R.id.tvContext);

//        ImageView img1 = new ImageView(this);
//        img1.setImageResource(R.drawable.ic_person);
//        linearLayout.addView(img1);
//        ImageView img2 = new ImageView(this);
//        img2.setImageResource(R.drawable.ic_person);
//        linearLayout.addView(img2);

        View.OnClickListener onClickListenerProf = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchProfile();
            }
        };

        ivProfileImage.setOnClickListener(onClickListenerProf);
        tvNameBox.setOnClickListener(onClickListenerProf);

        if(currentProfile.status){
            ivProfileBox.setImageResource(R.color.colorSecondaryLight);
            ivProfileImage.setBorderColorResource(R.color.colorPrimaryLight);
        }
        else {
            ivProfileBox.setImageResource(R.color.colorPrimaryLight);
            ivProfileImage.setBorderColorResource(R.color.colorSecondaryLight);
        }

        mDatabase.child("User Status").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String userName = dataSnapshot.getKey();
                if(!userName.equals(currentProfile.userId)){
                    Object value =  dataSnapshot.getValue();
                    if(value.equals("help")){
                        needHelpFriends.put(userName, allContacts.get(userName));
                        populateNeedHelp();
                    }
                    else{
                        needHelpFriends.remove(userName);
                        populateNeedHelp();
                    }
                }

                long numberNeedHelp =  needHelpFriends.size();
                if (numberNeedHelp > 0){
                    tvPeopleNeedHelp.setTextColor(Color.RED);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String userName = dataSnapshot.getKey();
                if(!userName.equals(currentProfile.userId)){
                    Object value =  dataSnapshot.getValue();
                    if(value.equals("help")){
                        needHelpFriends.put(userName, allContacts.get(userName));
                        populateNeedHelp();
                    }
                    else {
                        needHelpFriends.remove(userName);
                        populateNeedHelp();
                    }

                }
                long numberNeedHelp =  needHelpFriends.size();
                if (numberNeedHelp > 0){
                    tvContext.setTextColor(Color.RED);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
        MenuItem call911 = (MenuItem) menu.findItem(R.id.miCall911);

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
                FirebaseDatabase.getInstance().getReference().child("User Status").child(currentProfile.userId).setValue("safe");
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
    public void launchProfile() {
        Intent i = new Intent(MainActivity.this, ProfileActivity.class);
        startActivity(i);
    }

    //launch the resources activity
    public void launchResources() {
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

    public void message911(final MenuItem menuItem){
        FragmentManager fm = getSupportFragmentManager();
        Message911MenuItemFragment message911MenuItemFragment = Message911MenuItemFragment.newInstance(this);
        message911MenuItemFragment.show(fm, "tag");
    }

    public void call911(final MenuItem menuItem) {
        FragmentManager fm = getSupportFragmentManager();
        Call911MenuItemFragment call911MenuItemFragment = Call911MenuItemFragment.newInstance(this);
        call911MenuItemFragment.show(fm, "TAG");
    }

    //    @Override
//    protected void onStop() {
//        super.onStop();
//        startService(new Intent(this, NotificationService.class));
//    }

    public void populateNeedHelp() {

        int i = 0;
        linearLayout.removeAllViews();

        if(needHelpFriends.size() == 0){
            horizontalScrollView.setVisibility(View.GONE);
            tvPeopleNeedHelp.setText("No friends need help");
            tvPeopleNeedHelp.setTextColor(getResources().getColor(R.color.Black, null));

            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) cvFindFriends.getLayoutParams();
            params.height = px;
            cvFindFriends.setLayoutParams(params);

            params = (ConstraintLayout.LayoutParams) cvGetHelp.getLayoutParams();
            params.height = px;
            cvGetHelp.setLayoutParams(params);

            params = (ConstraintLayout.LayoutParams) cvDrinkCounter.getLayoutParams();
            params.height = px;
            cvDrinkCounter.setLayoutParams(params);
        }
        else{
            horizontalScrollView.setVisibility(View.VISIBLE);
            tvPeopleNeedHelp.setText("Friends in Need:");

            tvPeopleNeedHelp.setTextColor(getResources().getColor(R.color.Black, null));

            Resources r = getResources();
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, r.getDisplayMetrics());

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) cvFindFriends.getLayoutParams();
            params.height = px;
            cvFindFriends.setLayoutParams(params);

            params = (ConstraintLayout.LayoutParams) cvGetHelp.getLayoutParams();
            params.height = px;
            cvGetHelp.setLayoutParams(params);

            params = (ConstraintLayout.LayoutParams) cvDrinkCounter.getLayoutParams();
            params.height = px;
            cvDrinkCounter.setLayoutParams(params);
        }

        for(final User user : needHelpFriends.values()) {
            /*---------------Creating frame layout----------------------*/

            FrameLayout frameLayout = new FrameLayout(MainActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, getPixelsToDP(50));
            layoutParams.rightMargin = getPixelsToDP(10);
            frameLayout.setLayoutParams(layoutParams);

            /*--------------end of frame layout----------------------------*/

            /*---------------Creating image view----------------------*/
            final CircleImageView imgView = new CircleImageView(MainActivity.this); //create imageview dynamically
            LinearLayout.LayoutParams lpImage = new LinearLayout.LayoutParams(130, 130);
            imgView.setImageResource(getResources().getIdentifier(user.userId.replaceAll(" ",""), "drawable", getPackageName()));
            imgView.setLayoutParams(lpImage);
            // setting ID to retrieve at later time (same as its position)
            imgView.setId(i);
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    ContactFragment contactFragment = ContactFragment.newInstance(user.name);
                    contactFragment.show(fm, "idk_what_goes_here");
                }
            });
            /*--------------end of image view----------------------------*/

            /*---------------Creating Text view----------------------*/
//            TextView textView = new TextView(MainActivity.this);//create textview dynamically
//            textView.setText(user.name);
//            FrameLayout.LayoutParams lpText = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER);
//            // Note: LinearLayout.LayoutParams 's gravity was not working so I putted Framelayout as 3 paramater is gravity itself
//            textView.setTextColor(getResources().getColor(R.color.Black, null));
//            textView.setLayoutParams(lpText);
            /*--------------end of Text view----------------------------*/

            //Adding views at appropriate places
            frameLayout.addView(imgView);
//            frameLayout.addView(textView);
            linearLayout.addView(frameLayout);

            i++;

//            TextView tv = new TextView(getApplicationContext());
//            tv.setText(user.name);
//            linearLayout.addView(tv);

        }

    }

    private int getPixelsToDP(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }

    @Override
    public void launchNeedHelpFragment() {
        mDatabase.child("User Status").child(currentProfile.userId).setValue("help");
        mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
        currentProfile.status = true;
        Intent i = new Intent(this, NeedHelpActivity.class);
        i.putExtra("launchHelp", true);
        startActivity(i);
    }

    @Override
    public void launchNeedHelpFromMessage() {
        mDatabase.child("User Status").child(currentProfile.userId).setValue("help");
        mDatabase.child("Users").child(currentProfile.userId).child("need help").setValue(true);
        currentProfile.status = true;
        //TODO:THIS IS WHERE WE COULD THEORETICALLY ASK IF THEY ACTUALLY WANT TO
        Intent i = new Intent(this, NeedHelpActivity.class);
        startActivity(i);
    }

    public void loadPeopleInEvents(){
        peopleInEvents= new HashMap<String, String>();

        mDatabase.child("Users").child(currentProfile.userId).child("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map events = (Map) dataSnapshot.getValue();
                for (Object e : events.keySet()){
                    final String tempEvent = e.toString();
                    mDatabase.child("Events").child(tempEvent).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Map otherPeople = (Map) dataSnapshot.getValue();
                            for(Object s : otherPeople.keySet()) {
                                String temp = s.toString().replaceAll(" ", "");
                                if((otherPeople.get(s).toString().contentEquals("out")) || (otherPeople.get(s).toString().contentEquals("on call"))){
                                    peopleInEvents.put(temp, tempEvent);
                                }
                            }
                            peopleInEvents.remove(currentProfile.name.replaceAll(" ", ""));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}



















//