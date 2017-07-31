package compass.compass;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class LaunchFragmentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_launch_fragment);
        String message = getIntent().getStringExtra("message");
        final String eventId = getIntent().getStringExtra("eventId");

        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog).create();
        alertDialog.setTitle("Need Help");
        alertDialog.setMessage(message);
        alertDialog.setIcon(R.drawable.ic_need_help);

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        };

        DialogInterface.OnClickListener goToEvent = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(LaunchFragmentActivity.this, ChatActivity.class);
                intent.putExtra("eventId", eventId);
                intent.putExtra("fromHere", "eventActivity");
                startActivity(intent);

                alertDialog.dismiss();
            }
        };

        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "OK", okListener);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Go to a common event", goToEvent);

        alertDialog.show();


//
//        FragmentManager fm = getSupportFragmentManager();
//        FriendNeedsHelpFragment friendNeedsHelpFragment = FriendNeedsHelpFragment.newInstance(message);
//        friendNeedsHelpFragment.show(fm, "idk_what_goes_here");
    }
}
