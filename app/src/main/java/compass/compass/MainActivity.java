package compass.compass;

import android.content.Intent;
import android.database.SQLException;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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



@RequiresApi(api = Build.VERSION_CODES.N_MR1)
public class MainActivity extends AppCompatActivity{
    public ImageButton location;
    public ImageButton drink;
    public ImageButton needhelp;
    //DataBaseManager myDb;
    public DataBaseHelper myDbHelper;
    public static ArrayList<User> contacts;


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

//    //launch the app shortcut
//    private void shortcutlauncher(){
//        ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
//                //getSystemService(ShortcutManager.class);`
//        Intent mintent = new Intent(this, DrinkActivity.class);
//        mintent.setAction(Intent.ACTION_VIEW);
//
//        ShortcutInfo mShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_1")
//                .setShortLabel("Drink Counter")
//                .setLongLabel("Drink Counter")
//                .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
//                .setIntent(mintent)
//                .build();
//        mShortcutManager.setDynamicShortcuts(Arrays.asList(mShortcutInfo));
//
//    }
//    private void shortcutlauncher() {
//        ShortcutManager mShortcutManager = getSystemService(ShortcutManager.class);
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (!Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                        Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, 1234);
//
//                ShortcutInfo mShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_1")
//                        .setShortLabel("Drink Counter")
//                        .setLongLabel("Drink Counter")
//                        .setIcon(Icon.createWithResource(this, R.mipmap.ic_launcher))
//                        .setIntent(intent)
//                        .build();
//                mShortcutManager.setDynamicShortcuts(Arrays.asList(mShortcutInfo));
//
//
//            }
//        } else {
//            Intent mintent = new Intent(this, DrinkActivity.class);
//            startService(mintent);
//        }
//    }

//    private void registerToken(String token){
//        OkHttpClient client = new OkHttpClient();
//        RequestBody body = new FormBody.Builder()
//                .add("token", token)
//                .build();
//
//        Request request = new Request.Builder()
//                .url("url")
//                .post(body)
//                .build();
//        try {
//            client.newCall(request).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
