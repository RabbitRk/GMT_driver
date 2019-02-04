package mark1.gmt.rk_rabbitt.gmt_driver;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class driverJob_alert extends AppCompatActivity {
    Ringtone ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_job_alert);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(this, alarmUri);
        ringtone.play();
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, AlarmActivity.class), 0);
//
//        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
//                this).setContentTitle("Alarm").setSmallIcon(R.drawable.ic_launcher)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
//                .setContentText(msg);
//
//
//        alamNotificationBuilder.setContentIntent(contentIntent);
//        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
//        Log.d("AlarmService", "Notification sent.");
    }

    public void stopAlarm(View view) {
        ringtone.stop();
    }
}
