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

import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.DEST_LAT;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.DEST_LNG;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.DROP;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.ORI_LAT;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.ORI_LNG;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.PACKAGE;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.PICKUP;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.TIME;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.TYPE;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.BOOK_ID;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.SHARED_PREFS;
import static mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification.firebaseMessengingService.VEHICLE;

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

    public static final String oriLata = "orilat", oriLnga = "orilng";
    public static final String desLata = "deslat", desLnga = "deslng";

    public static final String typeI = "type";
    public static final String vehicleI = "vehicle";
    public static final String packageI = "package";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_job_alert);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelpar =  new dbHelper(this);


        //Textview initialization
        book_idTxt = findViewById(R.id.book_id);
        typeTxt = findViewById(R.id.type);
        vehicleTxt = findViewById(R.id.vehcile);
        package_idTxt = findViewById(R.id.package_id);
        pickupTxt = findViewById(R.id.pickup);
        dropTxt = findViewById(R.id.drop);
        timeTxt = findViewById(R.id.timeat);

        //getting values from the sharedprefs
        shrp = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        book_id = shrp.getString(BOOK_ID, "0");
        type = shrp.getString(TYPE, "0");
        vehicle = shrp.getString(VEHICLE, "0");
        pickup = shrp.getString(PICKUP, "0");
        drop = shrp.getString(DROP, "0");
        time = shrp.getString(TIME, "0");
        package_type = shrp.getString(PACKAGE, "0");
        ori_lat = shrp.getString(ORI_LAT, "0");
        ori_lng = shrp.getString(ORI_LNG, "0");
        dest_lat = shrp.getString(DEST_LAT, "0");
        dest_lng = shrp.getString(DEST_LNG, "0");

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
        SharedPreferences.Editor editor = shrp.edit();
        editor.clear();
        editor.apply();
        finish();
    }

    public void gotoNavigation(View view) {

        ringtone.stop();

        dbHelpar.insertdata(book_id, time, type, vehicle, pickup, drop, package_type);

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(oriLata, ori_lat);
        intent.putExtra(oriLnga, ori_lng);
        intent.putExtra(desLata, dest_lat);
        intent.putExtra(desLnga, dest_lng);
        intent.putExtra(typeI, type);
        intent.putExtra(vehicleI, vehicle);
        intent.putExtra(packageI, package_type);

        startActivity(intent);
    }
}
