package mark1.gmt.rk_rabbitt.gmt_driver;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import mark1.gmt.rk_rabbitt.gmt_driver.DBHelper.dbHelper;
import mark1.gmt.rk_rabbitt.gmt_driver.DBHelper.recycleAdapter;

/**
 * Created by Rabbitt on 30,January,2019
 */
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    SwitchCompat login;
    RecyclerView job_alert_recycler;
    dbHelper database;
    job_alert_adapter recycler;
    List<recycleAdapter> productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.switch1);
        job_alert_recycler = findViewById(R.id.jobsRecycler);
        login.setOnCheckedChangeListener(this);

        productAdapter = new ArrayList<>();
        //code begins
        database = new dbHelper(this);

        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
        /*
        tvLatitud.setText("No se tienen permisos");
        ...
         */

            return;
        }else
        {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng userlatlang=new LatLng(location.getLatitude(),location.getLongitude());
            Toast.makeText(this,userlatlang.toString(),Toast.LENGTH_SHORT).show();
            Log.i("latlngof",userlatlang.toString());
        }

//        database.insertdata("book_id", "time", "type", "vehicle", "pickup", "drop", "package_type");
//        database.insertdata("book_id", "time", "type", "vehicle", "pickup", "drop", "package_type");
//        database.insertdata("book_id", "time", "type", "vehicle", "pickup", "drop", "package_type");
//        database.insertdata("book_id", "time", "type", "vehicle", "pickup", "drop", "package_type");
//        database.insertdata("book_id", "time", "type", "vehicle", "pickup", "drop", "package_type");
//        database.insertdata("book_id", "time", "type", "vehicle", "pickup", "drop", "package_type");
//        database.insertdata("book_id", "time", "type", "vehicle", "pickup", "drop", "package_type");
//        database.insertdata("book_id", "time", "type", "vehicle", "pickup", "drop", "package_type");
//        database.insertdata("book_id", "time", "type", "vehicle", "pickup", "drop", "package_type");

        productAdapter = database.getdata();

        recycler = new job_alert_adapter(productAdapter);

        Log.i("HIteshdata", "" + productAdapter);

        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        job_alert_recycler.setLayoutManager(reLayoutManager);
        job_alert_recycler.setItemAnimator(new DefaultItemAnimator());

        job_alert_recycler.setAdapter(recycler);

//        LocationManager myloc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        boolean network_enabled = myloc.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        Log.i("latlngof","er0");
//        if (network_enabled) {
//            Log.i("latlngof","er1");
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                Log.i("latlngof","error");
//                return;
//            }
//            Log.i("latlngof","er2");
//            Location my_location = myloc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            LatLng userLatlng=new LatLng(my_location.getLatitude(),my_location.getLongitude());
//            Log.i("latlngof",userLatlng.toString());
//            Toast.makeText(this,userLatlng.toString(),Toast.LENGTH_SHORT).show();
//        }
//

    }

    public void agreeJob(View view) {
        Intent maps = new Intent(this, MapsActivity.class);
        startActivity(maps);
    }

    public void gotoMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public void onLocationChanged(Location location) {
//        LatLng userlatlang=new LatLng(location.getLatitude(),location.getLongitude());
//        Toast.makeText(this,userlatlang.toString(),Toast.LENGTH_SHORT).show();
//        Log.i("latlngof",userlatlang.toString());
//    }
}