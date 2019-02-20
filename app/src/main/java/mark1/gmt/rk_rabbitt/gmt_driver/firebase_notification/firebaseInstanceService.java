package mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import mark1.gmt.rk_rabbitt.gmt_driver.Utils.Config;

public class firebaseInstanceService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        StoreinPrefs(refreshedToken);
        Log.d("firebaseService", "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void StoreinPrefs(String refreshedToken) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", refreshedToken);
        editor.apply();
        editor.commit();
    }

    private void sendRegistrationToServer(String refreshedToken) {
        Log.d("firebaseService", "Refreshed token: " + refreshedToken);
    }
}
