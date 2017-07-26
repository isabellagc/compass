package compass.compass;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import compass.compass.models.User;
import de.hdodenhof.circleimageview.CircleImageView;

import static compass.compass.MainActivity.currentProfile;


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
        tvAllergyInfo.setText(currentProfile.healthInfo.get(User.KEY_ALLERGIES_INFO));
        tvAsthmaInfo.setText(currentProfile.healthInfo.get(User.KEY_ASTHMA_INFO));
        tvDiabetesInfo.setText(currentProfile.healthInfo.get(User.KEY_DIABETES_INFO));
        tvOtherInfo.setText(currentProfile.healthInfo.get(User.KEY_OTHER_INFO));

        emergencyContactsAdapter = new EmergencyContactsAdapter(this);
        rvEmergencyContacts.setAdapter(emergencyContactsAdapter);
        rvEmergencyContacts.setLayoutManager(new LinearLayoutManager(this));
        rvEmergencyContacts.invalidate();

//        rvGroupsProfView.setAdapter(new EventsAdapter(this));
//        rvGroupsProfView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
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
}
