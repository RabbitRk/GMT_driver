package mark1.gmt.rk_rabbitt.gmt_driver;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mark1.gmt.rk_rabbitt.gmt_driver.Utils.Config;
import mark1.gmt.rk_rabbitt.gmt_driver.odometer.odometer;
import mark1.gmt.rk_rabbitt.gmt_driver.user_recognition.BackgroundDetectedActivitiesService;
import mark1.gmt.rk_rabbitt.gmt_driver.user_recognition.Constants;

/**
 * Created by Rabbitt on 01,February,2019
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {


    //global variables goes on
    final static int REQUEST_LOCATION = 199;
    private static final String MY_API_KEY = "AIzaSyD-6jHfmp3-P27H90-SO-qUi_gB33SiJw0";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    GoogleApiClient mGoogleApiClient;

    //map utils
    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    MarkerOptions options = new MarkerOptions();
    private Marker oriMarker, destMarker, userMarker;
    LocationRequest mLocationRequest;
    public static final String LOG_TAG = "MapsActivity";
    public static final String LOG = "rkRabbitt";

    LatLng oriLatlng, desLatlng, userLatlng;

    String oriLati;
    String oriLngi;
    String desLati;
    String desLngi;

    String type_, package_, vehicle_;
    private double distanceTraveledValue;
    TextView distance;
    LocationListener mloclisterner;

    ProgressBar progressBar;
    RequestQueue requestQueue;

    //track distance code starts here
    odometer odo;
    boolean bound;
    BroadcastReceiver broadcastReceiver;
    private final int PERMISSION_REQUEST_CODE = 698;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i(LOG, "oncreate");
        checkLocationPermission();

        // Initializing
        distance = findViewById(R.id.dist);
        MarkerPoints = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar_cyclic);

        //distance tracking code
        Intent intento = new Intent(this, odometer.class);
        bindService(intento, connection, Context.BIND_AUTO_CREATE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        View mapView = mapFragment.getView();
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 100);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(10, 10, 10, 10);
        }
        mapFragment.getMapAsync(this);

        mloclisterner = new MyLocationListener();
        //get Current Location on app launch

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        turnOnGPS();

        //getting intent values
        Intent intent = getIntent();
        oriLati = intent.getStringExtra(driverJob_alert.oriLata);
        oriLngi = intent.getStringExtra(driverJob_alert.oriLnga);
        desLati = intent.getStringExtra(driverJob_alert.desLata);
        desLngi = intent.getStringExtra(driverJob_alert.desLnga);

        type_ = intent.getStringExtra(driverJob_alert.typeI);
        vehicle_ = intent.getStringExtra(driverJob_alert.vehicleI);
        package_ = intent.getStringExtra(driverJob_alert.packageI);

        progressBar.setVisibility(View.VISIBLE);

        //distance tracking goes on
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };

        startTracking();

    }

//    private void currLoc() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//        {
//        /*
//        tvLatitud.setText("No se tienen permisos");
//        ...
//         */
//
//            return;
//        }else
//        {
//            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            userLatlng=new LatLng(location.getLatitude(),location.getLongitude());
//            Toast.makeText(this,userLatlng.toString(),Toast.LENGTH_SHORT).show();
//            Log.i("latlngof",userLatlng.toString());
//        }
//
//    }


    //convertions
    public void typeFinding(String oriLati, String oriLngi, String desLati, String desLngi, String type_, String package_, String vehicle_) {
        //converting string to double
        double oriLat = Double.parseDouble(oriLati);
        double oriLng = Double.parseDouble(oriLngi);
        double desLat = Double.parseDouble(desLati);
        double desLng = Double.parseDouble(desLngi);

        //converting double to latlng
        oriLatlng = new LatLng(oriLat, oriLng);
        desLatlng = new LatLng(desLat, desLng);

        switch (type_) {
            case "rental":
                rentalAnimator(oriLatlng);
                break;
            case "city":
                cityAnimator(oriLatlng, desLatlng);
                break;
            case "outstation":
                outstationAnimator(oriLatlng, desLatlng);
                break;
            default:
                Toast.makeText(this, "Can't get the Travel type", Toast.LENGTH_SHORT).show();
        }

        //distance calculation
        displayDistance();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            odometer.OdometerBinder odometerBinder = (odometer.OdometerBinder) binder;
            odo = odometerBinder.getOdometer();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    private void handleUserActivity(int type, int confidence) {

//        String label = getString(R.string.activity_unknown);
//        int icon = R.drawable.activity_still;

        boolean isRiding = false;
        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                isRiding = true;
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                isRiding = true;
                break;
            }
            case DetectedActivity.ON_FOOT: {
                isRiding = false;
                break;
            }
            case DetectedActivity.RUNNING: {
                isRiding = false;
                break;
            }
            case DetectedActivity.STILL: {
                isRiding = false;
                break;
            }
            case DetectedActivity.TILTING: {
                isRiding = true;
                break;
            }
            case DetectedActivity.WALKING: {
                isRiding = true;
                break;
            }
            case DetectedActivity.UNKNOWN: {
                isRiding = false;
                break;
            }
        }

        if (isRiding)
            displayDistance();

        Log.e("user recognition", "User activity" + confidence);

        if (confidence > Constants.CONFIDENCE) {
//            txtActivity.setText(label);
//            txtConfidence.setText("Confidence: " + confidence);
//            imgActivity.setImageResource(icon);
        }
    }

    private void startTracking() {
        Intent intent1 = new Intent(MapsActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
    }

    private void stopTracking() {
        Intent intent = new Intent(MapsActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
    }



    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,
                odometer.PERMISSION_STRING)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{odometer.PERMISSION_STRING},
                    PERMISSION_REQUEST_CODE);
        } else {
            Intent intent = new Intent(this, odometer.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    private void displayDistance() {

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double dist = 0.0;
                if (bound && odo != null) {
                    dist = odo.getDistance();
                }
                String distanceStr = String.format(Locale.getDefault(),
                        "%1$,.2f", distance);
                distance.setText(distanceStr);
                handler.postDelayed(this, 1000);
            }
        });
    }


    private void outstationAnimator(LatLng oriLatlng, LatLng desLatlng) {
        MarkerOptions markerOptionsOri = new MarkerOptions();
        markerOptionsOri.position(oriLatlng);
        markerOptionsOri.title("Starting point");
        markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        oriMarker = mMap.addMarker(markerOptionsOri);

        MarkerOptions markerOptionsDes = new MarkerOptions();
        markerOptionsDes.position(desLatlng);
        markerOptionsDes.title("Destination point");
        markerOptionsDes.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        destMarker = mMap.addMarker(markerOptionsDes);

        //calling polyline
        animatePath(oriLatlng, desLatlng);
        //calling zoomfuntion
        zoomout(oriMarker, destMarker);
    }

    private void cityAnimator(LatLng oriLatlng, LatLng desLatlng) {
        MarkerOptions markerOptionsOri = new MarkerOptions();
        markerOptionsOri.position(oriLatlng);
        markerOptionsOri.title("Starting point");
        markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        oriMarker = mMap.addMarker(markerOptionsOri);

        MarkerOptions markerOptionsDes = new MarkerOptions();
        markerOptionsDes.position(desLatlng);
        markerOptionsDes.title("Destination point");
        markerOptionsDes.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        destMarker = mMap.addMarker(markerOptionsDes);

        //calling polyline
        animatePath(oriLatlng, desLatlng);
        //calling zoomfuntion
        zoomout(oriMarker, destMarker);
    }

    public void rentalAnimator(LatLng oriLatlng) {

        MarkerOptions markerOptionsOri = new MarkerOptions();
        markerOptionsOri.position(oriLatlng);
        markerOptionsOri.title("Starting point");
        markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
//        oriMarker = mMap.addMarker(markerOptionsOri);
        mMap.addMarker(markerOptionsOri);

        Log.i(LOG_TAG, "   "+oriLatlng.toString());

        //calling polyline
        animatePath(userLatlng, oriLatlng);
        //calling zoomfuntion
    }

//    private void zoomout(Marker oriMarker) {
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
////the include method will calculate the min and max bound.
//        builder.include(oriMarker.getPosition());
//        builder.include(userMarker.getPosition());
//        builder.include(userMarker.getPosition());
//
//        LatLngBounds bounds = builder.build();
//
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
//
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
//
//        mMap.animateCamera(cu);
//    }

    private void turnOnGPS() {

        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();

        }
        if (!hasGPSDevice(this)) {
            Toast.makeText(this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Log.e("keshav", "Gps already enabled");
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        } else {
            Log.e("keshav", "Gps already enabled");
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mGoogleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MapsActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(LOG, "buildgoogleapiclient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(LOG,"mapready");
        mMap = googleMap;


        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {

            }
        });

//        Location mylocation = googleMap.getMyLocation();
//        Log.i(LOG_TAG, String.valueOf(mylocation.getLatitude() +"   "+mylocation.getLongitude()));
        //Initialize Google Play Services
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
//finding the type of the ride
//        typeFinding(oriLati, oriLngi, desLati, desLngi, type_, package_, vehicle_);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG, "locationchanged");

        //Place current location marker
        userLatlng = new LatLng(location.getLatitude(), location.getLongitude());
        Geocoder geocoder;
        List<Address> addresses;
        String address = "";
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Can't get Address", Toast.LENGTH_SHORT).show();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

//        pickupLocTxt.setText(address);
        options = new MarkerOptions();

        // Setting the position of the marker
        options.position(userLatlng);

        //move map camerax
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        Toast.makeText(this, "userlatlng..."+userLatlng.toString(), Toast.LENGTH_SHORT).show();
//      //  creating marker onload as staring
//        LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());

        if (userMarker!=null)
        {
            userMarker.remove();
        }

        MarkerOptions markerOptionsOri = new MarkerOptions();
        markerOptionsOri.position(userLatlng);
        markerOptionsOri.title("Your are here");
        markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        userMarker = mMap.addMarker(markerOptionsOri);
        mMap.addMarker(markerOptionsOri).setDraggable(true);
        MarkerPoints.add(0, userLatlng);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatlng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    public void zoomout(Marker oriMarker, Marker destMarker) {

        Log.i(LOG, "zooomout");

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        //the include method will calculate the min and max bound.
        builder.include(oriMarker.getPosition());
        builder.include(destMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.animateCamera(cu);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG, "connected");
        progressBar.setVisibility(View.GONE);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
//        typeFinding(oriLati, oriLngi, desLati, desLngi, type_, package_, vehicle_);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG, "onconsuspend");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG, "onconfailed");

    }

    public void checkLocationPermission() {
        Log.i(LOG, "checklocationpermission");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is neededx
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public void animatePath(LatLng origin, LatLng dest) {
        Log.i(LOG_TAG, "iam animate path");
        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url);
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        Toast.makeText(MapsActivity.this, "parameters " + parameters, Toast.LENGTH_SHORT).show();
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + MY_API_KEY;

        Toast.makeText(MapsActivity.this, "url " + url, Toast.LENGTH_SHORT).show();

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    public void getRide(View view) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Did you pick up the customer ?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startTimerDistance();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void startTimerDistance() {
        //fetch value from the database

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.RATE_CALCULATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.i(LOG_TAG, "Responce.............." + response);

                        try {
                            if (response.equals("success")) {
                                Toast.makeText(getApplicationContext(), "Status updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in status update", Toast.LENGTH_SHORT).show();
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
                params.put("TYPE", "101");
                params.put("VEHICLE", "11.6542");
                params.put("LNG", "76.6542");
                params.put("ONDUTY", "");
                return params;
            }
        };

        //Adding request the the queue
        requestQueue.add(stringRequest);

    }

    public class MyLocationListener implements LocationListener {
        /**
         * Used for calculating distance between current and previous
         * locations.
         */
        private Location previousLocation;

        @Override
        public void onLocationChanged(Location loc) {

            if (previousLocation != null) {
                if (previousLocation == loc) //If true, we have not moved.
                {
                    return;
                }
                //GPS has built-in
                else// (loc.distanceTo(previousLocation) > 1.0d)
                {
                    //distance defined using the WGS84 ellipsoid
                    // -- error < 2cm
                    distanceTraveledValue += Math.abs(loc.distanceTo(previousLocation));
                    previousLocation = new Location(loc);
                    distance.setText(Double.toString(((int) distanceTraveledValue)) + " meters");
                }
            } else // when PreviousLocation = null, we need to assign our first previousLocation;
            {
                previousLocation = new Location(loc);
            }
        }
    }

    public void decline(View view) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.decline_reason_radiogroup);
        dialog.setTitle("Reason to Cancelling");
        dialog.setCancelable(false);


    }

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    Toast.makeText(MapsActivity.this, "latitude " + lat + "....longitude " + lng, Toast.LENGTH_SHORT).show();

//RkDk
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                    Log.i(LOG_TAG, "Points....." + points.toString());
                }

                // Adding all the points in the route to LineOptions

                if (mMap != null) {
                    MapAnimator.getInstance().animateRoute(mMap, points);
                } else {
                    Toast.makeText(getApplicationContext(), "Map not ready", Toast.LENGTH_LONG).show();
                }

                Log.d("onPostExecute", "onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }
}
