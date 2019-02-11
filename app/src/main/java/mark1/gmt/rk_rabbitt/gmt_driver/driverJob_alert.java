package mark1.gmt.rk_rabbitt.gmt_driver;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import mark1.gmt.rk_rabbitt.gmt_driver.DBHelper.dbHelper;

import static mark1.gmt.rk_rabbitt.gmt_driver.Preferences.prefsManager.ID_KEY;
import static mark1.gmt.rk_rabbitt.gmt_driver.Preferences.prefsManager.TYPE;
import static mark1.gmt.rk_rabbitt.gmt_driver.Preferences.prefsManager.USER_PREFS;

public class driverJob_alert extends AppCompatActivity {

    //Global declaration
    dbHelper dbHelpar;
    Ringtone ringtone;
    SharedPreferences shrp;

    String book_id;
    String type;
    String vehicle;
    String pickup;
    String drop;
    String time;
    String package_type;
    String ori_lat;
    String ori_lng;
    String dest_lat;
    String dest_lng;

    TextView book_idTxt, typeTxt, vehicleTxt, package_idTxt, pickupTxt, dropTxt, timeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_job_alert);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Textview initialization
        book_idTxt = findViewById(R.id.book_id);
        typeTxt = findViewById(R.id.type);
        vehicleTxt = findViewById(R.id.vehcile);
        package_idTxt = findViewById(R.id.package_id);
        pickupTxt = findViewById(R.id.pickup);
        dropTxt = findViewById(R.id.drop);
        timeTxt = findViewById(R.id.timeat);

        //getting values from the notification
//        Intent intent = getIntent();
//        book_id = intent.getStringExtra(BOOK_ID);
//        type = intent.getStringExtra(TYPE);
//        vehicle = intent.getStringExtra("VEHICLE");
//        pickup = intent.getStringExtra("PICKUP");
//        drop = intent.getStringExtra("DROP");
//        time = intent.getStringExtra("TIME");
//        package_type = intent.getStringExtra("PACKAGE");
//        ori_lat = intent.getStringExtra("ORI_LAT");
//        ori_lng = intent.getStringExtra("ORI_LNG");
//        dest_lat = intent.getStringExtra("DEST_LAT");
//        dest_lng = intent.getStringExtra("DEST_LNG");

        shrp = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        book_id = shrp.getString(ID_KEY, "0");
        type = shrp.getString(TYPE, "0");


        //setting the info to the job alert page
        book_idTxt.setText(book_id);
        typeTxt.setText(type);
        vehicleTxt.setText(vehicle);
        pickupTxt.setText(pickup);
        dropTxt.setText(drop);
        timeTxt.setText(time);
        package_idTxt.setText(package_type);

        Log.i("logs", book_id+" "+type+" "+vehicle+" "+pickup+" "+drop+" "+time);




        //ringing alarm to alert the driver
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
//        alamNotificationBuilder.setContentIntent(contentIntent);
//        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
//        Log.d("AlarmService", "Notification sent.");
    }

    public void stopAlarm(View view) {
        ringtone.stop();
        finish();
    }

    public void gotoNavigation(View view) {
        ringtone.stop();

        dbHelpar.insertdata(book_id, time, type, vehicle, pickup, drop, package_type);
        Intent intent = new Intent(this, MapsActivity.class);

        startActivity(intent);

    }
}
