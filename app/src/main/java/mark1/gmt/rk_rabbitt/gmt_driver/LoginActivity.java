package mark1.gmt.rk_rabbitt.gmt_driver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dx.dxloadingbutton.lib.LoadingButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mark1.gmt.rk_rabbitt.gmt_driver.Preferences.prefsManager;
import mark1.gmt.rk_rabbitt.gmt_driver.Utils.Config;


public class LoginActivity extends AppCompatActivity {


    private static final String LOG_TAG = "LoginActivity";
    private RequestQueue requestQueue;

    EditText password, phone_number;
    String passTxt, phoneTxt;
    String PuserTxt, PemailTxt, getId;
    Button lb;
    Boolean succes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //volley requestqueue initialization
        requestQueue = Volley.newRequestQueue(this);

        password = findViewById(R.id.confirm_pass);
        phone_number = findViewById(R.id.phonenumber);

        lb = findViewById(R.id.loading_btn);
        lb.setTypeface(Typeface.SERIF);
    }

    public void login(View view) {
        passTxt = password.getText().toString().trim();
        phoneTxt = phone_number.getText().toString().trim();

        if (TextUtils.isEmpty(phoneTxt)) {
            phone_number.setError("Enter the phone number");
            phone_number.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(passTxt)) {
            password.setError("Enter the password");
            password.requestFocus();
            return;
        }

        //Displaying a progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Registering", "Please wait...", false, false);

        //Again creating the string request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.USER_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Log.i(LOG_TAG, "Responce.............." + response);

                        try {
                            JSONArray arr = new JSONArray(response);
                            JSONObject jb = arr.getJSONObject(0);
                            getId = jb.getString("id");
                            PuserTxt = jb.getString("name");
                            PemailTxt = jb.getString("email");

                            setPrefsdetails();
                            loginto();

                        } catch (JSONException e) {
                            Log.i(LOG_TAG, "Json error.............." + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Log.i(LOG_TAG, "volley error.............................." + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Username or Phone number not found", Toast.LENGTH_LONG).show();
                    }
                })

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding the parameters to the request
                params.put("passWord", passTxt);
                params.put("phoneNumber", phoneTxt);

                return params;
            }
        };
        //Adding request the the queue
        requestQueue.add(stringRequest);
    }

    private void loginto() {
        Intent mainA = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainA);
        finish();
        Log.i(LOG_TAG, "Hid.............." + getId);
    }

    private void setPrefsdetails() {
        prefsManager prefsManager = new prefsManager(this);
        prefsManager.userPreferences(getId, PuserTxt, phoneTxt, PemailTxt);
        Log.i(LOG_TAG, "set preference Hid.............." + getId);
    }
}