package compass.compass;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Map;

import compass.compass.models.User;

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
                    setAllContacts();
                    FirebaseMessaging.getInstance().subscribeToTopic("user_"+ currentProfile.name.replaceAll(" ", ""));
                    MainActivity.allContacts.remove(user);
//                    setDrinksToZero();
                    Intent i  = new Intent(getBaseContext(), MainActivity.class);
                    i.putExtra("userToCheck", name);
                    startActivity(i);
                }
            }
        };

        btLogin.setOnClickListener(listener);
    }

    public void setAllContacts() {
        MainActivity.allContacts = new ArrayList<User>();
        for(User x : contacts) {
            MainActivity.allContacts.add(x);
        }

    }

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