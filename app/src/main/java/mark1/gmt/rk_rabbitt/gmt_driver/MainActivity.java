package mark1.gmt.rk_rabbitt.gmt_driver;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.List;

import mark1.gmt.rk_rabbitt.gmt_driver.DBHelper.dbHelper;
import mark1.gmt.rk_rabbitt.gmt_driver.DBHelper.recycleAdapter;
import mark1.gmt.rk_rabbitt.gmt_driver.Preferences.prefsManager;
import mark1.gmt.rk_rabbitt.gmt_driver.Utils.Config;

import static android.location.LocationManager.*;

/**
 * Created by Rabbitt on 30,January,2019
 */
public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, com.google.android.gms.location.LocationListener {

    private static final String LOG_TAG = "volley";
    SwitchCompat login;
    RecyclerView job_alert_recycler;
    dbHelper database;
    job_alert_adapter recycler;
    List<recycleAdapter> productAdapter;
    private RequestQueue requestQueue;
    String token, userid;
    prefsManager prefsManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting preferences
        prefsManager = new prefsManager(getApplicationContext());
        prefsManager.setFirstTimeLaunch(true);

        //switch buttom componenets
        login = findViewById(R.id.switch1);
        job_alert_recycler = findViewById(R.id.jobsRecycler);
        login.setOnCheckedChangeListener(this);
        login.setSwitchPadding(40);

        //getting shared prefs for login or logout
        SharedPreferences shrp = getSharedPreferences("USER_DETAILS",MODE_PRIVATE);
        String val = shrp.getString( "LOG_STATUS","");

        assert val != null;
        if (val.equals("1"))
            login.setChecked(true);
        else
            login.setChecked(false);

        //requestqueue for volley library
        requestQueue = Volley.newRequestQueue(this);

        //product apdapter for attended jobs
        productAdapter = new ArrayList<>();

        //code begins
        database = new dbHelper(this);

        productAdapter = database.getdata();

        //recycler goes on
        recycler = new job_alert_adapter(productAdapter);

        Log.i("HIteshdata", "" + productAdapter);

        RecyclerView.LayoutManager reLayoutManager = new LinearLayoutManager(getApplicationContext());
        job_alert_recycler.setLayoutManager(reLayoutManager);
        job_alert_recycler.setItemAnimator(new DefaultItemAnimator());

        job_alert_recycler.setAdapter(recycler);

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locManager.isProviderEnabled(NETWORK_PROVIDER);

        Location location;
//        LocationManger lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new com.google.android.gms.location.LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//                // TODO Auto-generated method stub
//            }
//        });
        if (network_enabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            location = locManager.getLastKnownLocation(NETWORK_PROVIDER);

            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String loc = "" + latitude + " ," + longitude + " ";
                Toast.makeText(this, loc, Toast.LENGTH_SHORT).show();
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng userlatlang = new LatLng(location.getLatitude(), location.getLongitude());
                Toast.makeText(MainActivity.this, "location changed", Toast.LENGTH_SHORT).show();
                Log.i("latlngof", userlatlang.toString());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

        tokenRegistration();
    }

    private void tokenRegistration() {
        SharedPreferences shrp = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        SharedPreferences shrpu = getSharedPreferences(Config.USER_PREFS, MODE_PRIVATE);

        token = shrp.getString("token", null);
        userid = shrpu.getString("ID_KEY",null);

        if ((token == null) || (userid == null)) {
            showAlert();
        } else {
            updateToken();
        }
    }

    private void updateToken() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.TOKEN_UPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.i("Responce......outside", response);

                if (response.equalsIgnoreCase("success")) {
                    Log.i("Responce....in", response);
                    Toast.makeText(getApplicationContext(), "Token updated Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("Responce.............", response);
                    Toast.makeText(getApplicationContext(), "Responce is  " + response, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", "volley response error");
                Toast.makeText(getApplicationContext(), "Responce error failed   " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("CUS_ID", userid);
                params.put("TOKEN", token);

                Log.i("LNG", userid);
                Log.i("LNG", token);
                return params;
            }
        };

        //inseting into  the iteluser table
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
        alertDialog.setTitle("Token is not Available");
        alertDialog.setMessage("Please contact Balaji");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void gotoMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Toast.makeText(this, "login", Toast.LENGTH_SHORT).show();
            prefsManager.status("1");
            statusUpdate("1");
        } else {
            Toast.makeText(this, "logout", Toast.LENGTH_SHORT).show();
            prefsManager.status("0");
            statusUpdate("0");
        }
    }

    public void statusUpdate(final String i) {
        //Again creating the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.STATUS_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i(LOG_TAG, "Responce.............." + response);

                        try {
                            if (response.equals("success")) {
                                Toast.makeText(MainActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Error in status update", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Log.i(LOG_TAG, ex.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, "volley error.............................." + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Cant connect to the server", Toast.LENGTH_LONG).show();
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put("DRIVERID", "101");
                params.put("LAT", "11.6542");
                params.put("LNG", "76.6542");
                params.put("ONDUTY", i);
                return params;
            }
        };

        //Adding request the the queue
        requestQueue.add(stringRequest);

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng userlatlang = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(this, userlatlang.toString(), Toast.LENGTH_SHORT).show();
        Log.i("latlngofseperate", userlatlang.toString());
    }

}