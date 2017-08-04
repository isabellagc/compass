package compass.compass;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.john.waveview.WaveView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import compass.compass.fragments.Call911MenuItemFragment;
import compass.compass.fragments.Message911MenuItemFragment;
import compass.compass.models.ChatMessage;

import static compass.compass.MainActivity.currentProfile;
import static compass.compass.MainActivity.peopleInEvents;

/**
 * Created by brucegatete on 7/11/17.
 */

public class DrinkActivityReal extends AppCompatActivity implements Call911MenuItemFragment.Call911FragmentListener, Message911MenuItemFragment.Message911FragmentListener{
    public static final int GRAMS_IN_STANDARD_DRINK = 14;
    public static final double LBS_TO_GRAMS_CONVERSION = 453.59237;
    public static final double MALE_CONSTANT = .68;
    public static final double FEMALE_CONSTANT = .55;

    //todo: decide as a group what the max bac (where the body is full) should be!
    public static final double MAX_BAC = .13;

    ImageButton btAddDrink;
    TextView tvDrinkNumber;
    ImageView ivPerson;
    //info: refers to number of STANDARD DRINKS (1.5 oz hard, 12oz beer, 5(?) oz wine, etc)
    public int drinks;

    public DatabaseReference mDatabase;
    public double BAC;
    public int weight;
    public long time_start;
    public long time_end;
    public long time_elapsed;

    //grams of alcohol consumed (standard drinks x 14)
    private double alcoholContent;
    private double percentageOfMax;
    private WaveView waveView;
    TextView tvBAC;
    int leftMargin;
    int topMargin;
    FloatingActionButton fabAddDrink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);

        if(currentProfile.status){
            getTheme().applyStyle(R.style.AppThemeInverted, true);
        }
        else{
            getTheme().applyStyle(R.style.AppTheme, true);
        }

        setContentView(R.layout.activity_drink_real);
        tvDrinkNumber = (TextView) findViewById(R.id.tvDrinkNumber);
        fabAddDrink = (FloatingActionButton) findViewById(R.id.fabAddDrink);
        tvBAC = (TextView) findViewById(R.id.tvBAC);
        drinks = currentProfile.drinkCounter;
        tvDrinkNumber.setText(String.valueOf(drinks));

        FrameLayout.MarginLayoutParams mLayout = (FrameLayout.MarginLayoutParams) tvBAC.getLayoutParams();
        leftMargin = mLayout.leftMargin;
        topMargin = mLayout.topMargin - 90;

        weight = currentProfile.weight;

        waveView = (WaveView) findViewById(R.id.wave_view);
        ivPerson = (ImageView) findViewById(R.id.ivPerson);
        if(currentProfile.gender.equals("f")){
            ivPerson.setImageResource(R.drawable.ic_female);
        }else{
            ivPerson.setImageResource(R.drawable.ic_male);
        }

        setProgress();

        fabAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //increment the number of drinks
                drinks += 1;
                currentProfile.drinkCounter += 1;
                tvDrinkNumber.setText(String.valueOf(drinks));
                setProgress();
                if(BAC >= 0.08){
                    final AlertDialog alertDialog = new AlertDialog.Builder(DrinkActivityReal.this, R.style.Theme_AppCompat_Light_Dialog).create();
                    alertDialog.setTitle("BLOOD ALCOHOL CONTENT TOO HIGH");
                    alertDialog.setMessage("Your BAC is now over .08! Please slow down.");
                    alertDialog.setIcon(R.drawable.ic_need_help);
                    DialogInterface.OnClickListener okay = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    };

                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OKAY", okay);
                    alertDialog.show();
                }
                currentProfile.currentBAC = BAC;
                sendDbDrinkMessage();
            }
        });

        EventActivity.showFabWithAnimation(fabAddDrink, 50);
    }

    private void sendDbDrinkMessage(){
        Map<String, Object> info = new HashMap<>();
        info.put("drink count", currentProfile.drinkCounter);
        info.put("BAC", BAC);
        mDatabase.child("Drinks").child(currentProfile.userId).updateChildren(info);
    }

    private void setProgress(){
        BAC = calculateBAC();

        percentageOfMax = (BAC / MAX_BAC)  * 100;
        waveView.setProgress((int) percentageOfMax);

        tvBAC.setText("BAC: " + String.format("%.2f", BAC));
        FrameLayout.MarginLayoutParams mLayout = (FrameLayout.MarginLayoutParams) tvBAC.getLayoutParams();
        int top;
        if(percentageOfMax > 100){
            top = 90;
        }
        else{
            top = (int) (topMargin - (percentageOfMax * (topMargin) / 100)) + 90;
        }
        mLayout.setMargins(leftMargin, top, 0, 0);
        tvBAC.setLayoutParams(mLayout);
    }



    private double calculateBAC(){
        double total = 0.0;
        if (time_start == 0) {
            time_start = System.currentTimeMillis();
        }else{
            time_end = System.currentTimeMillis();
            time_elapsed = time_end - time_start;
        }

        alcoholContent = drinks * GRAMS_IN_STANDARD_DRINK;
        double bodyWeightInGrams = weight * LBS_TO_GRAMS_CONVERSION;
        if(currentProfile.gender.equals("f")){
            bodyWeightInGrams *= FEMALE_CONSTANT;
        }else{
            bodyWeightInGrams *= MALE_CONSTANT;
        }

        total = (alcoholContent / bodyWeightInGrams) * 100;
        //account for elapsed time
        total -= time_elapsed * 0.015/(1000 * 3600);

        return total;
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
                FirebaseDatabase.getInstance().getReference().child("User Status").child(currentProfile.userId).setValue("safe");
                currentProfile.status = false;

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setText(currentProfile.userId + " has marked themselves as safe");
                chatMessage.setSender("SAFE");
                chatMessage.setTime((new Date().getTime()));
                NeedHelpActivity.sendNotificationToUser(peopleInEvents, chatMessage, mDatabase);

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
        Intent i = new Intent(this, NeedHelpActivity.class);
        i.putExtra("launchHelp", true);
        startActivity(i);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if(resultCode == CALL_ACTIVITY_CODE){
//            Intent i = new Intent(this, NeedHelpActivity.class);
//            startActivity(i);
//        }
//    }

}























//