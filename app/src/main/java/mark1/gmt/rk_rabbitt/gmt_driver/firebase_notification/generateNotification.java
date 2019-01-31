package mark1.gmt.rk_rabbitt.gmt_driver.firebase_notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;

import mark1.gmt.rk_rabbitt.gmt_driver.MainActivity;
import mark1.gmt.rk_rabbitt.gmt_driver.R;

/**
 * Created by Rabbitt on 30,January,2019
*/

class generateNotification {

    static NotificationCompat.Builder getBaseNotificationCompatBuilder(Context context, RemoteMessage remoteMessage) {

        NotificationCompat.Style notificationStyle;
        NotificationCompat.BigTextStyle bigTextStyle;

        String message = remoteMessage.getData().get("message");
        String title = remoteMessage.getData().get("title");

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(message);
        notificationStyle = bigTextStyle;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setStyle(notificationStyle)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(title)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_EVENT);
            builder.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        return builder;
    }
}
