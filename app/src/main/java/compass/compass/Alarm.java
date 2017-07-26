package compass.compass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import compass.compass.models.ChatMessage;

/**
 * Created by icamargo on 7/25/17.
 */

public class Alarm extends BroadcastReceiver {
    private DatabaseReference mDatabase;

    @Override
    public void onReceive(Context context, Intent intent) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String eventName = intent.getStringExtra("alarmName");
        Toast.makeText(context, "alarm went off for " + eventName, Toast.LENGTH_LONG).show();

        String[] user = {MainActivity.currentProfile.userId};

        ChatMessage message = new ChatMessage("Alarm Ringing!!", "myself");
        ArrayList<String> recipients= new ArrayList<String>(Arrays.asList(user));
        Map notification = new HashMap<>();
        notification.put("recipients", recipients);
        notification.put("message", message.getSender().replaceAll(" ", "") + ":" + " anything " + ":" + message.getText());
        mDatabase.child("notifications").push().setValue(notification);

        Uri notificationRing = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notificationRing);
        r.play();

        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);

    }
}
