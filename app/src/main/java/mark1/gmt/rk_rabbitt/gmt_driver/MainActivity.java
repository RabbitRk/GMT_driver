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
        login.setSwitchPadding(40);
//        login.setChecked(true);

        productAdapter = new ArrayList<>();
        //code begins
        database = new dbHelper(this);

        productAdapter = database.getdata();

        recycler = new job_alert_adapter(productAdapter);

        Log.i("HIteshdata", "" + productAdapter);

        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        job_alert_recycler.setLayoutManager(reLayoutManager);
        job_alert_recycler.setItemAnimator(new DefaultItemAnimator());

        job_alert_recycler.setAdapter(recycler);

    }

    public void gotoMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
        {
            Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
        }


    }

//    @Override
//    public void onLocationChanged(Location location) {
//        LatLng userlatlang=new LatLng(location.getLatitude(),location.getLongitude());
//        Toast.makeText(this,userlatlang.toString(),Toast.LENGTH_SHORT).show();
//        Log.i("latlngof",userlatlang.toString());
//    }
}