package mark1.gmt.rk_rabbitt.gmt_driver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
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

    SharedPreferences userpref;
//    TextView userTxt, useridTxt, phoneTxt, emailTxt;

    //get pref variables
    String user, idd, phone, emaill;

    //variable declaration rk
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(11.398194, 79.695358), new LatLng(11.916064, 79.812325));
    private static final int REQUEST_SELECT_PICK = 1000;
    public static final String MESSAGE = "OPTION";

    int pickP = 0, dropP = 0;

    Button rent_button, city_button, outstation_button;

    //map utils
    private GoogleMap mMap;
    ArrayList<LatLng> MarkerPoints;
    MarkerOptions options = new MarkerOptions();
    private Marker oriMarker, destMarker;
    LocationRequest mLocationRequest;
    LatLng origin, dest;
    //Type Variable says about rent or city or out
    String type;
    public static final String LOG_TAG = "MapsActivity";

    LinearLayout travel_type;

    LatLng oriLatlng;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        checkLocationPermission();

        //getting intent values
        Intent intent = getIntent();
        String oriLati = intent.getStringExtra(driverJob_alert.oriLata);
        String oriLngi = intent.getStringExtra(driverJob_alert.oriLnga);

        double oriLat = Double.parseDouble(oriLati);
        double oriLng = Double.parseDouble(oriLngi);

//        double oriLng = Double.parseDouble(intent.getStringExtra(driverJob_alert.oriLng));
//        double oriLng = Double.parseDouble(intent.getStringExtra(driverJob_alert.oriLng));

        oriLatlng = new LatLng(oriLat, oriLng);

        // Initializing
        MarkerPoints = new ArrayList<>();

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        turnOnGPS();
    }

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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {

            }
        });


        //Initialize Google Play Services
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
        options.position(latLng);

        //move map camerax
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

//      //  creating marker onload as staring
//        LatLng latLng1 = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptionsOri = new MarkerOptions();
//        markerOptionsOri.position(latLng1);
//        markerOptionsOri.title("Starting point");
//        markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//        oriMarker = mMap.addMarker(markerOptionsOri);
//        mMap.addMarker(markerOptionsOri).setDraggable(true);
//        MarkerPoints.add(0, latLng1);
//        //move map camera
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng1));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        animatePath(oriLatlng, latLng);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void checkLocationPermission() {
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

    public void addMarker(Place place) {

        try {

            if (pickP == 1) {
                if (oriMarker != null) {
                    oriMarker.remove();
                    MarkerPoints.remove(0);
                }

                MarkerOptions markerOptionsOri = new MarkerOptions();
                markerOptionsOri.position(place.getLatLng());
                markerOptionsOri.title("Starting point");
                markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                oriMarker = mMap.addMarker(markerOptionsOri);
                mMap.addMarker(markerOptionsOri).setDraggable(true);
                MarkerPoints.add(0, place.getLatLng());
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            } else {
                if (destMarker != null) {
                    destMarker.remove();
                    MarkerPoints.remove(1);
                }
                MarkerOptions markerOptionsDes = new MarkerOptions();
                markerOptionsDes.position(place.getLatLng());
                markerOptionsDes.title("Destination point");
                markerOptionsDes.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                destMarker = mMap.addMarker(markerOptionsDes);
                mMap.addMarker(markerOptionsDes).setDraggable(true);
                MarkerPoints.add(1, place.getLatLng());
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            }

            if (MarkerPoints.size() > 1) {
                origin = MarkerPoints.get(0);
                dest = MarkerPoints.get(1);
                Log.i(LOG_TAG, "origin marker: " + origin.toString());
                Log.i(LOG_TAG, "destination marker: " + dest.toString());

                origin = MarkerPoints.get(0);
                dest = MarkerPoints.get(1);

                animatePath(origin, dest);
            }


        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.i("Error in  ", e.getMessage());
            Toast.makeText(this, "Error in Search", Toast.LENGTH_SHORT).show();
        }
    }

    public void getRide(Place place) {

        try {

            if (pickP == 1) {
                if (oriMarker != null) {
                    oriMarker.remove();
                    MarkerPoints.remove(0);
                }

                MarkerOptions markerOptionsOri = new MarkerOptions();
                markerOptionsOri.position(place.getLatLng());
                markerOptionsOri.title("Starting point");
                markerOptionsOri.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                oriMarker = mMap.addMarker(markerOptionsOri);
                mMap.addMarker(markerOptionsOri).setDraggable(true);
                MarkerPoints.add(0, place.getLatLng());
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            } else {
                if (destMarker != null) {
                    destMarker.remove();
                    MarkerPoints.remove(1);
                }
                MarkerOptions markerOptionsDes = new MarkerOptions();
                markerOptionsDes.position(place.getLatLng());
                markerOptionsDes.title("Destination point");
                markerOptionsDes.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                destMarker = mMap.addMarker(markerOptionsDes);
                mMap.addMarker(markerOptionsDes).setDraggable(true);
                MarkerPoints.add(1, place.getLatLng());
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
            }

            if (MarkerPoints.size() > 1) {
                origin = MarkerPoints.get(0);
                dest = MarkerPoints.get(1);
                Log.i(LOG_TAG, "origin marker: " + origin.toString());
                Log.i(LOG_TAG, "destination marker: " + dest.toString());

                origin = MarkerPoints.get(0);
                dest = MarkerPoints.get(1);

                animatePath(origin, dest);
            }


        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.i("Error in  ", e.getMessage());
            Toast.makeText(this, "Error in Search", Toast.LENGTH_SHORT).show();
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

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void startTimerDistance() {

        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
    }

    public void decline(View view) {
        Toast.makeText(this, "Declined", Toast.LENGTH_SHORT).show();
        finish();
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
