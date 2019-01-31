package mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Rabbitt on 30,January,2019
 */
public class firebaseMessengingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        NotificationCompat.Builder notificationBuilder = generateNotification.getBaseNotificationCompatBuilder(this, remoteMessage);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
