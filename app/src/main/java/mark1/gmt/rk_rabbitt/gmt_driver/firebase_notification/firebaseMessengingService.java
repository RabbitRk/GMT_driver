package mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import mark1.gmt.rk_rabbitt.gmt_driver.MainActivity;
import mark1.gmt.rk_rabbitt.gmt_driver.MapsActivity;
import mark1.gmt.rk_rabbitt.gmt_driver.R;
import mark1.gmt.rk_rabbitt.gmt_driver.driverJob_alert;

/**
 * Created by Rabbitt on 30,January,2019
 */
public class firebaseMessengingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.i("remote", "Data Payload: " + remoteMessage.getData().toString());
            try {

                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
                //Log.i("json", json.toString());
            } catch (Exception e) {
                Log.e("remote", "Exception: " + e.getMessage());
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.i("remote", "Notification JSON " + json.toString());

        Intent i=new Intent(this,driverJob_alert.class);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = data.getString("title");
            String message = data.getString("body");
            Log.i("remote", "title..." + title);
            Log.i("remote", "body1..." + message);

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            // assuming your main activity
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            notificationBuilder.setAutoCancel(true)
/*                    .setDefaults(Notification.CATEGORY_CALL)*/
                    .setCategory(Notification.CATEGORY_CALL)
                    .setOngoing(true)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_bell)
                    .setTicker("Hearty365")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentTitle("Default notification")
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                    .setFullScreenIntent(pendingIntent,true)
                    .setVisibility(1)
                    .setContentInfo("Info");

            notificationManager.notify(/*notification id*/1, notificationBuilder.build());


        } catch (JSONException e) {
            Log.e("remote", "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e("remote", "Exception: " + e.getMessage());
        }
    }

}
