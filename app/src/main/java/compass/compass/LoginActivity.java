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

import java.util.ArrayList;
import java.util.Map;

import compass.compass.models.User;

/**
 * Created by icamargo on 7/14/17.
 */

public class LoginActivity extends AppCompatActivity{
    private EditText etName;
    private Button btLogin;
    private ArrayList<User> contacts;
    public DatabaseReference mDatabase;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //make users out of all items in the Users child in the database
        loadUsers();

//        Intent i = getIntent();
//        Bundle bundle = i.getExtras();
        //contacts = (ArrayList) bundle.get("usernames");
        contacts = new ArrayList<>();
        etName = (EditText) findViewById(R.id.etName);
        btLogin = (Button) findViewById(R.id.btLogin);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                User user = checkForUser(name);
                if(user == null){
                    Toast.makeText(getApplicationContext(), "user not found in database", Toast.LENGTH_LONG).show();
                }else{
                    MainActivity.currentProfile = user;
                    setAllContacts();
                    MainActivity.allContacts.remove(user);
                }
                Intent i  = new Intent(getBaseContext(), MainActivity.class);
                i.putExtra("userToCheck", name);
                startActivity(i);
                //setMainUser(name);
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
        for(User x : contacts){
            if(x.userId.equals(name)){
                return x;
            }
        }
        return null;
    }
//    private void setMainUser(String s){
//        if(MainActivity.checkDBForUser(s)){
//            //move on to next intent
//        }else{
//            //do something to tell the people they didnt pick a good user
//        }
//    }
}
