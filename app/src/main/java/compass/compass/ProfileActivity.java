package compass.compass;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import compass.compass.fragments.Call911MenuItemFragment;
import compass.compass.fragments.Message911MenuItemFragment;
import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.currentProfile;
import static compass.compass.fragments.Call911MenuItemFragment.CALL_ACTIVITY_CODE;


public class ProfileActivity extends AppCompatActivity {
    TextView tvName, tvSchool;
    ImageView ivProfileImage;
//    RecyclerView rvGroupsProfView;
    TextView tvAddress, tvAllergyInfo, tvAsthmaInfo, tvDiabetesInfo, tvOtherInfo;
    RecyclerView rvEmergencyContacts;
    EmergencyContactsAdapter emergencyContactsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(currentProfile.status){
            getTheme().applyStyle(R.style.AppThemeInverted, true);
        }
        else{
            getTheme().applyStyle(R.style.AppTheme, true);
        }

        setContentView(R.layout.activity_profile);

        tvName = (TextView) findViewById(R.id.tvName);
        tvSchool = (TextView) findViewById(R.id.tvSchool);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvAllergyInfo = (TextView) findViewById(R.id.tvAllergyInfo);
        tvAsthmaInfo = (TextView) findViewById(R.id.tvAsthmaInfo);
        tvDiabetesInfo = (TextView) findViewById(R.id.tvDiabetesInfo);
        tvOtherInfo = (TextView) findViewById(R.id.tvOtherInfo);
        rvEmergencyContacts = (RecyclerView) findViewById(R.id.rvEmergencyContacts);
//        rvGroupsProfView =  (RecyclerView) findViewById(rvGroupsProfView);

        ivProfileImage = (CircleImageView) findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageResource(getResources().getIdentifier(currentProfile.userId.replaceAll(" ",""), "drawable", getPackageName()));
        tvName.setText(capitalize(currentProfile.name));
        tvSchool.setText(capitalize(currentProfile.school));
        tvAddress.setText(currentProfile.address);
        tvAllergyInfo.setText(currentProfile.healthInfo.get(User.KEY_ALLERGIES_INFO));
        tvAsthmaInfo.setText(currentProfile.healthInfo.get(User.KEY_ASTHMA_INFO));
        tvDiabetesInfo.setText(currentProfile.healthInfo.get(User.KEY_DIABETES_INFO));
        tvOtherInfo.setText(currentProfile.healthInfo.get(User.KEY_OTHER_INFO));

        emergencyContactsAdapter = new EmergencyContactsAdapter(this);
        rvEmergencyContacts.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        rvEmergencyContacts.setAdapter(emergencyContactsAdapter);
        rvEmergencyContacts.setLayoutManager(new LinearLayoutManager(this));
        //rvEmergencyContacts.invalidate();

//        rvGroupsProfView.setAdapter(new EventsAdapter(this));
//        rvGroupsProfView.setLayoutManager(new LinearLayoutManager(this));
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

    public static String capitalize(String str) {
        return capitalize(str, null);
    }

    public static String capitalize(String str, char[] delimiters) {
        int delimLen = (delimiters == null ? -1 : delimiters.length);
        if (str == null || str.length() == 0 || delimLen == 0) {
            return str;
        }
        int strLen = str.length();
        StringBuffer buffer = new StringBuffer(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);

            if (isDelimiter(ch, delimiters)) {
                buffer.append(ch);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer.append(Character.toTitleCase(ch));
                capitalizeNext = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }
    private static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (int i = 0, isize = delimiters.length; i < isize; i++) {
            if (ch == delimiters[i]) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onRestart() {
        recreate();
        super.onRestart();
    }

    public void message911(final MenuItem menuItem){
        FragmentManager fm = getSupportFragmentManager();
        Message911MenuItemFragment message911MenuItemFragment = Message911MenuItemFragment.newInstance();
        message911MenuItemFragment.show(fm, "tag");
    }

    public void call911(final MenuItem menuItem) {
        FragmentManager fm = getSupportFragmentManager();
        Call911MenuItemFragment call911MenuItemFragment = Call911MenuItemFragment.newInstance();
        call911MenuItemFragment.show(fm, "TAG");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == CALL_ACTIVITY_CODE){
            Intent i = new Intent(this, NeedHelpActivity.class);
            startActivity(i);
        }
    }

}
