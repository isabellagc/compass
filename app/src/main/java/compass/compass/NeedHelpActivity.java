package compass.compass;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.SeekBar;

/**
 * Created by brucegatete on 7/11/17.
 */

public class NeedHelpActivity extends AppCompatActivity {

    SeekBar level;
    ImageButton alc;
    ImageButton home;
    ImageButton sa;
    ImageButton other;
    SwipeButton callPolice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_help);

        level = (SeekBar) findViewById(R.id.level);
        alc = (ImageButton) findViewById(R.id.ibAlc);
        home = (ImageButton) findViewById(R.id.ibHome);
        sa = (ImageButton) findViewById(R.id.ibSA);
        other = (ImageButton) findViewById(R.id.ibOther);
        callPolice = (SwipeButton) findViewById(R.id.callPolice);

        SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                onSwiped();
            }
        };

        if (callPolice != null) {
            callPolice.setSwipeButtonCustomItems(swipeButtonSettings);
        }
    }

    private void onSwiped() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:7325168820"));

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("YIKES", "lol u dont have permission to call");
            return;
        }

        startActivity(callIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
