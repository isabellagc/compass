package compass.compass;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by brucegatete on 7/20/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    NotificationManager mNotificationManager;
    String total;
    String message;
    String eventName;
    String sender;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.

        Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        total = remoteMessage.getData().toString();

        sender = total.substring(total.indexOf("=")+1,total.indexOf(":"));
        eventName = total.substring(total.indexOf(":")+1,total.lastIndexOf(":"));
        message = total.substring(total.lastIndexOf(":")+1, total.length()-1);

        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("eventId", eventName);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(sender.contentEquals("BOT")){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_need_help)
//                    .setContentTitle("New Message from " + sender + " to " + eventName)
                    .setContentTitle(message)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSound(notificationSound)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);
            mBuilder.setLocalOnly(false);

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());

            openFragment();
        }
        else{
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_need_help)
                    .setContentTitle("New Message from " + sender + " to " + eventName)
                    .setContentText(message)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSound(notificationSound)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
            mBuilder.setAutoCancel(true);
            mBuilder.setLocalOnly(false);

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }

    }

    public void openFragment(){
        Intent i = new Intent(this, LaunchFragmentActivity.class);
        i.putExtra("message", message);
        i.putExtra("eventId", eventName);
        startActivity(i);


    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }
}
