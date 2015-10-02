package net.ottercove.hybernate;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AppNotificationService extends IntentService {
    public static final String DISABLE_NOTIFICATION = "net.ottercove.hybernate.action.disable_notification";
    public static final String LAUNCH_NOTIFICATION = "net.ottercove.hybernate.action.launch_notification";
    public static final String NAME = "net.ottercove.hybernate.extra.NAME";
    public static final String TITLE = "net.ottercove.hybernate.extra.TITLE";
    public static final String SUCCESS = "net.ottercove.hybernate.extra.SUCCESS";
    private NotificationManager notificationManager;

    public static void sendDisableNotification(Context context,
                                               String name,
                                               String title,
                                               Boolean success) {
        Intent intent = new Intent(context, AppNotificationService.class);
        intent.setAction(DISABLE_NOTIFICATION);
        intent.putExtra(NAME, name);
        intent.putExtra(TITLE, title);
        intent.putExtra(SUCCESS, success);
        context.startService(intent);

    }

    public static void sendLaunchNotification(Context context,
                                              String name,
                                              String title,
                                              Boolean success) {
        Intent intent = new Intent(context, AppNotificationService.class);
        intent.setAction(LAUNCH_NOTIFICATION);
        intent.putExtra(NAME, name);
        intent.putExtra(TITLE, title);
        intent.putExtra(SUCCESS, success);
        context.startService(intent);
    }

    public AppNotificationService() {
        super("AppNotification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (intent != null) {
            final String action = intent.getAction();
            if (DISABLE_NOTIFICATION.equals(action)) {
                final String title = intent.getStringExtra(TITLE);
                final Boolean success = intent.getBooleanExtra(SUCCESS, true);
                disableNotification(title, success);
            } else if (LAUNCH_NOTIFICATION.equals(action)) {
                final String name = intent.getStringExtra(NAME);
                final String title = intent.getStringExtra(TITLE);
                final Boolean success = intent.getBooleanExtra(SUCCESS, true);
                launchNotification(name, title, success);
            }
        }
        this.stopSelf();
    }

    private void disableNotification(String title, Boolean success) {
        if(success) {
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker("Disabled " + title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Disabled " + title)
                    .setContentText("The selected app was disabled.")
                    .setAutoCancel(true)
                    .build();

            notificationManager.cancel(0);
            notificationManager.notify(0, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker("Failed to disable " + title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Failed to disable " + title)
                    .setContentText("The selected app was not disabled.")
                    .setAutoCancel(true)
                    .build();

            notificationManager.cancel(0);
            notificationManager.notify(0, notification);
        }
    }

    private void launchNotification(String name, String title, Boolean success) {
        if(success) {
            Intent disable = new Intent(this, LaunchActivity.class);
            disable.setAction(LaunchAppService.DISABLE);
            disable.putExtra(LaunchAppService.NAME, name);
            disable.putExtra(LaunchAppService.TITLE, title);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, disable, 0);

            Notification notification = new NotificationCompat.Builder(this)
                    .setContentIntent(pendingIntent)
                    .setTicker("Launched " + title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Launched " + title)
                    .setContentText("Click here to disable.")
                    .setAutoCancel(true)
                    .build();

            int id = (int) System.currentTimeMillis() / 1000;
            notificationManager.notify(id, notification);
        } else {
            Notification notification = new NotificationCompat.Builder(this)
                    .setTicker("Failed to launch " + title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Failed to launch " + title)
                    .setContentText("The selected app was not started.")
                    .setAutoCancel(true)
                    .build();

            notificationManager.cancel(0);
            notificationManager.notify(0, notification);
        }
    }
}
