package net.ottercove.hybernate;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by ottah on 9/26/15.
 */
public class SendNotification {

    public static void SingleNotification(String ticker,
                                          String title,
                                          String text,
                                          NotificationManager notificationManager,
                                          Context context) {
        Notification notification = new NotificationCompat.Builder(context)
                .setTicker(ticker)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .build();

        notificationManager.cancelAll();
        notificationManager.notify(0, notification);
    }
}


