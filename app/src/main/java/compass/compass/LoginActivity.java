package compass.compass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import compass.compass.models.User;

import static compass.compass.MainActivity.allContacts;
import static compass.compass.MainActivity.currentProfile;

/**
 * Created by icamargo on 7/14/17.
 */

public class LoginActivity extends AppCompatActivity{
    private EditText etName;
    private Button btLogin;
    private ArrayList<User> contacts;
    public DatabaseReference mDatabase;
    private User user;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //make users out of all items in the Users child in the database
        contacts = new ArrayList<>();
        loadUsers();

        etName = (EditText) findViewById(R.id.etName);
        btLogin = (Button) findViewById(R.id.btLogin);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                user = checkForUser(name);
                if(user == null){
                    etName.clearComposingText();
                    Toast.makeText(getApplicationContext(), "That user was not found in the database!", Toast.LENGTH_LONG).show();
                }else{
                    currentProfile = user;
                    //setUserEvents();
                    //setAlarms();

                    //TODO: UNCOMMENT IF WE WANT ALARM USEAGE
                    //initAlarms();
                    setAllContacts();
                    FirebaseMessaging.getInstance().subscribeToTopic("user_"+ currentProfile.name.replaceAll(" ", ""));
                    allContacts.remove(user.userId);
//                    setDrinksToZero();
                    setDrinkListeners();
                    Intent i  = new Intent(getBaseContext(), MainActivity.class);
                    //Intent i  = new Intent(getBaseContext(), testing_layouts.class);
                    i.putExtra("userToCheck", name);
                    startActivity(i);
                }
            }
        };

        btLogin.setOnClickListener(listener);
    }

    private void setDrinkListeners() {

        mDatabase.child("Drinks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Map<String, Object> newInfo = (Map) ds.getValue();
                    Object BAC = newInfo.get("BAC");
                    Object drinkCount = newInfo.get("drink count");
                    Object key = ds.getKey();
                    if(allContacts.containsKey(key.toString())){
                        User user = allContacts.get(key.toString());
                        //find and cast BAC
                        if(BAC instanceof Integer){
                            Integer temp = ((Integer) BAC).intValue();
                            user.currentBAC = temp.doubleValue();
                        }else if (BAC instanceof Double){
                            user.currentBAC = (double) BAC;
                        }else if (BAC instanceof Long){
                            Long x = (Long) BAC;
                            user.currentBAC = x.doubleValue();
                        }
                        //find and cast drink count
                        if(drinkCount instanceof Integer){
                            user.drinkCounter = (int) drinkCount;
                        }else if (drinkCount instanceof Double){
                            Double d = ((Double) drinkCount).doubleValue();
                            user.drinkCounter = d.intValue();
                        }else if (drinkCount instanceof Long){
                            Long x = (Long) drinkCount;
                            user.drinkCounter = x.intValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        mDatabase.child("Drinks").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Map<String, Object> newInfo = (Map) dataSnapshot.getValue();
//                Object BAC = newInfo.get("BAC");
//                Object drinkCount = newInfo.get("drink count");
//                Object key = dataSnapshot.getKey();
//                if(allContacts.containsKey(key.toString())){
//                    User user = allContacts.get(key.toString());
//                    Long l = (Long) drinkCount;
//                    user.drinkCounter = l.intValue();
//                    Long x = (Long) BAC;
//                    user.currentBAC = x.doubleValue();
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Map<String, Object> newInfo = (Map) dataSnapshot.getValue();
//                Object BAC = newInfo.get("BAC");
//                Object drinkCount = newInfo.get("drink count");
//                Object key = dataSnapshot.getKey();
//                if(allContacts.containsKey(key.toString())){
//                    User user = allContacts.get(key.toString());
//                    Long l = (Long) drinkCount;
//                    user.drinkCounter = l.intValue();
//                    Long x = (Long) BAC;
//                    user.currentBAC = x.doubleValue();
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    }
    public void setAllContacts() {
        allContacts = new HashMap<>();
        for(User x : contacts) {
            allContacts.put(x.userId, x);
        }

    }


//    private void setAlarms(){
//        HashMap<String, Long> alarmsFromDB = new HashMap<>();
//        mDatabase.child("Users").child(currentProfile.userId).child("alarms").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    Object value = ds.getValue();
//                    Object key = ds.getKey();
//
//                    if(value.toString().equals("null")){
//                        currentProfile.alarms.put(key.toString(), User.KEY_NULL_VALUE);
//                    }else{
//                        Long longVal = Long.valueOf(value.toString());
//                        currentProfile.alarms.put(key.toString(), longVal);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void initAlarms(){
//        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
//        Set<String> keys = currentProfile.alarms.keySet();
//        for(String key: keys){
//            if(currentProfile.alarms.get(key).longValue() != User.KEY_NULL_VALUE){
//                Intent intent = new Intent(this, Alarm.class);
//                intent.putExtra("alarmName", key);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
//                alarmManager.set(AlarmManager.RTC_WAKEUP, currentProfile.alarms.get(key), pendingIntent);
//            }
//        }
//    }

    private void initAlarms(){
        mDatabase.child("Users").child(currentProfile.userId).child("alarms").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                Object value = dataSnapshot.getValue();
                Object key = dataSnapshot.getKey();

                if(value.toString().equals("null")){
                    currentProfile.alarms.put(key.toString(), User.KEY_NULL_VALUE);
                }else{
                    Long longVal = Long.valueOf(value.toString());
                    currentProfile.alarms.put(key.toString(), longVal);
                    Intent intent = new Intent(getApplicationContext(), Alarm.class);
                    intent.putExtra("alarmName", key.toString());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, currentProfile.alarms.get(key), pendingIntent);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
                Object value = dataSnapshot.getValue();
                Object key = dataSnapshot.getKey();

                if(value.toString().equals("null")){
                    currentProfile.alarms.put(key.toString(), User.KEY_NULL_VALUE);
                }else{
                    Long longVal = Long.valueOf(value.toString());
                    currentProfile.alarms.put(key.toString(), longVal);
                    Intent intent = new Intent(getApplicationContext(), Alarm.class);
                    intent.putExtra("alarmName", key.toString());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, currentProfile.alarms.get(key), pendingIntent);
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

//    private void setUserEvents(){
//        mDatabase.child("Users").child(currentProfile.userId).child("events").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void setDrinksToZero(){
//        HashMap<String, Object> drinkMap = new HashMap<>();
//        drinkMap.put("drinkCounter", 0);
//        mDatabase.child("Users").child(user.userId).updateChildren(drinkMap);
//    }

    private void loadUsers(){
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Map userData = (Map) ds.getValue();
                    User user = new User();
                    user.userId = (String) ds.getKey();
                    user.name = (String) userData.get("name");
                    user.email = (String) userData.get("email");
                    user.gender = (String) userData.get("gender");
                    user.school = (String) userData.get("school");
                    user.weight = ((Long)userData.get("weight")).intValue();
                    user.status = ((boolean) userData.get("need help"));
                    user.address = (String ) userData.get("address");
                    user.phoneNumber = Long.toString((Long) userData.get("phone"));
                    Map userHealthData = (Map) userData.get("health info");
                    String allergyInfo = (String) userHealthData.get(User.KEY_ALLERGIES_INFO);
                    String diabeticInfo = (String) userHealthData.get(User.KEY_DIABETES_INFO);
                    String asthmaInfo = (String) userHealthData.get(User.KEY_ASTHMA_INFO);
                    String otherInfo = (String) userHealthData.get(User.KEY_OTHER_INFO);
                    HashMap<String, String> healthInfo = new HashMap<>();
                    healthInfo.put(User.KEY_ASTHMA_INFO, asthmaInfo);
                    healthInfo.put(User.KEY_ALLERGIES_INFO, allergyInfo);
                    healthInfo.put(User.KEY_DIABETES_INFO, diabeticInfo);
                    healthInfo.put(User.KEY_OTHER_INFO, otherInfo);
                    user.healthInfo = healthInfo;

                    contacts.add(user);
                    //SET THE DRINKS ON DB TO 0: WE ARE ALL STARTING WITH 0 DRINKS WHEN WE LOGIN TO APP
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        mDatabase.child("Users").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Map userData = (Map) dataSnapshot.getValue();
//                User user = new User();
//                user.name = (String) userData.get("name");
//                user.email = (String) userData.get("email");
//                user.gender = (String) userData.get("gender");
//                contacts.add(user);
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Log.d("wow", "here");
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d("wow", "here");
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//                Log.d("wow", "here");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("wow", "here");
//            }
//        });
    }


    private User checkForUser(String name){
        name = name.trim();
        for(User x : contacts){
            if(x.name.equalsIgnoreCase(name)){
                return x;
            }
        }
        return null;
    }
}